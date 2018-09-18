package cn.anytec.security.component.ipcamera.ipcService;

import cn.anytec.security.common.ServerResponse;
import cn.anytec.security.core.enums.CameraType;
import cn.anytec.security.core.enums.CaptureCameraStatus;
import cn.anytec.security.core.exception.BussinessException;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@EnableScheduling
public class IPCOperationsImpl implements IPCOperations {

    private final Logger logger = LoggerFactory.getLogger(IPCOperationsImpl.class);
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private RestTemplate restTemplate;
    @Value("${redisKeys.captureCameras}")
    private String captureCameras;
    @Value("${redisKeys.captureCamerasInUse}")
    private String captureCamerasInUse;


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
    @Transactional
    public void handleAddCaptureCamera(String macAddress) {
            standbyCaptureCamera(macAddress);
            addToInUseCache(macAddress);
            deleteFromCache(macAddress);
    }

    @Override
    public boolean addToCache(String macAddress, String ipAddress) {
        logger.info("【addToCache】mac: "+macAddress+" ip: "+ipAddress);
        if(StringUtils.isEmpty(macAddress)){
            logger.error("【addToCache】mac地址为空，添加抓拍机失败！");
            return false;
        }
        if(StringUtils.isEmpty(ipAddress)){
            logger.error("【addToCache】ip地址为空，添加抓拍机失败！");
        }
        redisTemplate.opsForHash().put(captureCameras,macAddress,ipAddress);
        //redisTemplate.expire(captureCameras, 1, TimeUnit.DAYS);
        return true;
    }

    @Override
    public boolean addToCache(String macAddress) {
        String ipAddress = "";
        if(StringUtils.isEmpty(macAddress)){
            logger.error("【addToCache】mac地址为空，添加抓拍机失败！");
            return false;
        }
        if(redisTemplate.opsForHash().hasKey(captureCamerasInUse,macAddress)){
            ipAddress = (String) redisTemplate.opsForHash().get(captureCamerasInUse,macAddress);
            redisTemplate.opsForHash().put(captureCameras,macAddress,ipAddress);
            return true;
        }
        return false;
    }

    @Override
    public void deleteFromCache(String macAddress) {
        logger.info("【deleteFromCache】mac: "+macAddress);
        if(StringUtils.isEmpty(macAddress)){
            throw new BussinessException(1,"【deleteFromCache】mac地址为空，删除抓拍机失败！");
        }
        if(redisTemplate.opsForHash().hasKey(captureCameras,macAddress)){
            redisTemplate.opsForHash().delete(captureCameras,macAddress);
        }
    }

    @Override
    public void addToInUseCache(String macAddress) {
        logger.info("【addToInUseCache】mac: "+macAddress);
        if(StringUtils.isEmpty(macAddress)){
            throw new BussinessException(1,"【addToInUseCache】mac地址为空，添加抓拍机失败！");
        }
        String ipAddress = "";
        if(redisTemplate.opsForHash().hasKey(captureCameras,macAddress)){
            ipAddress = (String) redisTemplate.opsForHash().get(captureCameras,macAddress);
        }
        if(StringUtils.isEmpty(ipAddress)){
            throw new BussinessException(1,"【addToInUseCache】ip地址为空，添加抓拍机失败！");
        }
        redisTemplate.opsForHash().put(captureCamerasInUse,macAddress,ipAddress);
        //redisTemplate.expire(captureCamerasInUse, 1, TimeUnit.DAYS);
    }

    @Override
    public void deleteFromInUseCache(String macAddress) {
        logger.info("【deleteFromInUseCache】mac: "+macAddress);
        if(StringUtils.isEmpty(macAddress)){
            throw new BussinessException(1,"【deleteFromInUseCache】mac地址为空，删除抓拍机失败！");
        }
        if(redisTemplate.opsForHash().hasKey(captureCameras,macAddress)){
            String ipAddress = (String) redisTemplate.opsForHash().get(captureCameras,macAddress);
            if(StringUtils.isEmpty(ipAddress)){
                throw new BussinessException(1,"【deleteFromInUseCache】ip地址为空，删除抓拍机失败！");
            }
            redisTemplate.opsForHash().put(captureCameras,macAddress,ipAddress);
            redisTemplate.opsForHash().delete(captureCamerasInUse,macAddress);
        }
    }

    @Override
    public void activeCaptureCamera(String macAddress) {
        logger.info("【activeCaptureCamera】mac: "+macAddress);
        String ipAddress = getIpAddress(macAddress);
        if(StringUtils.isEmpty(ipAddress)){
            throw new BussinessException(1,"抓拍机 "+macAddress+" 获取不到ip,转换状态active失败");
        }
        String active = CaptureCameraStatus.ACCEPTED.getMsg();
        String url = "http://"+ipAddress+"/goform/status?mac="+macAddress+"&operation="+active;
        ipcHttpGet(url);
    }

    @Override
    public void standbyCaptureCamera(String macAddress) {
        logger.info("【standbyCaptureCamera】mac: "+macAddress);
        String ipAddress = getIpAddress(macAddress);
        if(StringUtils.isEmpty(ipAddress)){
            throw new BussinessException(1,"抓拍机 "+macAddress+" 获取不到ip,转换状态standby失败");
        }
        String standby = CaptureCameraStatus.STANDBY.getMsg();
        String url = "http://"+ipAddress+"/goform/status?mac="+macAddress+"&operation="+standby;
        ipcHttpGet(url);
    }

    @Override
    public void invalidCaptureCamera(String macAddress) {
        logger.info("【invalidCaptureCamera】mac: "+macAddress);
        String ipAddress = getIpAddress(macAddress);
        if(StringUtils.isEmpty(ipAddress)){
            throw new BussinessException(1,"抓拍机 "+macAddress+" 获取不到ip,转换状态invalid失败");
        }
        String invalid = CaptureCameraStatus.INVAILD.getMsg();
        String url = "http://"+ipAddress+"/goform/status?mac="+macAddress+"&operation="+invalid;
        ipcHttpGet(url);
    }

    public ServerResponse getCaptureCameras(){
        List<String> macList = new ArrayList<>();
        Map<String,String> result = redisTemplate.opsForHash().entries(captureCameras);
        result.forEach((mac,ip)->{
            macList.add(mac);
        });
        return ServerResponse.createBySuccess(macList);
    }

    private void ipcHttpGet(String url){
        HttpHeaders requestHeaders = new HttpHeaders();
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(null, requestHeaders);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, requestEntity, String.class);
        String result = response.getBody();
        logger.info("ipc response: "+result);
        int statusCode = response.getStatusCodeValue();
        logger.info("ipc response statuscode: "+statusCode);
        if(statusCode != 200){
            throw new BussinessException(1,"修改抓拍机状态失败,statuscode："+statusCode);
        }
    }

    private String getIpAddress(String macAddress) {
        String ipAddress = "";
        if(redisTemplate.opsForHash().hasKey(captureCamerasInUse,macAddress)){
            ipAddress = (String) redisTemplate.opsForHash().get(captureCamerasInUse,macAddress);
        }
        if(StringUtils.isEmpty(ipAddress)){
            if(redisTemplate.opsForHash().hasKey(captureCameras,macAddress)){
                ipAddress = (String) redisTemplate.opsForHash().get(captureCameras,macAddress);
            }
        }
        return ipAddress;
    }
}
