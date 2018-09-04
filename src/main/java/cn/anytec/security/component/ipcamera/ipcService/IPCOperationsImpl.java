package cn.anytec.security.component.ipcamera.ipcService;

import cn.anytec.security.model.TbCamera;
import cn.anytec.security.service.CameraService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@EnableScheduling
public class IPCOperationsImpl implements IPCOperations {

    private final Logger logger = LoggerFactory.getLogger(IPCOperationsImpl.class);
    @Autowired
    public RedisTemplate redisTemplate;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private CameraService cameraService;
    @Value("${redisKeys.captureCameras}")
    private String captureCameras;


 /*   @Scheduled(fixedDelay = 60000)
    public void pingCaptureCameras() {
        Map<String,String> result = redisTemplate.opsForHash().entries(captureCameras);
        result.forEach((macAddress,ipcAddress)->{
            String url = "http://"+ipcAddress+"/goform/ping";
            if(!ipcHttpGet(url)){
                redisTemplate.opsForValue().increment(macAddress,1);
                if((long)redisTemplate.opsForValue().get(macAddress)>10){
                    TbCamera camera = cameraService.getCameraBySdkId(macAddress);
                    camera.setCameraStatus(0);
                    cameraService.update(camera);
                }
            }else {
                redisTemplate.opsForValue().set(macAddress,0);
            }
        });
    }*/

    @Override
    public boolean addToCache(String mac, String ipAddress) {
        if(!StringUtils.isEmpty(mac) && !StringUtils.isEmpty(ipAddress)){
            redisTemplate.opsForHash().put(captureCameras,mac,ipAddress);
            redisTemplate.expire(captureCameras, 1, TimeUnit.DAYS);
            return true;
        }
        return false;
    }

    @Override
    public void deleteFromCache(String mac) {
        if(!StringUtils.isEmpty(mac)){
            redisTemplate.opsForHash().delete(captureCameras,mac);
        }
    }

    @Override
    public boolean activeCaptureCamera(String macAddress, String ipcAddress) {
        String url = "http://"+ipcAddress+"/goform/status?mac="+macAddress+"&operation=active";
        return ipcHttpGet(url);
    }

    @Override
    public boolean invalidCaptureCamera(String macAddress, String ipcAddress) {
        String url = "http://"+ipcAddress+"/goform/status?mac="+macAddress+"&operation=invalid";
        return ipcHttpGet(url);
    }

    public Map<String,String> getCaptureCameras(){
        Map<String,String> result = redisTemplate.opsForHash().entries(captureCameras);
        return result;
    }

    private boolean ipcHttpGet(String url){
        HttpHeaders requestHeaders = new HttpHeaders();
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(null, requestHeaders);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, requestEntity, String.class);
        String result = response.getBody();
        logger.info("ipc response: "+result);
        int statusCode = response.getStatusCodeValue();
        logger.info("ipc response statuscode: "+statusCode);
        if(statusCode == 200){
            return true;
        }
        return false;
    }
}
