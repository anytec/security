package cn.anytec.security.component.mongo;

import cn.anytec.security.config.GeneralConfig;
import cn.anytec.security.model.parammodel.IdenfitySnapParam;
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
import org.springframework.stereotype.Service;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

@Service
public class MongoDBServiceImpl implements MongoDBService {
    @Autowired
    GeneralConfig config;

    private static final Logger logger = LoggerFactory.getLogger(MongoDBServiceImpl.class);
    private final MongoClient mongoClient = new MongoClient("127.0.0.1");
    private final MongoDatabase database = mongoClient.getDatabase("security");
    private final MongoCollection<Document> snapshotCollection = database.getCollection("snapshot");
    private final MongoCollection<Document> warningFaceCollection = database.getCollection("warning_face");

    public MongoDBServiceImpl(){
        logger.info("======= 初始化MongoDB =======");
    }

    //快照入库
    @Override
    public void addSnapShot(String json, Map<String, Object> map){
        Document document = Document.parse(json);
        document.putAll(map);
        snapshotCollection.insertOne(document);
    }

    ///报警人脸入库
    @Override
    public void addWarningFace(String json, Map<String, Object> map){
        Document document = Document.parse(json);
        document.putAll(map);
        warningFaceCollection.insertOne(document);
    }

    //获取快照列表
    public JSONObject getSnapshotList(Map<String,String[]> paramMap){
        Integer pageNum = 0;
        Integer pageSize = 10;
        if (paramMap.containsKey("pageNum")) {
            pageNum = Integer.parseInt(paramMap.get("pageNum")[0])-1;
            if(pageNum<0){
                pageNum = 0;
            }
        }
        if (paramMap.containsKey("pageSize")) {
            pageSize = Integer.parseInt(paramMap.get("pageSize")[0]);
        }

        BasicDBObject dbObject =getTimeDBObject(paramMap);
        //cameraGroupId条件
        if (paramMap.containsKey("cameraGroupId")) {
            Integer cameraGroupId = Integer.parseInt(paramMap.get("cameraGroupId")[0]);
            if(cameraGroupId != 0){
                dbObject.put("cameraGroupId",cameraGroupId);
            }
        }
        //cameraName条件
        if (paramMap.containsKey("cameraName")) {
            String cameraName = paramMap.get("cameraName")[0];
            if(!StringUtils.isEmpty(cameraName)){
                Pattern cameraPattern = Pattern.compile("^.*"+cameraName+".*$");
                dbObject.put("cameraName",cameraPattern);
            }
        }
        List<JSONObject> dataList = getResultJson(snapshotCollection.find(dbObject).skip(pageNum * pageSize).limit(pageSize).sort(new BasicDBObject("timestamp",-1)));
        Integer count = Integer.parseInt(snapshotCollection.count(dbObject)+"");
        JSONObject result = new JSONObject();
        result.put("list",dataList);
        result.put("total",count);
        return result;
    }

    //获取报警人脸列表
    public JSONObject getWarningFaceList(Map<String,String[]> paramMap){
        Integer pageNum = 0;
        Integer pageSize = 10;
        if (paramMap.containsKey("pageNum")) {
            pageNum = Integer.parseInt(paramMap.get("pageNum")[0])-1;
            if(pageNum<0){
                pageNum = 0;
            }
        }
        if (paramMap.containsKey("pageSize")) {
            pageSize = Integer.parseInt(paramMap.get("pageSize")[0]);
        }

        BasicDBObject dbObject =getTimeDBObject(paramMap);
        if (paramMap.containsKey("cameraName")) {
            String cameraName = paramMap.get("cameraName")[0];
            if(!("").equals(cameraName) && cameraName != null){
                Pattern cameraPattern = Pattern.compile("^.*"+cameraName+".*$");
                dbObject.put("cameraName",cameraPattern);
            }
        }
        //cameraGroup条件
        if (paramMap.containsKey("cameraGroupId")) {
            Integer cameraGroupId = 0;
            cameraGroupId = Integer.parseInt(paramMap.get("cameraGroupId")[0]);
            if(cameraGroupId != 0){
                dbObject.put("cameraGroupId",cameraGroupId);
            }
        }
       //personName条件
        if (paramMap.containsKey("personName")) {
            String personName = paramMap.get("personName")[0];
            if(!StringUtils.isEmpty(personName)){
                Pattern personNamePattern = Pattern.compile("^.*"+personName+".*$");
                dbObject.put("personName",personNamePattern);
            }
        }
        //personGroup条件
        if (paramMap.containsKey("personGroupId")) {
            Integer personGroupId = 0;
            personGroupId = Integer.parseInt(paramMap.get("personGroupId")[0]);
            if(personGroupId != 0){
                dbObject.put("personGroupId",personGroupId);
            }
        }
        //gender条件
        if (paramMap.containsKey("gender")) {
            String gender = paramMap.get("gender")[0];
            if(!StringUtils.isEmpty(gender)){
                dbObject.put("gender",gender);
            }
        }
        //idNumber条件
        if (paramMap.containsKey("idNumber")) {
            String idNumber = paramMap.get("idNumber")[0];
            if(!StringUtils.isEmpty(idNumber)){
                Pattern idNumberPattern = Pattern.compile("^.*"+idNumber+".*$");
                dbObject.put("idNumber",idNumberPattern);
            }
        }

        List<JSONObject> dataList = null;
        dataList = getResultJson(warningFaceCollection.find(dbObject).skip(pageNum * pageSize).limit(pageSize).sort(new BasicDBObject("confidence",-1)));
        Integer count = Integer.parseInt(warningFaceCollection.count(dbObject)+"");
        JSONObject result = new JSONObject();
        result.put("list",dataList);
        result.put("total",count);
        return result;
    }

    //快照搜索
    public JSONObject identifySnap(Map<String,String> sdkMap,IdenfitySnapParam idenfitySnapParam){
        JSONObject result = new JSONObject();
        BasicDBObject dbObject = new BasicDBObject();
        String format ="yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        //sdkId条件
        if(sdkMap.size()>0){
            BasicDBList sdkIds = new BasicDBList();
            sdkMap.forEach((k,v)->{
                sdkIds.add(k);
            });
            dbObject.put("sdkFaceId",new BasicDBObject("$in",sdkIds));
        }

        BasicDBList groupIds = new BasicDBList();
        BasicDBList camIds = new BasicDBList();
        //cameraGroup条件
        String cameraGroupIds = idenfitySnapParam.getCameraGroupIds();
        if(!StringUtils.isEmpty(cameraGroupIds)){
            String[] cameraGroupIdList = cameraGroupIds.split(",");
            if(cameraGroupIdList.length>0){
                for(String groupId : cameraGroupIdList){
                    groupIds.add(Integer.parseInt(groupId));
                }
            }
        }
        //camera条件
        String cameraIds = idenfitySnapParam.getCameraIds();
        if(!StringUtils.isEmpty(cameraIds)){
            String[] cameraIdList = cameraIds.split(",");
            if(cameraIdList.length>0){
                for(int i = 0; i<cameraIdList.length ; i++){
                    camIds.add(Integer.parseInt(cameraIdList[i]));
                }
            }
        }
        //camera和cameraGroup条件
        if(!StringUtils.isEmpty(cameraGroupIds) && !StringUtils.isEmpty(cameraIds)){
            BasicDBList cameraQuery = new BasicDBList();
            BasicDBObject cameraObj = new BasicDBObject();
            BasicDBObject cameraGroupObj = new BasicDBObject();
            cameraGroupObj.put("cameraGroupId",new BasicDBObject("$in",groupIds));
            cameraObj.put("cameraId",new BasicDBObject("$in",camIds));
            cameraQuery.add(cameraObj);
            cameraQuery.add(cameraGroupObj);
            dbObject.put("$or",cameraQuery);
        }else {
            if(camIds.size()>0){
                dbObject.put("cameraId",new BasicDBObject("$in",camIds));
            }
            if(groupIds.size()>0){
                dbObject.put("cameraGroupId",new BasicDBObject("$in",groupIds));
            }
        }
        List<JSONObject> dataList = getResultJson(snapshotCollection.find(dbObject).sort(new BasicDBObject("timestamp",-1)));
        for(JSONObject data : dataList){
            String sdkId = data.get("sdkFaceId").toString();
            if(sdkMap.containsKey(sdkId)){
                data.put("confidence",sdkMap.get(sdkId));
            }
        }
        Integer count = Integer.parseInt(snapshotCollection.count(dbObject)+"");
        result.put("list",dataList);
        result.put("total",count);
        return result;
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

    @Override
    public long getNumberOfWarningByTime(Long start_timestamp,Long end_timestamp) {

        BasicDBObject dbObject = getTimestampDBObject(start_timestamp,end_timestamp);
        if(dbObject.size() > 0){
            return warningFaceCollection.count(dbObject);
        }
        return -1;
    }

    @Override
    public long getNumberOfSnapshotByTime(Long start_timestamp,Long end_timestamp) {
        BasicDBObject dbObject = getTimestampDBObject(start_timestamp,end_timestamp);
        if(dbObject.size() > 0){
            return snapshotCollection.count(dbObject);
        }
        return -1;
    }

    private BasicDBObject getTimestampDBObject(Long start_timestamp,Long end_timestamp){
        BasicDBObject dbObject = new BasicDBObject();
        if(start_timestamp != null && end_timestamp != null){
            if(start_timestamp > end_timestamp)
                return null;
            dbObject.put("timestamp", new BasicDBObject().append("$gte", start_timestamp).append("$lte", end_timestamp));
        }else if(start_timestamp != null){
            dbObject.put("timestamp", new BasicDBObject().append("$gte", start_timestamp));
        }else if(end_timestamp != null){
            dbObject.put("timestamp", new BasicDBObject().append("$lte", end_timestamp));
        }
        return dbObject;
    }

    private BasicDBObject getTimeDBObject(Map<String,String[]> paramMap){
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
        return getTimestampDBObject(start,end);
    }

    private Long convertTime(String timeStr){
        try{
            if(!StringUtils.isEmpty(timeStr)){
                if(timeStr.length() == 13 ||timeStr.length() == 10){
                    return Long.parseLong(timeStr);
                }else if(timeStr.contains("GMT")){
                    return new Date(timeStr).getTime();
                }else {
                    String format ="yyyy-MM-dd HH:mm:ss";
                    SimpleDateFormat sdf = new SimpleDateFormat(format);
                    return sdf.parse(timeStr).getTime();
                }
            }
        }catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

}
