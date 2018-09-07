package cn.anytec.security.controller;

import cn.anytec.security.common.ServerResponse;
import cn.anytec.security.core.annotion.OperLog;
import cn.anytec.security.model.TbGroupCamera;
import cn.anytec.security.model.vo.CameraVO;
import cn.anytec.security.service.GroupCameraService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/groupCamera")
public class GroupCameraController {
    @Autowired
    private GroupCameraService groupCameraService;

    @OperLog(value = "添加设备组", key="id,name")
    @RequestMapping("/add")
    @ResponseBody
    public ServerResponse add(TbGroupCamera groupCamera){
        return groupCameraService.add(groupCamera);
    }

    @OperLog(value = "删除设备组", key = "groupCameraIds")
    @RequestMapping("/delete")
    @ResponseBody
    public ServerResponse delete(@RequestParam(value = "groupCameraIds") String groupCameraIds){
        return groupCameraService.delete(groupCameraIds);
    }


    @RequestMapping("/list")
    @ResponseBody
    public ServerResponse list(@RequestParam(value = "pageNum",defaultValue = "0") Integer pageNum,
                               @RequestParam(value = "pageSize",defaultValue = "0") Integer pageSize,
                               @RequestParam(value = "groupName",required = false)String groupName){
        return groupCameraService.list(pageNum,pageSize,groupName);
    }

    @OperLog(value = "修改设备组信息", key="id,name")
    @RequestMapping("/update")
    @ResponseBody
    public ServerResponse update(TbGroupCamera groupCamera){
        groupCameraService.getCameraGroupInfo(groupCamera.getId());
        return groupCameraService.update(groupCamera);
    }

    @RequestMapping("/getAllCameras")
    @ResponseBody
    public ServerResponse<Map<String,List<CameraVO>>> getAllCameras(@RequestParam(value = "status",required = false)String status){
        return groupCameraService.getAllCameras(status);
    }
}
