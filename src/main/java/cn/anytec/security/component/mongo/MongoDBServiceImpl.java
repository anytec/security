package cn.anytec.security.component.mongo;

import cn.anytec.security.config.GeneralConfig;
import cn.anytec.security.model.TbCamera;
import cn.anytec.security.model.TbGroupCamera;
import cn.anytec.security.model.parammodel.IdenfitySnapParam;
import cn.anytec.security.service.CameraService;
import cn.anytec.security.service.GroupCameraService;
import com.alibaba.fastjson.JSONObject;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

<<<<<<< HEAD
import java.text.ParsePosition;
=======
import javax.annotation.PostConstruct;
>>>>>>> upstream/master
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

@Service
public class MongoDBServiceImpl implements MongoDBService {
    @Autowired
    GeneralConfig config;
    @Autowired
    private CameraService cameraService;
    @Autowired
    private GroupCameraService groupCameraService;

    private static final Logger logger = LoggerFactory.getLogger(MongoDBServiceImpl.class);

    @Value("${mongo.host}")
    private String host;
    @Value("${mongo.databaseNames}")
    private String databaseName;
    @Value("${mongo.snapshotCollection}")
    private String configSnapshot;
    @Value("${mongo.warningFaceCollection}")
    private String configWarning_face;

    private  MongoClient mongoClient;
    private  MongoDatabase database;
    private  MongoCollection<Document> snapshotCollection;
    private  MongoCollection<Document> warningFaceCollection;

    private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

    public MongoDBServiceImpl() {
        logger.info("======= 初始化MongoDB =======");
    }

    @PostConstruct
    public void init(){
        mongoClient = new MongoClient(host);
        database = mongoClient.getDatabase(databaseName);
        snapshotCollection = database.getCollection(configSnapshot);
        warningFaceCollection = database.getCollection(configWarning_face);
    }
    //快照入库
    @Override
    public void addSnapShot(String json, Map<String, Object> map) {
        Document document = Document.parse(json);
        document.putAll(map);
        snapshotCollection.insertOne(document);
    }

    ///报警人脸入库
    @Override
    public void addWarningFace(String json, Map<String, Object> map) {
        Document document = Document.parse(json);
        document.putAll(map);
        warningFaceCollection.insertOne(document);
    }

    //获取快照列表
    public JSONObject getSnapshotList(Map<String, String[]> paramMap) {
        Integer pageNum = 0;
        Integer pageSize = 10;
        if (paramMap.containsKey("pageNum")) {
            pageNum = Integer.parseInt(paramMap.get("pageNum")[0]) - 1;
            if (pageNum < 0) {
                pageNum = 0;
            }
        }
        if (paramMap.containsKey("pageSize")) {
            pageSize = Integer.parseInt(paramMap.get("pageSize")[0]);
        }

        BasicDBObject dbObject = getTimeDBObject(paramMap);
        //cameraGroupId条件
        if (paramMap.containsKey("cameraGroupId")) {
            Integer cameraGroupId = Integer.parseInt(paramMap.get("cameraGroupId")[0]);
            if (cameraGroupId != 0) {
                dbObject.put("cameraGroupId", cameraGroupId);
            }
        }
        //cameraSdkId条件
        insertCameraQuery(paramMap,dbObject);
        if (paramMap.containsKey("faceSdkId")) {
            String faceSdkId = paramMap.get("faceSdkId")[0];
            if (!StringUtils.isEmpty(faceSdkId)) {
                dbObject.put("faceSdkId", faceSdkId);
            }
        }
        List<JSONObject> dataList = getResultJson(snapshotCollection.find(dbObject).skip(pageNum * pageSize).limit(pageSize).sort(new BasicDBObject("timestamp", -1)));
        insertCameraData(dataList);
        Integer count = Integer.parseInt(snapshotCollection.count(dbObject) + "");
        JSONObject result = new JSONObject();
        result.put("list", dataList);
        result.put("total", count);
        return result;
    }

    //获取报警人脸列表
    public JSONObject getWarningFaceList(Map<String, String[]> paramMap) {
        Integer pageNum = 0;
        Integer pageSize = 10;
        if (paramMap.containsKey("pageNum")) {
            pageNum = Integer.parseInt(paramMap.get("pageNum")[0]) - 1;
            if (pageNum < 0) {
                pageNum = 0;
            }
        }
        if (paramMap.containsKey("pageSize")) {
            pageSize = Integer.parseInt(paramMap.get("pageSize")[0]);
        }

        BasicDBObject dbObject = getTimeDBObject(paramMap);

        //personName条件
        if (paramMap.containsKey("personName")) {
            String personName = paramMap.get("personName")[0];
            if (!StringUtils.isEmpty(personName)) {
                Pattern personNamePattern = Pattern.compile("^.*" + personName + ".*$");
                dbObject.put("personName", personNamePattern);
            }
        }
        //personGroup条件
        if (paramMap.containsKey("personGroupId")) {
            Integer personGroupId = 0;
            personGroupId = Integer.parseInt(paramMap.get("personGroupId")[0]);
            if (personGroupId != 0) {
                dbObject.put("personGroupId", personGroupId);
            }
        }
        //gender条件
        if (paramMap.containsKey("gender")) {
            String gender = paramMap.get("gender")[0];
            if (!StringUtils.isEmpty(gender)) {
                dbObject.put("gender", gender);
            }
        }
        //idNumber条件
        if (paramMap.containsKey("idNumber")) {
            String idNumber = paramMap.get("idNumber")[0];
            if (!StringUtils.isEmpty(idNumber)) {
                Pattern idNumberPattern = Pattern.compile("^.*" + idNumber + ".*$");
                dbObject.put("idNumber", idNumberPattern);
            }
        }
        //cameraSdkId条件
        insertCameraQuery(paramMap,dbObject);
        //cameraGroupId条件
        if (paramMap.containsKey("cameraGroupId")) {
            Integer cameraGroupId = Integer.parseInt(paramMap.get("cameraGroupId")[0]);
            if (cameraGroupId != 0) {
                dbObject.put("cameraGroupId", cameraGroupId);
            }
        }
        List<JSONObject> dataList = null;
        dataList = getResultJson(warningFaceCollection.find(dbObject).skip(pageNum * pageSize).limit(pageSize).sort(new BasicDBObject("timestamp", -1)));
        insertCameraData(dataList);
        Integer count = Integer.parseInt(warningFaceCollection.count(dbObject) + "");
        JSONObject result = new JSONObject();
        result.put("list", dataList);
        result.put("total", count);
        return result;
    }

    private void insertCameraQuery(Map<String, String[]> paramMap, BasicDBObject dbObject){
        if (paramMap.containsKey("cameraSdkIds")) {
            String cameraSdkIdStr = paramMap.get("cameraSdkIds")[0];
            String[] cameraSdkIds = cameraSdkIdStr.split(",");
            List<String> cameraSdkIdList = Arrays.asList(cameraSdkIds);
            dbObject.put("cameraSdkId", new BasicDBObject("$in", cameraSdkIdList));
            dbObject.remove("cameraGroupId");
        }
    }

    private void insertCameraData(List<JSONObject> dataList) {
        for (JSONObject data : dataList) {
            String cameraSdkId = data.getString("cameraSdkId");
            if(!StringUtils.isEmpty(cameraSdkId)){
                TbCamera camera = cameraService.getCameraBySdkId(cameraSdkId);
                data.put("cameraName", camera.getName());
                data.put("cameraGroupName", camera.getGroupName());
            }
        }
    }

    //快照搜索
    public JSONObject identifySnap(Map<String, String> sdkMap, IdenfitySnapParam idenfitySnapParam) {
        JSONObject result = new JSONObject();
        BasicDBObject dbObject = new BasicDBObject();
        String format = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        //sdkId条件
        if (sdkMap.size() > 0) {
            BasicDBList sdkIds = new BasicDBList();
            sdkMap.forEach((k, v) -> {
                sdkIds.add(k);
            });
            dbObject.put("faceSdkId", new BasicDBObject("$in", sdkIds));
        }

        BasicDBList groupIds = new BasicDBList();
        BasicDBList camIds = new BasicDBList();
        //cameraGroup条件
        String cameraGroupIds = idenfitySnapParam.getCameraGroupIds();
        if (!StringUtils.isEmpty(cameraGroupIds)) {
            String[] cameraGroupIdList = cameraGroupIds.split(",");
            if (cameraGroupIdList.length > 0) {
                for (String groupId : cameraGroupIdList) {
                    groupIds.add(Integer.parseInt(groupId));
                }
            }
        }
        //camera条件
        String cameraIds = idenfitySnapParam.getCameraIds();
        if (!StringUtils.isEmpty(cameraIds)) {
            String[] cameraIdList = cameraIds.split(",");
            if (cameraIdList.length > 0) {
                for (int i = 0; i < cameraIdList.length; i++) {
                    camIds.add(Integer.parseInt(cameraIdList[i]));
                }
            }
        }
        //camera和cameraGroup条件
        if (!StringUtils.isEmpty(cameraGroupIds) && !StringUtils.isEmpty(cameraIds)) {
            BasicDBList cameraQuery = new BasicDBList();
            BasicDBObject cameraObj = new BasicDBObject();
            BasicDBObject cameraGroupObj = new BasicDBObject();
            cameraGroupObj.put("cameraGroupId", new BasicDBObject("$in", groupIds));
            cameraObj.put("cameraId", new BasicDBObject("$in", camIds));
            cameraQuery.add(cameraObj);
            cameraQuery.add(cameraGroupObj);
            dbObject.put("$or", cameraQuery);
        } else {
            if (camIds.size() > 0) {
                dbObject.put("cameraId", new BasicDBObject("$in", camIds));
            }
            if (groupIds.size() > 0) {
                dbObject.put("cameraGroupId", new BasicDBObject("$in", groupIds));
            }
        }
        List<JSONObject> dataList = getResultJson(snapshotCollection.find(dbObject).sort(new BasicDBObject("timestamp", -1)));
        for (JSONObject data : dataList) {
            String sdkId = data.get("faceSdkId").toString();
            if (sdkMap.containsKey(sdkId)) {
                data.put("confidence", sdkMap.get(sdkId));
            }
            String cameraSdkId = data.getString("cameraSdkId");
            if(!StringUtils.isEmpty(cameraSdkId)){
                TbCamera camera = cameraService.getCameraBySdkId(cameraSdkId);
                data.put("cameraName", camera.getName());
                data.put("cameraGroupName", camera.getGroupName());
            }
        }
        Integer count = Integer.parseInt(snapshotCollection.count(dbObject) + "");
        result.put("list", dataList);
        result.put("total", count);
        return result;
    }

    public JSONObject sanpCounting(){
        JSONObject result = new JSONObject();
        Integer snapCount = Integer.parseInt(snapshotCollection.count() + "");
        Integer warningCount = Integer.parseInt(warningFaceCollection.count() + "");
        result.put("snapCount",snapCount);
        result.put("warningCount",warningCount);
        return result;
    }


    public JSONObject peopleCounting(Map<String, String[]> paramMap) {
        Map<String, Map<String, List<BasicDBObject>>> dayCameraTimeMap = getDayCameraTimeMap(paramMap);
        JSONObject result = new JSONObject();
        dayCameraTimeMap.forEach((day, value) -> {
            List<JSONObject> dayCameraCountList = new ArrayList<>();
            value.forEach((cammeraName, timeDbObjectList) -> {
                JSONObject cameraCountList = new JSONObject();
                List<Integer> countList = new ArrayList<>();
                countList.add(0);
                Integer total = 0;
                for (BasicDBObject timeDbObject : timeDbObjectList) {
                    Integer count = Integer.parseInt(snapshotCollection.count(timeDbObject) + "");
                    countList.add(count);
                    total += count;
                }
                countList.add(total);
                cameraCountList.put(cammeraName, countList);
                dayCameraCountList.add(cameraCountList);
            });
            result.put(day, dayCameraCountList);
        });
        return result;
    }

    //按日期,设备,mongo时间查询条件封装好的map
    private Map<String, Map<String, List<BasicDBObject>>> getDayCameraTimeMap(Map<String, String[]> paramMap){
        Map<String, Map<String, List<BasicDBObject>>> dayCameraTimeMap = new HashMap<>();
        List<String> dayList = getPastWeek(paramMap);
        List<TbGroupCamera> cameraGroupList = getCameraGroupList(paramMap);
        List<TbCamera> cameraList = getCameraList(paramMap);
        for (String day : dayList) {
            Map<String, List<BasicDBObject>> cameraTimeDbObjectMap = new HashMap<>();
            if(cameraGroupList.size() > 0){
                cameraTimeDbObjectMap = getCameraGroupTimeMap(cameraGroupList,paramMap,day);
            }else if(cameraList.size() > 0){
                cameraTimeDbObjectMap = getCameraTimeMap(cameraList,paramMap,day);
            }
            dayCameraTimeMap.put(day, cameraTimeDbObjectMap);
        }
        return dayCameraTimeMap;
    }

    private List<String> getPastWeek(Map<String, String[]> paramMap) {
        List<String> dayList = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        if(paramMap.containsKey("date")){
            String dateStr = paramMap.get("date")[0];
            ParsePosition pos = new ParsePosition(0);
            Date date = format.parse(dateStr,pos);
            calendar.setTime(date);
        }
        for (int i = 0; i < 7; i++) {
            if(i != 0){
                calendar.add(Calendar.DATE, -1);
            }
            Date date = calendar.getTime();
            String day = format.format(date);
            dayList.add(day);
        }
        return dayList;
    }

    private List<TbCamera> getCameraList(Map<String, String[]> paramMap) {
        List<TbCamera> cameraList = new ArrayList<>();
        if (paramMap.containsKey("cameraSdkIds")) {
            String cameraSdkIds = paramMap.get("cameraSdkIds")[0];
            String[] sdkIds = cameraSdkIds.split(",");
            for(String cameraSdkId : sdkIds){
                if(!StringUtils.isEmpty(cameraSdkId)){
                    TbCamera camera = cameraService.getCameraBySdkId(cameraSdkId);
                    cameraList.add(camera);
                }
            }
        }else {
            cameraList = cameraService.list(1,5,null,null,null,null,null);
        }
        return cameraList;
    }

    private Map<String, List<BasicDBObject>> getCameraTimeMap(List<TbCamera> cameraList, Map<String, String[]> paramMap, String day ){
        Map<String, List<BasicDBObject>> cameraTimeMap = new HashMap<>();
        for (TbCamera camera : cameraList) {
            List<BasicDBObject> cameraTimeDbList = new ArrayList<>();
            List<BasicDBObject> timePointList = getTimePointList(paramMap, day);
            for (BasicDBObject dbObject : timePointList) {
                dbObject.put("cameraSdkId", camera.getSdkId());
                cameraTimeDbList.add(dbObject);
            }
            cameraTimeMap.put(camera.getName(), cameraTimeDbList);
        }
        return cameraTimeMap;
    }

    private List<TbGroupCamera> getCameraGroupList(Map<String, String[]> paramMap) {
        List<TbGroupCamera> cameraGroupIdList = new ArrayList<>();
        if (paramMap.containsKey("cameraGroupIds")) {
            String cameraGroupIds = paramMap.get("cameraGroupIds")[0];
            String[] groupIds = cameraGroupIds.split(",");
            for(String groupId : groupIds){
                TbGroupCamera cameraGroup = groupCameraService.getGroupCameraById(groupId);
                cameraGroupIdList.add(cameraGroup);
            }
        }
        return cameraGroupIdList;
    }

    private Map<String, List<BasicDBObject>> getCameraGroupTimeMap(List<TbGroupCamera> cameraGroupList, Map<String, String[]> paramMap, String day ){
        Map<String, List<BasicDBObject>> cameraGroupTimeMap = new HashMap<>();
        for (TbGroupCamera cameraGroup : cameraGroupList) {
            List<BasicDBObject> cameraGroupTimeDbList = new ArrayList<>();
            List<BasicDBObject> timePointList = getTimePointList(paramMap, day);
            for (BasicDBObject dbObject : timePointList) {
                dbObject.put("cameraGroupId", cameraGroup.getId());
                cameraGroupTimeDbList.add(dbObject);
            }
            cameraGroupTimeMap.put(cameraGroup.getName(), cameraGroupTimeDbList);
        }
        return cameraGroupTimeMap;
    }

    private List<BasicDBObject> getTimePointList(Map<String, String[]> paramMap, String today) {
        String[] timeArray = {today + " 00:00:00", today + " 02:00:00", today + " 04:00:00",
                today + " 06:00:00", today + " 08:00:00", today + " 10:00:00", today + " 12:00:00",
                today + " 14:00:00", today + " 16:00:00", today + " 18:00:00", today + " 20:00:00",
                today + " 20:00:00", today + " 22:00:00", today + " 23:59:59"};
        List<BasicDBObject> dayObjectList = new ArrayList<>();
        for (int i = 0; i < timeArray.length - 2; i++) {
            BasicDBObject dbObject = new BasicDBObject();
            Long start = convertTime(timeArray[i]);
            Long end = convertTime(timeArray[i + 1]);
            dbObject = getTimestampDBObject(start, end);
            dbObject = putCameraCondition(paramMap, dbObject);
            dayObjectList.add(dbObject);
        }
        return dayObjectList;
    }

    public JSONObject peopleAnalysis(Map<String, String[]> paramMap) {
        JSONObject result = new JSONObject();
        result.put("age",getAgeAnalysis(paramMap));
        result.put("gender",getGenderAnalysis(paramMap));
        result.put("emotions",getEmotionAnalysis(paramMap));
        return result;
    }

    private BasicDBObject getAnalysisDbObject(Map<String, String[]> paramMap){
        BasicDBObject dbObject = getTimeDBObject(paramMap);
        BasicDBList cameraGroupIdDbList = new BasicDBList();
        BasicDBList cameraSdkIdDbList = new BasicDBList();
        if (paramMap.containsKey("cameraGroupIds")) {
            String cameraGroupIds = paramMap.get("cameraGroupIds")[0];
            String[] groupIds = cameraGroupIds.split(",");
            for(String groupId : groupIds){
                cameraGroupIdDbList.add(Integer.parseInt(groupId));
            }
        }
        if (paramMap.containsKey("cameraSdkIds")) {
            String cameraSdkIds = paramMap.get("cameraSdkIds")[0];
            String[] sdkIds = cameraSdkIds.split(",");
            for(String cameraSdkId : sdkIds){
                cameraSdkIdDbList.add(cameraSdkId);
            }
        }
        if(cameraGroupIdDbList.size() > 0){
            dbObject.put("cameraGroupId", new BasicDBObject("$in", cameraGroupIdDbList));
        }else if(cameraSdkIdDbList.size() > 0){
            dbObject.put("cameraSdkId", new BasicDBObject("$in", cameraSdkIdDbList));
        }
        return dbObject;
    }


    private Map<String,List<Integer>> getAgeAnalysis(Map<String, String[]> paramMap){
        BasicDBObject basicDBObject = getAnalysisDbObject(paramMap);
        Map<String,List<Integer>> ageMap = new HashMap<>();
        Integer[] ages = {0,15,36,61,91};
        for(int i=0; i<ages.length-1; i++){
            BasicDBObject dbObject = basicDBObject;
            dbObject.put("age", new BasicDBObject().
                    append("$gte", ages[i]).
                    append("$lt", ages[i+1]));
            dbObject.put("gender","male");
            Integer maleCount = Integer.parseInt(snapshotCollection.count(dbObject)+"");
            dbObject.put("gender","female");
            Integer femaleCount = Integer.parseInt(snapshotCollection.count(dbObject)+"");
            Integer total = maleCount + femaleCount;
            List<Integer> countList = new ArrayList<>();
            countList.add(maleCount);
            countList.add(femaleCount);
            countList.add(total);
            String key = ages[i]+" ~ "+(ages[i+1]-1);
            ageMap.put(key,countList);
        }
        return ageMap;
    }

    private Map<String,Integer> getGenderAnalysis(Map<String, String[]> paramMap){
        BasicDBObject basicDBObject = getAnalysisDbObject(paramMap);
        Map<String,Integer> genderMap = new HashMap<>();
            String[] genders = {"male","female"};
            for(String gender : genders) {
                BasicDBObject dbObject = basicDBObject;
                dbObject.put("gender",gender);
                Integer count = Integer.parseInt(snapshotCollection.count(dbObject)+"");
                genderMap.put(gender,count);
            }
        return genderMap;
    }

    private Map<String,String> getEmotionAnalysis(Map<String, String[]> paramMap){
        BasicDBObject firstObj = getAnalysisDbObject(paramMap);
        BasicDBObject secondObj = getAnalysisDbObject(paramMap);
        Map<String,String> emotionMap = new HashMap<>();
        String[] emotions = {"neutral","sad","happy","surprise","fear","angry","disgust"};
        for(String emotion : emotions){
            BasicDBObject firstEmotion = firstObj;
            BasicDBObject secondEmotion = secondObj;
            firstEmotion.put("firstEmotion",emotion);
            secondEmotion.put("secondEmotion",emotion);
            Integer firstEmotinCount = Integer.parseInt(snapshotCollection.count(firstEmotion)+"");
            Integer secondEmotinCount = Integer.parseInt(snapshotCollection.count(secondEmotion)+"");
            emotionMap.put(emotion,firstEmotinCount+","+secondEmotinCount);
        }
        return emotionMap;
    }

    @Override
    public long getNumberOfWarningByTime(Long start_timestamp, Long end_timestamp) {

        BasicDBObject dbObject = getTimestampDBObject(start_timestamp, end_timestamp);
        if (dbObject.size() > 0) {
            return warningFaceCollection.count(dbObject);
        }
        return -1;
    }

    @Override
    public long getNumberOfSnapshotByTime(Long start_timestamp, Long end_timestamp) {
        BasicDBObject dbObject = getTimestampDBObject(start_timestamp, end_timestamp);
        if (dbObject.size() > 0) {
            return snapshotCollection.count(dbObject);
        }
        return -1;
    }

    private BasicDBObject getTimeDBObject(Map<String, String[]> paramMap) {
        String startTime = "";
        String endTime = "";
        if (paramMap.containsKey("startTime")) {
            startTime = paramMap.get("startTime")[0];
        }
        if (paramMap.containsKey("endTime")) {
            endTime = paramMap.get("endTime")[0];
        }
        Long start = convertTime(startTime);
        Long end = convertTime(endTime);
        return getTimestampDBObject(start, end);
    }

    private BasicDBObject putCameraCondition(Map<String, String[]> paramMap, BasicDBObject dbObject) {
        //cameraGroup条件
        if (paramMap.containsKey("cameraGroupId")) {
            Integer cameraGroupId = 0;
            cameraGroupId = Integer.parseInt(paramMap.get("cameraGroupId")[0]);
            if (cameraGroupId != 0) {
                dbObject.put("cameraGroupId", cameraGroupId);
            }
        }
        //cameraSdkId条件
        if (paramMap.containsKey("cameraSdkIds")) {
            String cameraSdkIdStr = paramMap.get("cameraSdkIds")[0];
            String[] cameraSdkIds = cameraSdkIdStr.split(",");
            List<String> cameraSdkIdList = new ArrayList<>();
            cameraSdkIdList = Arrays.asList(cameraSdkIds);
            dbObject.put("cameraSdkId", new BasicDBObject("$in", cameraSdkIdList));
            dbObject.remove("cameraGroupId");
        }
        return dbObject;
    }


    private BasicDBObject getTimestampDBObject(Long start_timestamp, Long end_timestamp) {
        BasicDBObject dbObject = new BasicDBObject();
        if (start_timestamp != null && end_timestamp != null) {
            if (start_timestamp > end_timestamp)
                return null;
            dbObject.put("timestamp", new BasicDBObject().append("$gte", start_timestamp).append("$lte", end_timestamp));
        } else if (start_timestamp != null) {
            dbObject.put("timestamp", new BasicDBObject().append("$gte", start_timestamp));
        } else if (end_timestamp != null) {
            dbObject.put("timestamp", new BasicDBObject().append("$lte", end_timestamp));
        }
        return dbObject;
    }

    private Long convertTime(String timeStr) {
        try {
            if (!StringUtils.isEmpty(timeStr)) {
                if (timeStr.length() == 13 || timeStr.length() == 10) {
                    return Long.parseLong(timeStr);
                } else if (timeStr.contains("GMT")) {
                    return new Date(timeStr).getTime();
                } else {
                    String format = "yyyy-MM-dd HH:mm:ss";
                    SimpleDateFormat sdf = new SimpleDateFormat(format);
                    return sdf.parse(timeStr).getTime();
                }
            }
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    private List<JSONObject> getResultJson(FindIterable<Document> documents) {
        List<JSONObject> result = new ArrayList<>();
        Iterator<Document> iterator = documents.iterator();
        while (iterator.hasNext()) {
            Document document = iterator.next();
            JSONObject jsonObject = null;
            jsonObject = JSONObject.parseObject(document.toJson());
            result.add(jsonObject);
        }
        return result;
    }

}
