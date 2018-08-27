package cn.anytec.security.service;

import cn.anytec.security.common.ServerResponse;
import cn.anytec.security.model.TbCamera;
import com.github.pagehelper.PageInfo;

import java.util.List;

public interface CameraService {
    ServerResponse add(TbCamera camera);
    ServerResponse<String> delete(String cameraIds);
    List<TbCamera> list(int pageNum, int pageSize, String name, Integer groupId, String type, String serverLabel, Integer status, String cameraSdkId);
    ServerResponse update(TbCamera camera);
    TbCamera getCameraBySdkId(String sdkId);
    String cameras();
    String connect(int id);
//    ServerResponse addCameraConnect(String id);
    ServerResponse deleteCameraConnect(int id);
    ServerResponse killCameraProcess(int id);
    ServerResponse<List<String>> getServerLabel();
}
