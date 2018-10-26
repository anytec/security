package cn.anytec.security.component.ipcamera.ipcService;

import cn.anytec.security.common.ServerResponse;
import cn.anytec.security.constant.RedisConst;
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
    @Autowired
    private CameraService cameraService;

    private String captureCameras = RedisConst.CAPTURECAMERAS;
    private String captureCamerasInUse = RedisConst.CAPTURECAMERAS_INUSE;
    private String captureCameraOffline = RedisConst.CAPTURECAMERAS_OFFLINE;

    @Scheduled(fixedDelay = 60000)
    public void ping() {
        Map<String,String> capCams = redisTemplate.opsForHash().entries(captureCameras);
        capCams.forEach((macAddress,ipcAddress)->{
            pingCaptureCameras(macAddress,ipcAddress);
        });

        Map<String,String> capCamsInUse = redisTemplate.opsForHash().entries(captureCamerasInUse);
        capCamsInUse.forEach((macAddress,ipcAddress)->{
            pingCaptureCameras(macAddress,ipcAddress);
        });
    }

    private void pingCaptureCameras(String macAddress, String ipcAddress){
        logger.debug("【pingCaptureCameras】{} , {}",macAddress,ipcAddress);
        String url = "http://"+ipcAddress+"/goform/ping";
        try{
            ipcHttpGet(url);
            redisTemplate.opsForHash().put("pingOffTimes",macAddress,"0");
        }catch (Exception e) {
            redisTemplate.opsForHash().increment("pingOffTimes",macAddress,1);
            logger.debug("【ping off times】{} ",redisTemplate.opsForHash().get("pingOffTimes",macAddress));
            if(Long.parseLong(redisTemplate.opsForHash().get("pingOffTimes",macAddress).toString())>10){
                //cameraService.delete(macAddress);
                deleteFromCache(macAddress);
                deleteFromInUseCache(macAddress);
                addToOfflineCache(macAddress,ipcAddress);
                //后面可以把摄像机状态写到enum
                cameraService.changeOfflineCameraStatus(macAddress,0);
                logger.info("【ping offline】mac:{}",macAddress);
            }
        }
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
        if(redisTemplate.opsForHash().hasKey(captureCamerasInUse,macAddress)){
            String ipAddress = (String) redisTemplate.opsForHash().get(captureCamerasInUse,macAddress);
            if(StringUtils.isEmpty(ipAddress)){
                throw new BussinessException(1,"【deleteFromInUseCache】ip地址为空，删除抓拍机失败！");
            }
            redisTemplate.opsForHash().delete(captureCamerasInUse,macAddress);
        }
    }

    @Override
    public void addToOfflineCache(String macAddress, String ipAddress) {
        logger.info("【addToOfflineCache】mac: {}, ip: {}",macAddress,ipAddress);
        redisTemplate.opsForHash().put(captureCameraOffline,macAddress,ipAddress);
    }

    @Override
    public void deleteFromOfflineCache(String macAddress) {
        logger.info("【deleteFromOfflineCache】mac: {}",macAddress);
        if(redisTemplate.opsForHash().hasKey(captureCameraOffline,macAddress)){
            redisTemplate.opsForHash().delete(captureCameraOffline,macAddress);
        }
    }

    @Override
    public void activeCaptureCamera(String macAddress) {
        logger.info("【activeCaptureCamera】mac: {}",macAddress);
        String ipAddress = getIpAddress(macAddress);
        if(StringUtils.isEmpty(ipAddress)){
            throw new BussinessException(1,"抓拍机 {}"+macAddress+" 获取不到ip,转换状态active失败");
        }
        String active = CaptureCameraStatus.ACTIVE.getMsg();
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
    public void invalidCaptureCamera(String macAddress, String ipAddress) {
        logger.info("【invalidCaptureCamera】mac: {}, ip: {}",macAddress,ipAddress);
        String invalid = CaptureCameraStatus.INVAILD.getMsg();
        String url = "http://"+ipAddress+"/goform/status?mac="+macAddress+"&operation="+invalid;
        ipcHttpGet(url);
    }

    public ServerResponse getCaptureCameras(){
        List<TbCamera> cameraList = cameraService.allList();
        List<String> macList = new ArrayList<>();
        Map<String,String> result = redisTemplate.opsForHash().entries(captureCameras);
            result.forEach((mac,ip)->{
                boolean flag = true;
                for(TbCamera camera : cameraList){
                    if(camera.getSdkId().equals(mac)){
                        flag = false;
                        break;
                    }
                }
                if(flag){
                    macList.add(mac);
                }
            });
        return ServerResponse.createBySuccess(macList);
    }

    @Override
    public ServerResponse getAllCaptureCameras() {
        List<String> macList = new ArrayList<>();
        Map<String,String> captureCameraList = redisTemplate.opsForHash().entries(captureCameras);
        captureCameraList.forEach((mac,ip)->{
            macList.add(mac);
        });
        Map<String,String> captureCameraInUseList = redisTemplate.opsForHash().entries(captureCamerasInUse);
        captureCameraInUseList.forEach((mac,ip)->{
            macList.add(mac);
        });
        return ServerResponse.createBySuccess(macList);
    }

    private void ipcHttpGet(String url){
        HttpHeaders requestHeaders = new HttpHeaders();
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(null, requestHeaders);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, requestEntity, String.class);
        String result = response.getBody();
        int statusCode = response.getStatusCodeValue();
        if(statusCode != 200){
            logger.info("【修改抓拍机状态失败】result: {}",result);
            throw new BussinessException(1,"修改抓拍机状态失败,statuscode："+statusCode);
        }
    }

    public String getIpAddress(String macAddress) {
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
