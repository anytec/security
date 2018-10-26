package cn.anytec.security.controller;

import cn.anytec.security.common.ServerResponse;
import cn.anytec.security.component.FRDataHandler;
import cn.anytec.security.config.GeneralConfig;
import cn.anytec.security.constant.RedisConst;
import cn.anytec.security.model.TbCamera;
import cn.anytec.security.model.parammodel.IdenfitySnapParam;
import cn.anytec.security.service.CameraService;
import cn.anytec.security.service.PersonService;
//import com.sun.tools.javac.util.Convert;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
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

    @GetMapping("/v0/camera")
    public String cameras() {
        return cameraService.cameras();
    }

    @PostMapping("/receiveSnap")
    @ResponseBody
    public void receiveSnap(String cam_id, String timestamp, String bbox,
                            @RequestParam("photo") MultipartFile photo,
                            @RequestParam(value = "face0",required = false) MultipartFile face0) {
        logger.info("【receiveSnap】cameraSdkId: {}",cam_id);
        if (photo != null) {
            frDataHandler.recieveSnap(cam_id,timestamp,bbox,photo);
        }else {
            logger.info("【recieveSnap】接收的人脸图片为空！");
        }
    }

    @RequestMapping("/main/identifySnap")
    @ResponseBody
    public ServerResponse identifySnap(IdenfitySnapParam param) {
        return frDataHandler.identifySnap(param);
    }

    @RequestMapping("/uploadPhotos")
    @ResponseBody
    public ServerResponse uploadPhotos(@RequestParam("photos") MultipartFile[] photos) {
        List<String> photoPathList = personService.uploadPhotos(photos);
        if(photoPathList.size()==0){
            return ServerResponse.createByErrorMessage("批量上传的照片数量为0！");
        }else if(!CollectionUtils.isEmpty(photoPathList)){
            return ServerResponse.createBySuccess(photoPathList);
        }else {
            return ServerResponse.createByErrorMessage("批量上传照片发生错误！");
        }
    }

    @RequestMapping("/addPhotos")
    @ResponseBody
    public ServerResponse addPhotos(@RequestParam("photoPathList")List<String> photoPathList,
                                    @RequestParam("personGroupId")String personGroupId,
                                    @RequestParam("personGroupName")String personGroupName) {
        return personService.addPhotos(photoPathList,personGroupId,personGroupName);
    }

}
