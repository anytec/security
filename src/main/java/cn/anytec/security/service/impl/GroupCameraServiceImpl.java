package cn.anytec.security.service.impl;

import cn.anytec.security.common.ResponseCode;
import cn.anytec.security.common.ServerResponse;
import cn.anytec.security.component.mongo.MongoDBService;
import cn.anytec.security.config.GeneralConfig;
import cn.anytec.security.core.log.LogObjectHolder;
import cn.anytec.security.dao.TbCameraMapper;
import cn.anytec.security.dao.TbGroupCameraMapper;
import cn.anytec.security.model.TbCamera;
import cn.anytec.security.model.TbCameraExample;
import cn.anytec.security.model.TbGroupCamera;
import cn.anytec.security.model.TbGroupCameraExample;
import cn.anytec.security.model.vo.CameraVO;
import cn.anytec.security.service.GroupCameraService;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.base.Splitter;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
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
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private MongoDBService mongoDBService;
    @Autowired
    private GeneralConfig config;

    public TbGroupCamera getCameraGroupInfo(Integer cameraGroupId){
        TbGroupCamera cameraGroup = groupCameraMapper.selectByPrimaryKey(cameraGroupId);
        LogObjectHolder.me().set(cameraGroup);
        return cameraGroup;
    }

    public ServerResponse add(TbGroupCamera groupCamera) {
        int updateCount = groupCameraMapper.insertSelective(groupCamera);
        if (updateCount > 0) {
            return ServerResponse.createBySuccess("添加groupCamera成功", groupCamera);
        }
        return ServerResponse.createByErrorMessage("添加groupCamera失败");
    }

    public ServerResponse list(Integer pageNum, Integer pageSize, String groupName) {
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


    public List<Integer> getAllCameraGroupId() {
        TbGroupCameraExample example = new TbGroupCameraExample();
        List<TbGroupCamera> cameraGroupList = groupCameraMapper.selectByExample(example);
        List<Integer> cameraGroupIdList = new ArrayList<>();
        for(TbGroupCamera cameraGroup : cameraGroupList){
            cameraGroupIdList.add(cameraGroup.getId());
        }
        return cameraGroupIdList;
    }

    public ServerResponse delete(String groupCameraIds){
        List<String> groupCameraIdList = Splitter.on(",").splitToList(groupCameraIds);
        if(CollectionUtils.isEmpty(groupCameraIdList)){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        for(String cameraGroupId : groupCameraIdList){
            if(!StringUtils.isEmpty(cameraGroupId)){
                TbCameraExample cameraExample = new TbCameraExample();
                TbCameraExample.Criteria camC = cameraExample.createCriteria();
                camC.andGroupIdEqualTo(Integer.parseInt(cameraGroupId));
                List<TbCamera> camList = cameraMapper.selectByExample(cameraExample);
                if(camList.size()>0){
                    TbCamera camera = camList.get(0);
                    String groupName = camera.getGroupName();
                    return ServerResponse.createByErrorMessage("设备组里还有设备成员,不能删除设备组: "+groupName);
                }
                TbGroupCameraExample groupExample = new TbGroupCameraExample();
                TbGroupCameraExample.Criteria groupC = groupExample.createCriteria();
                groupC.andIdEqualTo(Integer.parseInt(cameraGroupId));
                groupCameraMapper.deleteByExample(groupExample);
            }
        }
        return ServerResponse.createBySuccess();
    }

    public ServerResponse<TbGroupCamera> update(TbGroupCamera groupCamera) {
        int updateCount = groupCameraMapper.updateByPrimaryKeySelective(groupCamera);
        if (updateCount > 0) {
            TbGroupCamera camGroup = groupCameraMapper.selectByPrimaryKey(groupCamera.getId());
            removeRedisCameraGroup(camGroup);
            return ServerResponse.createBySuccess("更新groupCamera信息成功", groupCamera);
        }
        return ServerResponse.createByErrorMessage("更新groupCamera信息失败");
    }

    private void removeRedisCameraGroup(TbGroupCamera camGroup){
        String redisKey = config.getCameraGroupById();
        String camGroupId = camGroup.getId().toString();
        if (redisTemplate.opsForHash().hasKey(redisKey, camGroupId)) {
            redisTemplate.opsForHash().delete(redisKey,camGroupId);
        }
    }

    @Override
    public ServerResponse<Map<String,List<CameraVO>>> getAllCameras(String status) {
        List<String>  keyList = new ArrayList<>();
        Map<String,List<CameraVO>> allCamera = new HashMap<>();
        TbGroupCameraExample example = new TbGroupCameraExample();
        List<TbGroupCamera> groupCameraList = groupCameraMapper.selectByExample(example);
        if(groupCameraList.size()>0){
            for(TbGroupCamera groupCamera : groupCameraList){
                keyList.add(groupCamera.getName()+","+groupCamera.getId());
            }
           for(String key : keyList){
               TbCameraExample cexm = new TbCameraExample();
               TbCameraExample.Criteria c = cexm.createCriteria();
               c.andGroupIdEqualTo(Integer.parseInt(key.split(",")[1]));
               if(!StringUtils.isEmpty(status)){
                   if(status.equals("activated")){
                       c.andCameraStatusEqualTo(1);
                   }
               }
               List<TbCamera> cameraList = cameraMapper.selectByExample(cexm);
               List<CameraVO> cameraVOList = new ArrayList<>();
               if(cameraList.size()>0){
                   for(TbCamera camera : cameraList){
                       CameraVO cameraVO = new CameraVO();
                       BeanUtils.copyProperties(camera, cameraVO, "");
                       long snapCount = mongoDBService.getSnapCountByCameraSdkId(camera.getSdkId());
                       cameraVO.setSnapCount(snapCount);
                       cameraVOList.add(cameraVO);
                   }
               }
               allCamera.put(key, cameraVOList);
           }
           return ServerResponse.createBySuccess(allCamera);
        }
        return ServerResponse.createByError();
    }

    public TbGroupCamera getGroupCameraById(String cameraGroupId) {
        String redisKey = config.getCameraGroupById();
        if (redisTemplate.opsForHash().hasKey(redisKey, cameraGroupId)) {
            String cameraGroupStr = redisTemplate.opsForHash().get(redisKey, cameraGroupId).toString();
            TbGroupCamera groupCamera = JSONObject.parseObject(cameraGroupStr,TbGroupCamera.class);
            return groupCamera;
        }
        TbGroupCameraExample example = new TbGroupCameraExample();
        TbGroupCameraExample.Criteria c = example.createCriteria();
        c.andIdEqualTo(Integer.parseInt(cameraGroupId));
        List<TbGroupCamera> groupCameraList = groupCameraMapper.selectByExample(example);
        if (!CollectionUtils.isEmpty(groupCameraList)) {
            TbGroupCamera groupCamera = groupCameraList.get(0);
            redisTemplate.opsForHash().put(redisKey, cameraGroupId, JSONObject.toJSONString(groupCamera));
            return groupCamera;
        }
        return null;
    }

    @Override
    public boolean isCameraGroupNameExist(String cameraGroupName) {
        TbGroupCameraExample example = new TbGroupCameraExample();
        TbGroupCameraExample.Criteria c = example.createCriteria();
        c.andNameEqualTo(cameraGroupName);
        List<TbGroupCamera> groupCameraList = groupCameraMapper.selectByExample(example);
        if(!CollectionUtils.isEmpty(groupCameraList)){
            return true;
        }
        return false;
    }
}
