package cn.anytec.security.component.mongo;


import cn.anytec.security.model.parammodel.IdenfitySnapParam;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Service;
import java.util.Map;

@Service
public interface MongoDBService {

    void addSnapShot(String json, Map<String, Object> map);

    void addWarningFace(String json, Map<String, Object> map);

    JSONObject getSnapshotList(Map<String, String[]> paramMap);

    JSONObject getWarningFaceList(Map<String, String[]> paramMap);

    JSONObject identifySnap(Map<String,String> sdkMap, IdenfitySnapParam idenfitySnapParam);

    JSONObject sanpCounting();
    
    JSONObject peopleCounting(Map<String, String[]> paramMap);

    JSONObject peopleAnalysis(Map<String, String[]> paramMap);

    long getNumberOfWarningByTime(Long start_timestamp,Long end_timestamp);

    long getNumberOfSnapshotByTime(Long start_timestamp,Long end_timestamp);



}
