package cn.anytec.security.component.mongo;

import cn.anytec.security.config.GeneralConfig;
import cn.anytec.security.constant.RedisConst;
import cn.anytec.security.model.TbCamera;
import cn.anytec.security.model.TbGroupCamera;
import cn.anytec.security.model.parammodel.IdenfitySnapParam;
import cn.anytec.security.service.CameraService;
import cn.anytec.security.service.GroupCameraService;
import cn.anytec.security.service.GroupPersonService;
import cn.anytec.security.util.DateTimeUtil;
import cn.anytec.security.util.SearchUtil;
import com.alibaba.fastjson.JSONObject;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.client.*;
import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;


@Service
public class MongoDBServiceImpl implements MongoDBService {
    @Autowired
    GeneralConfig config;
    @Autowired
    private CameraService cameraService;
    @Autowired
    private GroupCameraService groupCameraService;
    @Autowired
    private GroupPersonService groupPersonService;
    @Autowired
    private RedisTemplate redisTemplate;

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
    private String personCounting = RedisConst.PERSON_COUNTING;

    public MongoDBServiceImpl() {
        logger.info("======= 初始化MongoDB =======");
    }

    @PostConstruct
    public void init(){
        MongoClientOptions.Builder build = new MongoClientOptions.Builder();
        build.connectionsPerHost(100);
        MongoClientOptions myOptions = build.build();
        mongoClient = new MongoClient(host,myOptions);
        database = mongoClient.getDatabase(databaseName);
        snapshotCollection = database.getCollection(configSnapshot);
        warningFaceCollection = database.getCollection(configWarning_face);
        java.util.logging.Logger.getLogger("org.mongodb.driver").setLevel(java.util.logging.Level.INFO);

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
        insertCameraQuery(paramMap,dbObject);
        //faceSdkId条件
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
        //faceSdkId条件
        if (paramMap.containsKey("faceSdkId")) {
            String faceSdkId = paramMap.get("faceSdkId")[0];
            if (!StringUtils.isEmpty(faceSdkId)) {
                dbObject.put("faceSdkId", faceSdkId);
            }
        }
        //cameraGroupId条件
        if (paramMap.containsKey("cameraGroupId")) {
            Integer cameraGroupId = Integer.parseInt(paramMap.get("cameraGroupId")[0]);
            if (cameraGroupId != 0) {
                dbObject.put("cameraGroupId", cameraGroupId);
            }
        }
        //cameraSdkId条件
        insertCameraQuery(paramMap,dbObject);
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
            //to do 不要循环发请求
            String cameraSdkId = data.getString("cameraSdkId");
            if(!StringUtils.isEmpty(cameraSdkId)){
                TbCamera camera = cameraService.getCameraBySdkId(cameraSdkId);
                if(camera != null){
                    data.put("cameraName", camera.getName());
                    TbGroupCamera cameraGroup = groupCameraService.getGroupCameraById(camera.getGroupId().toString());
                    if(cameraGroup != null){
                        data.put("cameraGroupName", cameraGroup.getName());
                    }
                }
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
        Map<String,Integer> cameraSnapCount = new HashMap<>();
        for (JSONObject data : dataList) {
            String sdkId = data.get("faceSdkId").toString();
            if (sdkMap.containsKey(sdkId)) {
                data.put("confidence", sdkMap.get(sdkId));
            }
            String cameraSdkId = data.getString("cameraSdkId");
            if(!StringUtils.isEmpty(cameraSdkId)){
                TbCamera camera = cameraService.getCameraBySdkId(cameraSdkId);
                if(camera != null){
                    data.put("cameraName", camera.getName());
                    data.put("cameraStatus",camera.getCameraStatus());
                    TbGroupCamera cameraGroup = groupCameraService.getGroupCameraById(camera.getGroupId().toString());
                    if(cameraGroup != null){
                        data.put("cameraGroupName", cameraGroup.getName());
                    }
                }
            }
            if(cameraSnapCount.containsKey(cameraSdkId)){
                data.put("snapCount",Integer.parseInt(cameraSnapCount.get(cameraSdkId).toString()));
            }else {
                BasicDBObject cameraDbObject = new BasicDBObject();
                cameraDbObject.put("cameraSdkId",cameraSdkId);
                Integer snapCount = Integer.parseInt(snapshotCollection.count(cameraDbObject)+"");
                data.put("snapCount",snapCount);
                cameraSnapCount.put(cameraSdkId,snapCount);
            }
        }
        Integer count = Integer.parseInt(snapshotCollection.count(dbObject) + "");
        result.put("list", dataList);
        result.put("total", count);
        return result;
    }

    public JSONObject snapCounting(){
        JSONObject result = new JSONObject();
        Integer snapCount = Integer.parseInt(snapshotCollection.count() + "");
        Integer warningCount = Integer.parseInt(warningFaceCollection.count() + "");
        result.put("snapCount",snapCount);
        result.put("warningCount",warningCount);
        return result;
    }

    private Document getMatchForParam(Map<String, String[]> paramMap) {
        Document sub_match = new Document();

        if (paramMap.containsKey("cameraSdkIds")){
            String cameraSdkIdStr = paramMap.get("cameraSdkIds")[0];
            String[] cameraSdkIds = cameraSdkIdStr.split(",");
            sub_match.put("cameraSdkId", new Document("$in", Arrays.asList(cameraSdkIds)));
        }
        if (paramMap.containsKey("cameraGroupIds")){
            String cameraGroupIdStr = paramMap.get("cameraGroupIds")[0];
            String[] cameraGroupIds = cameraGroupIdStr.split(",");
            List<Integer> cameraGroupIdList = new ArrayList<>();
            for(String cameraGroupId : cameraGroupIds){
                cameraGroupIdList.add(Integer.parseInt(cameraGroupId));
            }
            sub_match.put("cameraGroupId", new Document("$in", cameraGroupIdList));
        }
        return sub_match;
    }

    public JSONObject peopleCountingV2(Map<String, String[]> paramMap) {

        String format = "yyyy-MM-dd'T'HH:mm:ss'Z'";
        final int past = 7;

        Document sub_match = getMatchForParam(paramMap);
        Date[] daysAgoAndToday = DateTimeUtil.getDaysAgoAndToday(paramMap, past, format);
        Date daysAgo = daysAgoAndToday[0];
        Date today = daysAgoAndToday[1];
        sub_match.put("date", new Document("$gte", daysAgo)
                .append("$lte", today));

        Document sub_group = new Document();
        sub_group.put("_id", "$cameraSdkId");
        sub_group.put("count", new Document("$sum", 1));
        sub_group.put("cameraName", new Document("$first", "$cameraName"));
        sub_group.put("timestamp", new Document("$push", "$timestamp"));

        Document match = new Document("$match", sub_match);
        Document group = new Document("$group", sub_group);
        Document sort = new Document("$sort", new Document("_id", 1).append("cameraName", 1));

        ArrayList<Document> aggregateList = new ArrayList<>();
        aggregateList.add(match);
        aggregateList.add(group);
        aggregateList.add(sort);

        AggregateIterable<Document> aggregate = snapshotCollection.aggregate(aggregateList);

        ArrayList<Map<String, Map<String, Object>>> ret = new ArrayList<>();

        boolean flag = false;
        if(!sub_match.containsKey("cameraSdkId")&&!sub_match.containsKey("cameraGroupIds")){
            flag= true;
        }
        try (MongoCursor<Document> iterator = aggregate.iterator()) {
            Integer count = 0;
            while (iterator.hasNext()) {
                if(flag){
                    if(count >4){
                        break;
                    }
                    count++;
                }
                Document item = iterator.next();

                HashMap<String, Map<String, Object>> keyMap = new HashMap<>();
                HashMap<String, Object> valueMap = new HashMap<>();
                List<Long> timestampList = null;
                try {
                    timestampList = item.get("timestamp", List.class);
                } catch (Exception e) {
                    logger.error(e.getMessage());
                    throw new SecurityException("获取时间参数异常.");
                }


                String cameraName = item.getString("cameraName");
//                String cameraSdkId = item.getString("_id");
                if (null == cameraName) {
                    continue;
                }

                int[] arrCountIndex = new int[past * 12];

                List<Long> timestamps = DateTimeUtil.generateTimestamp(past, today);

                // oldIndex 为timestampList上一次for循环的索引位置.
                int index, oldIndex = 0;
                Assert.notEmpty(timestampList, "未成功获取统计参数.");
                for (int i = 1; i < timestamps.size(); i++) {

                    int indexOf = timestampList.indexOf(timestamps.get(i));
                    // 精确查找
                    if (indexOf != -1) {
                        index = indexOf;
                        // 模糊查询
                    } else {
                        // 找出无限接近于(或者等于)某一天的(0:00/2:00/4:00/6:00...22:00)的时间戳的抓拍记录
                        index = SearchUtil.searchKey(timestampList, timestamps.get(i), true);
                        /*if (index <= 0) {
                        }*/

                        // 如果接近值查找拿到的最接近23:59:59的时间戳是00:00:00.而不是23:59:58, 通过判断进行纠错.
                        if (timestampList.get(index) >= timestamps.get(i)) {
                            continue;
                        }

                        /*
                         * 容错率==10, 避免同一时间段推送多条数据.二分查找拿到第一条数据就返回,如后续还有相等数据.此位置用于纠错
                         * 不要调太大. 计算量次数等于    N(设备数量) * past(天) * 12 * 10
                         */
                        for (int j = 1; j <= 10; j++) {
                            if (index < timestampList.size() - 1) {
                                if (!timestampList.get(index + 1).equals(timestampList.get(index))) {
                                    break;
                                }
                                index += 1;
                            }
                        }
                    }

                    label3:
                    for (int j = oldIndex; j <= index; j++) {

                        SimpleDateFormat hhSdf = new SimpleDateFormat("HH");
                        Integer hours = Integer.valueOf(hhSdf.format(new Date(timestampList.get(j))));

                        // 当天数据各个时间段的分布
                        int arrIndex;
                        if (hours % 2 == 0) {
                            arrIndex = hours / 2;
                        } else {
                            arrIndex = (hours - 1) / 2;
                        }
                        arrCountIndex[arrIndex + (12 * (i - 1))] += 1;
                    }
                    if (index >= timestampList.size() - 1) {
                        break;
                    }
                    // 下一次 label3 循环从本次已经筛选过的时间点的后一个timeList开始.
                    oldIndex = index + 1;
                }

                valueMap.put("count", item.getInteger("count", 0));
                valueMap.put("countIndex", arrCountIndex);

                keyMap.put(cameraName, valueMap);
                ret.add(keyMap);
            }
        }
        // 汇总功能
        JSONObject strings = DateTimeUtil.countTime(daysAgo, today, past, ret);

        return strings;
    }


    public JSONObject peopleCounting(Map<String, String[]> paramMap) {

        JSONObject maps = null;
        try {
            maps = peopleCountingV2(paramMap);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        if (maps != null) {
            return maps;
        }

        Map<String, Map<String, List<BasicDBObject>>> dayCameraTimeMap = getDayCameraTimeMap(paramMap);
        JSONObject result = new JSONObject();
        dayCameraTimeMap.forEach((day, value) -> {
            Map<String,List<Integer>> cameraCountMap = new HashMap<>();
            value.forEach((cameraName, timeDbObjectList) -> {
                List<Integer> countList = new ArrayList<>();
                String key = "";
                if(paramMap.containsKey("cameraGroupIds")){
                    key = day+",camGroup:"+cameraName;
                }else {
                    key = day+",cam:"+cameraName;
                }
                if (redisTemplate.opsForHash().hasKey(personCounting, key)) {
                    String countStr =  redisTemplate.opsForHash().get(personCounting, key).toString();
                    String[] counts = countStr.split(",");
                    for(String count : counts){
                        countList.add(Integer.parseInt(count));
                    }
                }else {
                    String countStr = "0,";
                    countList.add(0);
                    Integer total = 0;
                    for (BasicDBObject timeDbObject : timeDbObjectList) {
                        Integer count = Integer.parseInt(snapshotCollection.count(timeDbObject) + "");
                        countList.add(count);
                        countStr += count+",";
                        total += count;
                    }
                    countList.add(total);
                    countStr += total+"";
                    if(!day.equals(getToday())){
                        redisTemplate.opsForHash().put(personCounting, key, countStr);
                        redisTemplate.expire(personCounting, 1, TimeUnit.DAYS);
                    }
                }
                cameraCountMap.put(cameraName, countList);
            });
            result.put(day, cameraCountMap);
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

    private String getToday(){
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        return format.format(date);
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
            cameraList = cameraService.list(1,5,null,null,null,null,null,null);
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

        JSONObject jsonObject = null;
        try {
            jsonObject = peopleAnalysisV2(paramMap);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        if (jsonObject != null) {
            return jsonObject;
        }

        JSONObject result = new JSONObject();
        result.put("age",getAgeAnalysis(paramMap));
        result.put("gender",getGenderAnalysis(paramMap));
        result.put("emotions",getEmotionAnalysis(paramMap));
        return result;
    }

    private JSONObject peopleAnalysisV2(Map<String, String[]> paramMap) {

        Document sub_match = getMatchForParam(paramMap);

        String[] startTimes, endTimes;
        if (paramMap.containsKey("startTime") && paramMap.containsKey("endTime")) {
            startTimes = paramMap.get("startTime");
            endTimes = paramMap.get("endTime");
            long startTime, endTime;
            // 默认取前台传的时间区间
            if (startTimes.length == 1 && endTimes.length == 1) {
                startTime = Long.valueOf(startTimes[0]);
                endTime = Long.valueOf(endTimes[0]);
                sub_match.put("date", new Document("$gte", new Date(startTime))
                        .append("$lte", new Date(endTime)));
            }
        }

        Document sub_group = new Document();
        sub_group.put("_id", new Document("gender", "$gender").append("age", "$age").append("emotions", "$emotions"));
        sub_group.put("count", new Document("$sum", 1));

        Document sub_project = new Document();
        sub_project.put("_id", 0);
        sub_project.put("count", 1);
        sub_project.put("age", "$_id.age");
        sub_project.put("gender", "$_id.gender");
        sub_project.put("emotions", "$_id.emotions");


        Document match = new Document("$match", sub_match);
        Document group = new Document("$group", sub_group);
        Document sort = new Document("$sort", new Document("age", 1));
        Document project = new Document("$project", sub_project);

        ArrayList<Document> aggregateList = new ArrayList<>();
        aggregateList.add(match);
        aggregateList.add(group);
        aggregateList.add(sort);
        aggregateList.add(project);

        AggregateIterable<Document> aggregate = snapshotCollection.aggregate(aggregateList);

        Map<String,List<Integer>> ageMap = new HashMap<>();
        // 年龄段
        Integer[] generation = {0,15,36,61,91};
        // 情绪
        String[] emotionList = {"neutral","sad","happy","surprise","fear","angry","disgust"};

        // 结构化年龄汇总信息
        int[][] ages = new int[4][3];
        // 结构化性别汇总信息  1--男, 0--女
        int[] genders = new int[2];
        // 结构化情绪汇总信息
        int[][] emotions = new int[7][2];

        for (Document item : aggregate) {

            Integer age = item.getInteger("age");
            String gender = item.getString("gender");
            ArrayList emotionArr = item.get("emotions", ArrayList.class);
            Integer count = item.getInteger("count");

            // 通过循环年龄段,构建年龄和性别
            for (int j = 0; j < generation.length; j++) {
                if (generation[j] <= age && age < generation[j + 1]) {
                    if ("male".equals(gender)) {
                        ages[j][1] += count;
                        genders[1] += count;
                    }else {
                        ages[j][0] += count;
                        genders[0] += count;
                    }
                    ages[j][2] += count;
                }
            }
            // 通过循环情绪,构建情绪信息
            for (int i = 0; i < emotionList.length; i++) {

                Assert.isTrue(emotionArr.size() == 2, "超出情绪总数(显性情绪和隐性情绪)");
                for (int j = 0; j < emotionArr.size(); j++) {
                    if (emotionList[i].equals(emotionArr.get(j))) {
                        emotions[i][j] += count;
                    }else if (emotionList[i].equals(emotionArr.get(j))){
                        emotions[i][j] += count;
                    }
                }
            }
        }

        // 构建返回数据
        JSONObject result = new JSONObject();
        HashMap<String, int[]> ageHashMap = new HashMap<>();
        for (int i = 0; i < generation.length - 1; i++) {
            String key = generation[i] + " ~ " + (generation[i + 1] - 1);
            ageHashMap.put(key, ages[i]);
        }
        result.put("age", ageHashMap);
        HashMap<String, Integer> genderHashMap = new HashMap<>();
        genderHashMap.put("female", genders[0]);
        genderHashMap.put("male", genders[1]);
        result.put("gender", genderHashMap);
        HashMap<String, String> emotionHashMap = new HashMap<>();
        for (int i = 0; i < emotionList.length; i++) {
            // TODO: 2018/10/16 此处前台遗留问题. 可以直接将显性情绪和隐性情绪存入数组中返回前台.
            // eg: emotionHashMap.put(emotionList[i], emotions[i]);
            StringBuilder value = new StringBuilder();
            for (int j = 0; j < emotions[i].length; j++) {
                value.append(",").append(emotions[i][j]);
            }
            emotionHashMap.put(emotionList[i], value.toString().substring(1));
        }
        result.put("emotions", emotionHashMap);
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

    public long getSnapCountByCameraSdkId(String cameraSdkId){
        BasicDBObject dbObject = new BasicDBObject();
        dbObject.put("cameraSdkId",cameraSdkId);
        return snapshotCollection.count(dbObject);
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
