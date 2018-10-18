package cn.anytec.security.service;

import cn.anytec.security.common.ServerResponse;
import cn.anytec.security.model.TbCamera;
import cn.anytec.security.model.dto.CameraDTO;

import java.util.List;

public interface CameraService {
    /**通过cameraId获取camera*/
    TbCamera getCameraById(Integer cameraId);

    /**通过cameraSdkId获取camera*/
    TbCamera getCameraBySdkId(String sdkId);

    /**添加camera*/
    ServerResponse add(TbCamera camera);

    /**删除camera*/
    ServerResponse<String> delete(String cameraSdkIds);

    //删除mysql里的camera
    void deleteMysqlCamera(String cameraSdkId);

    /**查询camera列表*/
    List<TbCamera> list(int pageNum, int pageSize, String name, Integer groupId, String type, String serverLabel, Integer status, String cameraSdkId);

    List<TbCamera>allList();

    CameraDTO cameraConvertToCameraDTO(TbCamera camera);

    /**更新camera*/
    ServerResponse update(TbCamera camera);

    /**获取已激活的视频流camera*/
    String cameras();

    /**新增camera的一个连接,用于实时监控的回显*/
    String connect(int id);
//    ServerResponse addCameraConnect(String id);

    /**断开camerea的一个连接，用于实时监控*/
    ServerResponse deleteCameraConnect(int id);

    /**杀掉camera回显的进程*/
    ServerResponse killCameraProcess(int id);

    /**获取所有camerade的serverLabel*/
    ServerResponse<List<String>> getServerLabel();

    /**校验设备名是否存在*/
    boolean isCameraNameExist(String cameraName);

    void changeOfflineCameraStatus(String cameraSdkId, Integer status);
}
