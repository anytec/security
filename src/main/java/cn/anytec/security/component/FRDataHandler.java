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
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
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
    private TimeModel receiveTimeModel;

    public void setReceiveTimeModel(TimeModel receiveTimeModel) {
        this.receiveTimeModel = receiveTimeModel;
    }

    @PostConstruct
    private void initTimeModel(){
        LocalDateTime localDateTime = LocalDateTime.now();
        ZonedDateTime zonedDateTime= ZonedDateTime.of(localDateTime, ZoneId.systemDefault()).withZoneSameInstant(ZoneId.of("UTC"));
        receiveTimeModel = new TimeModel(zonedDateTime.toLocalDateTime());
    }

    @Scheduled(fixedDelay = 5000)
    public void sendSnapshotTimes() {
        if(checkTime(receiveTimeModel)){
            int hour = receiveTimeModel.getHour();
            int minute = receiveTimeModel.getMinute();
            long snapshot_start_timestamp = System.currentTimeMillis() - 1000 * 60 * (hour * 60 + minute);
            long snapshotOfDay = mongoDBService.getNumberOfSnapshotByTime(snapshot_start_timestamp, null);
            if (snapshotOfDay != -1) {
                wsSendHandler.sendSnapshotOfDay(snapshotOfDay);
            }
            long warningOfWeek = getWarningTimes(receiveTimeModel);
            String times = String.valueOf(new Long(warningOfWeek).intValue());
            redisTemplate.opsForValue().set(RedisConst.WARNNING_TIMES_OF_WEEK,times);
        }
    }

    public void recieveSnap(String cameraSdkId, String timestamp, String bbox, MultipartFile photo) {
        TbCamera camera = cameraService.getCameraBySdkId(cameraSdkId);
        if(camera != null){
            LocalDateTime localDateTime = LocalDateTime.parse(timestamp);
            TimeModel timeModel = new TimeModel(localDateTime);
            if(checkTime(timeModel)){
                this.receiveTimeModel = timeModel;
                List<IdentifyFace> faceList = addFace(photo,cameraSdkId,bbox);
                if (!CollectionUtils.isEmpty(faceList)) {
                    logger.info("【addFace】图片入sdk库成功");
                    faceList.forEach(face -> {
                        handleSnapshot(face, timeModel, camera);
                        IdentifyPojo identifyPojo = identifyInStaticGallery(photo,face.getBbox());
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
        logger.info("【快照处理】快照的获取时间：" + catchTime);
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
        snapshotAddition.put("date",timeModel.getDate());
        mongoDBService.addSnapShot(JSONObject.toJSONString(snapShot), snapshotAddition);
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



    //在静态库中identify
    public IdentifyPojo identifyInStaticGallery(MultipartFile photo, String bbox) {
        IdentifyPojo identifyPojo = null;
        FindFaceParam param = new FindFaceParam();
        Object thresholdObj = redisTemplate.opsForValue().get(warningThreshold);
        if(thresholdObj != null){
            param.setThreshold(thresholdObj.toString());
        }else {
            param.setThreshold(config.getWarningThreshold());
        }
        param.setGalleries(new String[]{config.getStaticGallery()});
        param.setBbox(bbox);
        param.setSdkIp(config.getStaticSdkIp());
        param.setSdkPort(config.getStaticSdkPort());
        param.setSdkVersion(config.getStaticSdkVersion());
        param.setSdkToken(config.getStaticSdkToken());
        try{
            identifyPojo = findFaceService.imageIdentify(photo.getBytes(), param);
        }catch (Exception e){
            e.printStackTrace();
        }
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
                try {
                    boolean warnningPush = handlePersonData(warning, matchFace);
                    //本周报警次数
                    Integer warnningOfWeek = Integer.parseInt(redisTemplate.opsForValue().get(RedisConst.WARNNING_TIMES_OF_WEEK).toString());
                    warning.setWarningOfWeek(warnningOfWeek);
                    if(warnningPush){
                        wsSendHandler.sendWarning(warning);
                    }
                    logger.info("预警记录存入mongo");
                    Map<String, Object> warningMap = new HashMap<>();
                    warningMap.put("timestamp", timeModel.getTimestamp());
                    warningMap.put("faceSdkId", matchFace.getFace().getId());
                    String age = face.getAge().toString().split("\\.")[0];
                    warningMap.put("age",age);
                    warningMap.put("emotions",face.getEmotions());
                    mongoDBService.addWarningFace(JSONObject.toJSONString(warning), warningMap);
                }catch (BussinessException e){

                }
            }
        });
    }

    //获取本周预警快照次数
    public long getWarningTimes(TimeModel timeModel) {
        int dayOfWeek = timeModel.getDayOfWeek();
        int hour = timeModel.getHour();
        int minute = timeModel.getMinute();
        long warnning_start_timestamp = System.currentTimeMillis() - 1000 * 60 * ((dayOfWeek - 1) * 24 * 60 + hour * 60 + minute);
        long warnningOfWeek = mongoDBService.getNumberOfWarningByTime(warnning_start_timestamp, null);
        return warnningOfWeek;
    }

    public void insertCameraData(FrWarning warning, TbCamera camera) {
        warning.setCameraId(camera.getId());
        warning.setCameraGroupId(camera.getGroupId());
        warning.setCameraSdkId(camera.getSdkId());
        warning.setCameraName(camera.getName());
        TbGroupCamera cameraGroup = groupCameraService.getGroupCameraById(camera.getGroupId().toString());
        if(cameraGroup != null){
            warning.setCameraGroupName(cameraGroup.getName());
        }else {
            logger.warn("{}获取设备组信息失败",camera.getName());
        }
    }

    //
    public boolean handlePersonData(FrWarning warning, MatchFace face) {
        boolean warnningPush = true;
        warning.setAge(face.getFace().getAge().intValue());
        warning.setEmotions(face.getFace().getEmotions());
        String faceSdkId = face.getFace().getId();
        TbPerson person = personService.getPersonBySdkId(faceSdkId).getData();
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
                if(personGroup.getWarnningPush() == 0){
                    warnningPush = false;
                }
            }
        }else {
            logger.error("库中未录入person,faceSdkId: " + faceSdkId);
            throw new BussinessException(1,"库中未录入person,faceSdkId:{}",faceSdkId);
        }
        return warnningPush;
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
        }else {
            return ServerResponse.createByErrorMessage("图片检测异常或未检测到人脸");
        }
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
