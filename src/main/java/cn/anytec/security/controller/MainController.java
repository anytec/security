package cn.anytec.security.controller;

import cn.anytec.security.common.ServerResponse;
import cn.anytec.security.model.parammodel.IdenfitySnapParam;
import cn.anytec.security.component.FRDataHandler;
import cn.anytec.security.service.CameraService;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/")
public class MainController {

    private static final Logger logger = LoggerFactory.getLogger(MainController.class);

    @Autowired
    private FRDataHandler frDataHandler;
    @Autowired
    private CameraService cameraService;

    @GetMapping("/v0/camera")
    public String cameras() {
        return cameraService.cameras();
    }

    @PostMapping("/receiveSnap")
    @ResponseBody
    public void receiveSnap(String cam_id, String timestamp, String bbox,
                            @RequestParam("photo") MultipartFile photo,
                            @RequestParam("face0") MultipartFile face0) {
        logger.info("invoke_function: { receiveSnap }");
        if (face0 != null) {
            frDataHandler.recieveSnap(cam_id,timestamp,bbox,photo);
        }else {
            logger.info("recieveSnap接收的人脸图片为空！");
        }
    }

    @RequestMapping("/main/identifySnap")
    @ResponseBody
    public ServerResponse identifySnap(IdenfitySnapParam param) {
        JSONObject resultList = frDataHandler.identifySnap(param);
        if (resultList != null) {
            return ServerResponse.createBySuccess(resultList);
        }
        return ServerResponse.createByErrorMessage("");
    }
}
