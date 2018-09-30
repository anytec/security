package cn.anytec.security.component;

import cn.anytec.security.common.ServerResponse;
import cn.anytec.security.component.ipcamera.ipcService.IPCOperations;
import cn.anytec.security.config.GeneralConfig;
import cn.anytec.security.constant.RedisConst;
import cn.anytec.security.core.exception.BussinessException;
import cn.anytec.security.findface.FindFaceService;
import cn.anytec.security.findface.model.*;
import cn.anytec.security.model.TbCamera;
import cn.anytec.security.model.TbGroupCamera;
import cn.anytec.security.model.TbGroupPerson;
import cn.anytec.security.model.TbPerson;
import cn.anytec.security.model.parammodel.FindFaceParam;
import cn.anytec.security.model.parammodel.IdenfitySnapParam;
import cn.anytec.security.component.mongo.MongoDBService;
import cn.anytec.security.service.CameraService;
import cn.anytec.security.service.GroupCameraService;
import cn.anytec.security.service.GroupPersonService;
import cn.anytec.security.service.PersonService;
import cn.anytec.security.model.websocketmodel.FdSnapShot;
import cn.anytec.security.model.websocketmodel.FrWarning;
import cn.anytec.security.model.parammodel.TimeModel;
import cn.anytec.security.websocket.WSSendHandler;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;


/**
 * Created by zhao on 2018/7/19.
 */
@Component
public class FRDataHandler {
    private static final Logger logger = LoggerFactory.getLogger(FRDataHandler.class);
    @Autowired
    GeneralConfig config;
    @Autowired
    private FindFaceService findFaceService;
    @Autowired
    private MongoDBService mongoDBService;
    @Autowired
    private PersonService personService;
    @Autowired
    private GroupPersonService groupPersonService;
    @Autowired
    private CameraService cameraService;
    @Autowired
    private GroupCameraService groupCameraService;
    @Autowired
    private WSSendHandler wsSendHandler;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private IPCOperations ipcOperations;

    private String warningThreshold = RedisConst.WARNING_THRESHOLD;

    public void recieveSnap(String cameraSdkId, String timestamp, String bbox, MultipartFile photo) {
        TbCamera camera = cameraService.getCameraBySdkId(cameraSdkId);
        if(camera != null){
            LocalDateTime localDateTime = LocalDateTime.parse(timestamp);
            TimeModel timeModel = new TimeModel(localDateTime);
            if(checkTime(timeModel)){
                List<IdentifyFace> faceList = addFace(photo,cameraSdkId,bbox);
                if (!CollectionUtils.isEmpty(faceList)) {
                    faceList.forEach(face -> {
                        handleSnapshot(face, timeModel, camera);
                        IdentifyPojo identifyPojo = identifyInStaticGallery(face.getThumbnail());
                        if (identifyPojo != null) {
                            handleWarningSnap(identifyPojo,face, timeModel, camera);
                        }
                    });
                }
            }
        }else {
            logger.info("【获取camera失败】cameraSdkId:{},快照不入库",cameraSdkId);
            try{
                ipcOperations.standbyCaptureCamera(cameraSdkId);
            }catch (BussinessException e){
                logger.error("【获取ip失败】cameraSdkId: {}",cameraSdkId);
            }
        }
    }

    /*检查快照传入时间是否符合当前时间**/
    public boolean checkTime(TimeModel timeModel){
        long nowTimestamp = System.currentTimeMillis();
        long timestamp = timeModel.getTimestamp();
        if(timestamp > nowTimestamp){
            if((timestamp - nowTimestamp)/(1000*60) <30){
                return true;
            }
            logger.info("【receiveSnap】快照传入时间{},大于当前时间30分钟，快照不录入",timeModel.getCatchTime());
        }else if(timestamp <= nowTimestamp){
            if((nowTimestamp - timestamp)/(1000*60) <30){
                return true;
            }
            logger.info("【receiveSnap】快照传入时间{},小于当前时间30分钟，快照不录入",timeModel.getCatchTime());
        }
        return false;
    }


    //快照入sdk动态库
    public List<IdentifyFace> addFace(MultipartFile photo, String cameraSdkId, String bbox) {
        FindFaceParam params = new FindFaceParam();
        params.setMeta(cameraSdkId);
        params.setBbox(bbox);
        params.setSdkIp(config.getSnapSdkIp());
        params.setSdkPort(config.getSnapSdkPort());
        params.setSdkVersion(config.getSnapSdkVersion());
        params.setSdkToken(config.getSnapSdkToken());
        String[] galleries = new String[]{config.getSnapGallery()};
        params.setGalleries(galleries);
        try {
            FacePojo facePojo = findFaceService.addFace(photo.getBytes(), params);
            if (facePojo != null) {
                return facePojo.getResults();
            } else {
                logger.info("图片入动态库失败！");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    //快照处理
    public void handleSnapshot(IdentifyFace face, TimeModel timeModel, TbCamera camera) {
        String catchTime = timeModel.getCatchTime();
        logger.info("快照获取：" + catchTime);
        //快照推送
        FdSnapShot snapShot = new FdSnapShot();
        List<String> emotions = face.getEmotions();
        snapShot.setCatchTime(catchTime);
        snapShot.setSnapshotUrl(face.getThumbnail());
        snapShot.setCameraName(camera.getName());
        snapShot.setEmotions(emotions);
        snapShot.setGender(face.getGender());
        Integer age = Integer.parseInt(face.getAge().toString().split("\\.")[0]);
        snapShot.setAge(age);
        snapShot.setFaceSdkId(face.getId());
        snapShot.setWholePhoto(face.getPhoto());
        wsSendHandler.sendSnapShot(snapShot, camera.getSdkId());
        //快照存入mongo
        Map<String, Object> snapshotAddition = insertCameraData(camera);
        if(!CollectionUtils.isEmpty(emotions)){
            snapshotAddition.put("firstEmotion",emotions.get(0));
            snapshotAddition.put("secondEmotion",emotions.get(1));
        }
        snapshotAddition.put("timestamp", timeModel.getTimestamp());
        String day =timeModel.getCatchTime().split(" ")[0];
        snapshotAddition.put("day",day);
        String time = timeModel.getCatchTime().split(" ")[1];
        Integer timeRange = getTimeRange(time);
        if(timeRange != null){
            snapshotAddition.put("timeRange",timeRange);
        }
        mongoDBService.addSnapShot(JSONObject.toJSONString(snapShot), snapshotAddition);
        //今日抓拍数推送
        sendSnapshotTimes(timeModel);
    }

    public Map<String, Object> insertCameraData(TbCamera camera) {
        Map<String, Object> map = new HashMap<>();
        map.put("cameraId", camera.getId());
        map.put("cameraSdkId",camera.getSdkId());
        map.put("cameraGroupId", camera.getGroupId());
        map.put("location",camera.getLocation());
        //map.put("location", returnLocation());
        return map;
    }

    //临时方法，到时候以真正的camera的location为准
    public String returnLocation() {
        List<String> locationList = new ArrayList<>();
        //深圳坐标,目前用于在线
        /*locationList.add("114.056215,22.539968");
        locationList.add("114.062931,22.542524");
        locationList.add("114.050121,22.524132");
        locationList.add("114.104366,22.546171");*/
        //北京坐标,目前用于离线
      /*  locationList.add("39.907001,116.391378");
        locationList.add("39.900153,116.397901");
        locationList.add("39.901733,116.40554");
        locationList.add("39.902326,116.420045");*/
        Integer i = new Random().nextInt(locationList.size());
        return locationList.get(i);
    }

    private Integer getTimeRange(String time){
        Integer timeOclock = Integer.parseInt(time.split(":")[0]);
        if(timeOclock <2){
            return 1;
        }else if(timeOclock >= 2 && timeOclock < 4 ){
            return 2;
        }else if(timeOclock >= 4 && timeOclock < 6 ){
            return 3;
        }else if(timeOclock >= 6 && timeOclock < 8 ){
            return 4;
        }else if(timeOclock >= 8 && timeOclock < 10 ){
            return 5;
        }else if(timeOclock >= 10 && timeOclock < 12 ){
            return 6;
        }else if(timeOclock >= 12 && timeOclock < 14 ){
            return 7;
        }else if(timeOclock >= 14 && timeOclock < 16 ){
            return 8;
        }else if(timeOclock >= 16 && timeOclock < 18 ){
            return 9;
        }else if(timeOclock >= 18 && timeOclock < 20 ){
            return 10;
        }else if(timeOclock >= 20 && timeOclock < 22 ){
            return 11;
        }else if(timeOclock >= 22 && timeOclock < 24 ){
            return 12;
        }
        return null;
    }

    //推送今日抓拍快照次数
    public void sendSnapshotTimes(TimeModel timeModel) {
        int hour = timeModel.getHour();
        int minute = timeModel.getMinute();
        long snapshot_start_timestamp = System.currentTimeMillis() - 1000 * 60 * (hour * 60 + minute);
        long snapshotOfDay = mongoDBService.getNumberOfSnapshotByTime(snapshot_start_timestamp, null);
        if (snapshotOfDay != -1) {
            wsSendHandler.sendSnapshotOfDay(snapshotOfDay);
        }
    }

    //在静态库中identify
    public IdentifyPojo identifyInStaticGallery(String faceUrl) {
        IdentifyPojo identifyPojo = null;
        FindFaceParam param = new FindFaceParam();
        Object thresholdObj = redisTemplate.opsForValue().get(warningThreshold);
        if(thresholdObj != null){
            param.setThreshold(thresholdObj.toString());
        }else {
            param.setThreshold(config.getWarningThreshold());
        }
        param.setGalleries(new String[]{config.getStaticGallery()});
        param.setPhotoUrl(faceUrl);
        param.setSdkIp(config.getStaticSdkIp());
        param.setSdkPort(config.getStaticSdkPort());
        param.setSdkVersion(config.getStaticSdkVersion());
        param.setSdkToken(config.getStaticSdkToken());
        identifyPojo = findFaceService.imageIdentify(null, param);
        return identifyPojo;
    }

    //处理预警的快照
    public void handleWarningSnap(IdentifyPojo identifyPojo, IdentifyFace face, TimeModel timeModel, TbCamera camera) {
        identifyPojo.getResults().forEach((k, matchFaces) -> {
            if (!CollectionUtils.isEmpty(matchFaces)) {
                logger.info("预警警告！ Time：" + timeModel.getCatchTime());
                MatchFace matchFace = matchFaces.get(0);
                FrWarning warning = new FrWarning(face.getThumbnail(), matchFace.getFace().getThumbnail(),face.getPhoto());
                warning.setConfidence(matchFace.getConfidence());
                warning.setCatchTime(timeModel.getCatchTime());
                insertCameraData(warning, camera);
                insertPersonData(warning, matchFace);
                //本周报警次数
                long warningOfWeek = getWarningTimes(timeModel);
                warning.setWarningOfWeek(new Long(warningOfWeek).intValue());
                wsSendHandler.sendWarning(warning);
                logger.info("预警记录存入mongo");
                Map<String, Object> warningMap = new HashMap<>();
                warningMap.put("timestamp", timeModel.getTimestamp());
                warningMap.put("faceSdkId", matchFace.getFace().getId());
                String age = face.getAge().toString().split("\\.")[0];
                warningMap.put("age",age);
                warningMap.put("emotions",face.getEmotions());
                mongoDBService.addWarningFace(JSONObject.toJSONString(warning), warningMap);
            }
        });
    }

    //获取本周预警快照次数
    public long getWarningTimes(TimeModel timeModel) {
        int dayOfWeek = timeModel.getDayOfWeek();
        int hour = timeModel.getHour();
        int minute = timeModel.getMinute();
        long warning_start_timestamp = System.currentTimeMillis() - 1000 * 60 * ((dayOfWeek - 1) * 24 * 60 + hour * 60 + minute);
        long warningOfWeek = mongoDBService.getNumberOfWarningByTime(warning_start_timestamp, null);
        return warningOfWeek;
    }

    public void insertCameraData(FrWarning warning, TbCamera camera) {
        warning.setCameraId(camera.getId());
        warning.setCameraGroupId(camera.getGroupId());
        warning.setCameraSdkId(camera.getSdkId());
        warning.setCameraName(camera.getName());
        TbGroupCamera cameraGroup = groupCameraService.getGroupCameraById(camera.getId().toString());
        if(cameraGroup != null){
            warning.setCameraGroupName(cameraGroup.getName());
        }
    }

    public void insertPersonData(FrWarning warning, MatchFace face) {
        warning.setAge(face.getFace().getAge().intValue());
        warning.setEmotions(face.getFace().getEmotions());
        TbPerson person = personService.getPersonBySdkId(face.getFace().getId()).getData();
        if (person != null) {
            warning.setPersonName(person.getName());
            warning.setGender(person.getGender());
            warning.setPersonGroupId(person.getGroupId());
            warning.setIdNumber(person.getIdNumber());
            warning.setFaceSdkId(person.getSdkId());
            TbGroupPerson personGroup = groupPersonService.getGroupPersonById(person.getGroupId().toString()).getData();
            if(personGroup != null){
                warning.setColorLabel(personGroup.getColorLabel());
                warning.setPersonGroupName(personGroup.getName());
                warning.setVoiceLabel(personGroup.getVoiceLabel());
            }
        }
    }

    //在动态库中搜索快照
    public ServerResponse identifySnap(IdenfitySnapParam idenfitySnapParam) {
        JSONObject result = new JSONObject();
        FindFaceParam findFaceParam = getFindFaceParam(idenfitySnapParam);
        IdentifyPojo identifyPojo = identify(idenfitySnapParam, findFaceParam);
        if(identifyPojo != null){
            Map<String, String> sdkMap = getSdkIdConfidenceMap(identifyPojo);
            if (sdkMap.size() > 0) {
                result = mongoDBService.identifySnap(sdkMap, idenfitySnapParam);
            }
            return ServerResponse.createBySuccess(result);
        }
        return ServerResponse.createByError();
    }

    public FindFaceParam getFindFaceParam(IdenfitySnapParam param){
        FindFaceParam findFaceParam = new FindFaceParam();
        //confidence
        String confidence =param.getConfidence();
        if(!StringUtils.isEmpty(confidence)){
            double confidenceValue = Double.parseDouble(confidence);
            if(confidenceValue > 1){
                confidenceValue = confidenceValue/100;
            }
            findFaceParam.setThreshold(String.valueOf(confidenceValue));
        }
        //photoUrl
        String photoUrl = param.getPhotoUrl();
        if(!StringUtils.isEmpty(photoUrl)){
            findFaceParam.setPhotoUrl(photoUrl);
        }
        if(StringUtils.isEmpty(param.getIdentifyNumber())){
            findFaceParam.setN(config.getSnapIdentifyNumber());
        }else {
            findFaceParam.setN(param.getIdentifyNumber());
        }
        findFaceParam.setGalleries(new String[]{config.getSnapGallery()});
        findFaceParam.setSdkIp(config.getSnapSdkIp());
        findFaceParam.setSdkPort(config.getSnapSdkPort());
        findFaceParam.setSdkVersion(config.getSnapSdkVersion());
        findFaceParam.setSdkToken(config.getSnapSdkToken());
        return findFaceParam;
    }

    public IdentifyPojo identify(IdenfitySnapParam idenfitySnapParam, FindFaceParam findFaceParam){
        IdentifyPojo identifyPojo = null;
        MultipartFile photo = idenfitySnapParam.getPhoto();
        String photoUrl = idenfitySnapParam.getPhotoUrl();
        if(photo != null){
            try {
                identifyPojo = findFaceService.imageIdentify(photo.getBytes(),findFaceParam);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else if(!StringUtils.isEmpty(photoUrl)){
            identifyPojo = findFaceService.imageIdentify(null,findFaceParam);
        }else {
            logger.info("identifySnap没有传入photo文件或photoUrl");
        }
        return identifyPojo;
    }

    public Map<String, String> getSdkIdConfidenceMap(IdentifyPojo identifyPojo){
        Map<String, String> sdkMap = new HashMap<>();
        identifyPojo.getResults().keySet().forEach((key) -> {
            List<MatchFace> matchFaces = identifyPojo.getResults().get(key);
            if (matchFaces.size() > 0) {
                for (MatchFace face : matchFaces) {
                    sdkMap.put(face.getFace().getId(), face.getConfidence() + "");
                }
            }
        });
        return sdkMap;
    }
}
