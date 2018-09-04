package cn.anytec.security.controller;

import cn.anytec.security.common.ServerResponse;
import cn.anytec.security.component.FRDataHandler;
import cn.anytec.security.config.GeneralConfig;
import cn.anytec.security.model.TbCamera;
import cn.anytec.security.model.parammodel.IdenfitySnapParam;
import cn.anytec.security.service.CameraService;
import cn.anytec.security.service.PersonService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/")
public class MainController {

    private static final Logger logger = LoggerFactory.getLogger(MainController.class);

    @Autowired
    private FRDataHandler frDataHandler;
    @Autowired
    private CameraService cameraService;
    @Autowired
    private PersonService personService;
    @Autowired
    private RedisTemplate redisTemplate;

    @Value("${redisKeys.warningThreshold}")
    String warningThreshold;

    @GetMapping("/v0/camera")
    public String cameras() {
        return cameraService.cameras();
    }

    @PostMapping("/receiveSnap")
    @ResponseBody
    public void receiveSnap(String cam_id, String timestamp, String bbox,
                            @RequestParam("photo") MultipartFile photo,
                            @RequestParam(value = "face0",required = false) MultipartFile face0) {
        logger.info("invoke_function: { receiveSnap }");
        if (photo != null) {
            frDataHandler.recieveSnap(cam_id,timestamp,bbox,photo);
        }else {
            logger.info("recieveSnap接收的人脸图片为空！");
        }
    }

    @RequestMapping("/main/identifySnap")
    @ResponseBody
    public ServerResponse identifySnap(IdenfitySnapParam param) {
        return frDataHandler.identifySnap(param);
    }

    @RequestMapping("/setWarningThreshold")
    @ResponseBody
    public ServerResponse setWarningThreshold(@RequestParam("threshold") String threshold){
        if(!StringUtils.isEmpty(threshold)){
            double thresholdValue = Double.parseDouble(threshold);
            if(thresholdValue > 1){
                thresholdValue = thresholdValue/100;
                redisTemplate.opsForValue().set(warningThreshold,thresholdValue,1, TimeUnit.DAYS);
                return ServerResponse.createBySuccess();
            }
        }
        return ServerResponse.createByErrorMessage("传入的threshold值有误： "+threshold);
    }

    /*@RequestMapping("/uploadPhotos")
    @ResponseBody
    public String uploadPhotos(MultipartFile[] photos) {
        personService.savePersonPhotos(photos);
        return "ok";
    }*/

}
