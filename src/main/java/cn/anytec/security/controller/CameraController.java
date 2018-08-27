package cn.anytec.security.controller;

import cn.anytec.security.common.ServerResponse;
import cn.anytec.security.model.TbCamera;
import cn.anytec.security.service.CameraService;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
//@RequestMapping("/camera")
public class CameraController {

    @Autowired
    private CameraService cameraService;

    @PostMapping("/camera/add")
    public ServerResponse add(TbCamera camera){
        return cameraService.add(camera);
    }
    @RequestMapping("/camera/delete")
    @ResponseBody
    public ServerResponse delete(@RequestParam(value = "cameraIds") String cameraIds){
        return cameraService.delete(cameraIds);
    }

    @RequestMapping("/camera/list")
    @ResponseBody
    public ServerResponse list(@RequestParam(value = "pageNum",defaultValue = "1") int pageNum, @RequestParam(value = "pageSize",defaultValue = "10") int pageSize,
                               @RequestParam(value = "name",required = false)String name,
                               @RequestParam(value = "groupId",required = false)Integer groupId,
                               @RequestParam(value = "type",required = false)String type,
                               @RequestParam(value = "serverLabel",required = false)String serverLabel,
                               @RequestParam(value = "status",required = false)Integer status,
                               @RequestParam(value = "cameraSdkId",required = false)String cameraSdkId){

        List<TbCamera> cameraList = cameraService.list(pageNum,pageSize,name,groupId,type,serverLabel,status,cameraSdkId);
        PageInfo pageResult = new PageInfo(cameraList);
        return ServerResponse.createBySuccess(pageResult);
    }

    @RequestMapping("/camera/update")
    public ServerResponse update(TbCamera camera){
        return cameraService.update(camera);
    }

    @GetMapping("/cameras")
    public String getCameras() {
        return cameraService.cameras();
    }


    @GetMapping("/camera/connect")
    public String connectCamera(Integer id){
        return cameraService.connect(id);
    }

    @GetMapping("/camera/deleteConnect")
    public ServerResponse deleteCamera(Integer id){
        return cameraService.deleteCameraConnect(id);
    }

    @RequestMapping("/camera/getServerLabel")
    public ServerResponse getServerLabel(){
        return cameraService.getServerLabel();
    }

}
