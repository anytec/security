package cn.anytec.security.service;

import cn.anytec.security.common.ServerResponse;
import cn.anytec.security.model.TbGroupCamera;
import cn.anytec.security.model.dto.CameraDTO;

import java.util.List;
import java.util.Map;

public interface GroupCameraService {
    TbGroupCamera getCameraGroupInfo(Integer cameraGroupId);
    ServerResponse add(TbGroupCamera groupCamera);
    ServerResponse<String> delete(String groupCameraIds);
    ServerResponse update(TbGroupCamera groupCamera);
    ServerResponse list(Integer pageNum, Integer pageSize, String groupName);
    ServerResponse<Map<String,List<CameraDTO>>> getAllCameras(String status);
    TbGroupCamera getGroupCameraById(String cameraGroupId);
    List<Integer> getAllCameraGroupId();
    boolean isCameraGroupNameExist(String cameraGroupName);

}
