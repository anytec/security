package cn.anytec.security.service.impl;

import cn.anytec.security.common.ResponseCode;
import cn.anytec.security.common.ServerResponse;
import cn.anytec.security.config.GeneralConfig;
import cn.anytec.security.dao.TbCameraMapper;
import cn.anytec.security.dao.TbGroupCameraMapper;
import cn.anytec.security.model.TbCamera;
import cn.anytec.security.model.TbCameraExample;
import cn.anytec.security.model.TbGroupCamera;
import cn.anytec.security.model.TbGroupCameraExample;
import cn.anytec.security.service.CameraService;
import cn.anytec.security.service.GroupCameraService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.base.Splitter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("GroupCameraService")
public class GroupCameraServiceImpl implements GroupCameraService {
    @Autowired
    private TbGroupCameraMapper groupCameraMapper;
    @Autowired
    private TbCameraMapper cameraMapper;

    public ServerResponse add(TbGroupCamera groupCamera) {
        int updateCount = groupCameraMapper.insertSelective(groupCamera);
        if (updateCount > 0) {
            return ServerResponse.createBySuccess("新增groupCamera成功", groupCamera);
        }
        return ServerResponse.createByErrorMessage("新增groupCamera失败");
    }

    public ServerResponse<PageInfo> list(Integer pageNum, Integer pageSize, String groupName) {
        if(pageNum != 0 && pageSize != 0){
            PageHelper.startPage(pageNum, pageSize);
        }
        TbGroupCameraExample example = new TbGroupCameraExample();
        TbGroupCameraExample.Criteria c = example.createCriteria();
        if(!StringUtils.isEmpty(groupName)){
            c.andNameLike("%"+groupName+"%");
        }
        List<TbGroupCamera> cameraList = groupCameraMapper.selectByExample(example);
        PageInfo pageResult = new PageInfo(cameraList);
        return ServerResponse.createBySuccess(pageResult);
    }

    public ServerResponse delete(String groupCameraIds){
        List<String> groupCameraIdList = Splitter.on(",").splitToList(groupCameraIds);
        if(CollectionUtils.isEmpty(groupCameraIdList)){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        for(String cameraGroupId : groupCameraIdList){
            if(!StringUtils.isEmpty(cameraGroupId)){
                TbGroupCameraExample example = new TbGroupCameraExample();
                TbGroupCameraExample.Criteria c = example.createCriteria();
                c.andIdEqualTo(Integer.parseInt(cameraGroupId));
                groupCameraMapper.deleteByExample(example);
            }
        }
        return ServerResponse.createBySuccess();
    }

    public ServerResponse<TbGroupCamera> update(TbGroupCamera groupCamera) {
        int updateCount = groupCameraMapper.updateByPrimaryKeySelective(groupCamera);
        if (updateCount > 0) {
            return ServerResponse.createBySuccess("更新groupCamera信息成功", groupCamera);
        }
        return ServerResponse.createByErrorMessage("更新groupCamera信息失败");
    }

    @Override
    public ServerResponse<Map<String,List<TbCamera>>> getAllCameras() {
        List<String>  keyList = new ArrayList<>();
        Map<String,List<TbCamera>> allCamera = new HashMap<>();
        TbGroupCameraExample example = new TbGroupCameraExample();
        List<TbGroupCamera> groupCameraList = groupCameraMapper.selectByExample(example);
        if(groupCameraList.size()>0){
            for(TbGroupCamera groupCamera : groupCameraList){
                keyList.add(groupCamera.getName()+","+groupCamera.getId());
            }
           for(String key : keyList){
               TbCameraExample cexm = new TbCameraExample();
               cexm.createCriteria().andGroupIdEqualTo(Integer.parseInt(key.split(",")[1]));
               List<TbCamera> cameraList = cameraMapper.selectByExample(cexm);
               allCamera.put(key,cameraList);
           }
           return ServerResponse.createBySuccess(allCamera);
        }
        return ServerResponse.createByError();
    }
}
