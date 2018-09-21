package cn.anytec.security.controller;

import cn.anytec.security.common.ServerResponse;
import cn.anytec.security.component.ipcamera.ipcService.IPCOperations;
import cn.anytec.security.core.annotion.OperLog;
import cn.anytec.security.core.annotion.Permission;
import cn.anytec.security.core.enums.PermissionType;
import cn.anytec.security.model.TbCamera;
import cn.anytec.security.model.dto.CameraDTO;
import cn.anytec.security.service.CameraService;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
//@RequestMapping("/camera")
public class CameraController {

    @Autowired
    private CameraService cameraService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private IPCOperations ipcOperations;

    @Value("${redisKeys.captureCameras}")
    private String captureCameras;
    @Value("${redisKeys.captureCamerasInUse}")
    private String captureCamerasInUse;

    @OperLog(value = "添加设备", key="id,name")
    @PostMapping("/camera/add")
    @Permission(value = "添加设备", method = PermissionType.IS_ADMIN)
    public ServerResponse add(TbCamera camera){
        if(cameraService.isCameraNameExist(camera.getName())){
            return ServerResponse.createByErrorMessage("设备名称 "+camera.getName()+" 已存在");
        }
        return cameraService.add(camera);
    }

    @OperLog(value = "删除设备", key = "cameraSdkIds")
    @RequestMapping("/camera/delete")
    @ResponseBody
    @Permission(value = "删除设备", method = PermissionType.IS_ADMIN)
    public ServerResponse delete(@RequestParam(value = "cameraSdkIds") String cameraSdkIds){
        return cameraService.delete(cameraSdkIds);
    }

//    @OperLog(value = "查询设备列表", key = "type")
    @RequestMapping("/camera/list")
    @ResponseBody
//    @Permission(value = "查询设备", method = PermissionType.IS_ADMIN)
    public ServerResponse list(@RequestParam(value = "pageNum",defaultValue = "1") int pageNum, @RequestParam(value = "pageSize",defaultValue = "10") int pageSize,
                               @RequestParam(value = "name",required = false)String name,
                               @RequestParam(value = "groupId",required = false)Integer groupId,
                               @RequestParam(value = "type",required = false)String type,
                               @RequestParam(value = "serverLabel",required = false)String serverLabel,
                               @RequestParam(value = "status",required = false)Integer status,
                               @RequestParam(value = "cameraSdkId",required = false)String cameraSdkId){

        List<TbCamera> cameraList = cameraService.list(pageNum,pageSize,name,groupId,type,serverLabel,status,cameraSdkId);
        PageInfo pageResult = new PageInfo(cameraList);
        List<CameraDTO> cameraDTOList = cameraList.stream()
                .map(e->cameraService.cameraConvertToCameraDTO(e))
                .collect(Collectors.toList());
        pageResult.setList(cameraDTOList);
        return ServerResponse.createBySuccess(pageResult);
    }

    @OperLog(value = "修改设备", key="id,name")
    @RequestMapping("/camera/update")
    @Permission(value = "修改设备", method = PermissionType.IS_ADMIN)
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

    @RequestMapping("/camera/getCaptureCameras")
    public ServerResponse getCaptureCameras(){
        return ipcOperations.getCaptureCameras();
    }

    @RequestMapping("/camera/activeCaptureCamera")
    public void activeCaptureCamera(@RequestParam(value = "macAddress")String macAddress){
        ipcOperations.activeCaptureCamera(macAddress);
    }

    @RequestMapping("/camera/invalidCaptureCamera")
    public void invalidCaptureCamera(@RequestParam(value = "macAddress")String macAddress){
        ipcOperations.invalidCaptureCamera(macAddress);
    }

    @PostMapping("/addCaptureCamera")
    public void addToCache(String mac, String ipAddress) {
        if(!StringUtils.isEmpty(mac) && !StringUtils.isEmpty(ipAddress)){
            redisTemplate.opsForHash().put(captureCameras,mac,ipAddress);
        }
    }

}
