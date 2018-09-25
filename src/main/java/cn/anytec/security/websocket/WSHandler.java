package cn.anytec.security.websocket;


import cn.anytec.security.component.CameraStreamMonitor;
import cn.anytec.security.model.TbCamera;
import cn.anytec.security.service.CameraService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class WSHandler {

    @Value("${camera.allProcess}")
    private String allProcessLabel;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private CameraStreamMonitor cameraStreamMonitor;
    @Autowired
    private CameraService cameraService;

    private Map<String,List<String>> cameraId_sessionIdList_map = new ConcurrentHashMap<>();

    public Map<String,String> getViewingCameraIdList(){
        return redisTemplate.opsForHash().entries(allProcessLabel);
    }

    public Map<String, List<String>> getCameraId_sessionIdList_map() {
        return cameraId_sessionIdList_map;
    }

    void removeSessionId(String sessionId){
        cameraId_sessionIdList_map.keySet().forEach(n->{
            List<String> sessionIdList = cameraId_sessionIdList_map.get(n);
            if(sessionIdList.contains(sessionId)){
                sessionIdList.remove(sessionId);
                //camera对应session连接数减一
                cameraStreamMonitor.disconnect(n);
                if(cameraId_sessionIdList_map.get(n).size() == 0)
                    cameraId_sessionIdList_map.remove(n);
            }
        });
    }

    void removeSessionId(String cameraId,String sessionId){
        List<String> sessionIdList = cameraId_sessionIdList_map.get(cameraId);
        if(sessionIdList != null && sessionIdList.contains(sessionId)){
            sessionIdList.remove(sessionId);
            //camera对应session连接数减一
            cameraStreamMonitor.disconnect(cameraId);
            if(cameraId_sessionIdList_map.get(cameraId).size() == 0)
                cameraId_sessionIdList_map.remove(cameraId);
        }
    }

    String registerCamera(String cameraId,String sessionId,boolean addFlag){
        String runInfo = null;
        TbCamera camera = cameraService.getCameraBySdkId(cameraId);
        if(cameraId_sessionIdList_map.containsKey(cameraId)){
            List<String> sessionIdList = cameraId_sessionIdList_map.get(cameraId);
            if(!sessionIdList.contains(sessionId)){
                sessionIdList.add(sessionId);
                runInfo = cameraStreamMonitor.createViewProcess(camera);
                if(runInfo != null && runInfo.equals("exist")){
                    redisTemplate.opsForHash().increment(allProcessLabel,cameraId, 1);
                }
            }
            runInfo = cameraStreamMonitor.createViewProcess(camera);
        }else {
            List<String> sessionIdList = new ArrayList<>();
            sessionIdList.add(sessionId);
            cameraId_sessionIdList_map.put(cameraId,sessionIdList);
            runInfo = cameraStreamMonitor.createViewProcess(camera);
            if(runInfo != null && runInfo.equals("exist")){
                redisTemplate.opsForHash().increment(allProcessLabel,cameraId, 1);
            }
        }
        return runInfo;
    }

    private boolean checkHealth(String cameraId){

        return false;
    }

        /*private Map<String,List<String>> getCameraId_sessionIdList_map(){
        Map<String,List<String>> cameraId_sessionIdList_map = redisService.get(allProcessLabel,Map.class);
        if(cameraId_sessionIdList_map != null){
            return cameraId_sessionIdList_map;
        }else {
            redisService.set(allProcessLabel,new HashMap<String,List<String>>());
        }
//        redisTemplate.setEnableTransactionSupport(true);
//        redisTemplate.watch(allProcessLabel);
//        List<Object> list = redisTemplate.exec();
//        redisTemplate.setEnableTransactionSupport(false);
        return redisService.get(allProcessLabel,Map.class);

    }*/

}
