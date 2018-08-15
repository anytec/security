package cn.anytec.security.service;

import cn.anytec.security.common.ServerResponse;
import cn.anytec.security.model.TbCamera;
import cn.anytec.security.model.TbGroupCamera;
import com.github.pagehelper.PageInfo;

import java.util.List;
import java.util.Map;

public interface GroupCameraService {
    ServerResponse add(TbGroupCamera groupCamera);
    ServerResponse<String> delete(String groupCameraIds);
    ServerResponse update(TbGroupCamera groupCamera);
    ServerResponse<PageInfo> list(Integer pageNum, Integer pageSize, String groupName);
    ServerResponse<Map<String,List<TbCamera>>> getAllCameras();
}
