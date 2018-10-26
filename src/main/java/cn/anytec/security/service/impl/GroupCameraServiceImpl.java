package cn.anytec.security.service.impl;

import cn.anytec.security.common.ResponseCode;
import cn.anytec.security.common.ServerResponse;
import cn.anytec.security.component.ipcamera.ipcService.IPCOperations;
import cn.anytec.security.component.mongo.MongoDBService;
import cn.anytec.security.config.GeneralConfig;
import cn.anytec.security.constant.RedisConst;
import cn.anytec.security.core.enums.CameraType;
import cn.anytec.security.core.exception.BussinessException;
import cn.anytec.security.core.log.LogObjectHolder;
import cn.anytec.security.dao.TbCameraMapper;
import cn.anytec.security.dao.TbGroupCameraMapper;
import cn.anytec.security.model.TbCamera;
import cn.anytec.security.model.TbCameraExample;
import cn.anytec.security.model.TbGroupCamera;
import cn.anytec.security.model.TbGroupCameraExample;
import cn.anytec.security.model.dto.CameraDTO;
import cn.anytec.security.service.GroupCameraService;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.base.Splitter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private final Logger logger = LoggerFactory.getLogger(GroupCameraServiceImpl.class);

    @Autowired
    private TbGroupCameraMapper groupCameraMapper;
    @Autowired
    private TbCameraMapper cameraMapper;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private MongoDBService mongoDBService;
    @Autowired
    private IPCOperations ipcOperations;

    public TbGroupCamera getCameraGroupInfo(Integer cameraGroupId) {
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
        if (pageNum != 0 && pageSize != 0) {
            PageHelper.startPage(pageNum, pageSize);
        }
        TbGroupCameraExample example = new TbGroupCameraExample();
        TbGroupCameraExample.Criteria c = example.createCriteria();
        if (!StringUtils.isEmpty(groupName)) {
            c.andNameLike("%" + groupName + "%");
        }
        List<TbGroupCamera> cameraList = groupCameraMapper.selectByExample(example);
        PageInfo pageResult = new PageInfo(cameraList);
        return ServerResponse.createBySuccess(pageResult);
    }


    public List<Integer> getAllCameraGroupId() {
        TbGroupCameraExample example = new TbGroupCameraExample();
        List<TbGroupCamera> cameraGroupList = groupCameraMapper.selectByExample(example);
        List<Integer> cameraGroupIdList = new ArrayList<>();
        for (TbGroupCamera cameraGroup : cameraGroupList) {
            cameraGroupIdList.add(cameraGroup.getId());
        }
        return cameraGroupIdList;
    }

    public ServerResponse delete(String groupCameraIds) {
        List<String> groupCameraIdList = Splitter.on(",").splitToList(groupCameraIds);
        if (CollectionUtils.isEmpty(groupCameraIdList)) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        for (String cameraGroupId : groupCameraIdList) {
            if (!StringUtils.isEmpty(cameraGroupId)) {
                List<TbCamera> camList = getCameraListByGroupId(cameraGroupId);
                if (camList.size() > 0) {
                    return ServerResponse.createByErrorMessage("设备组里还有设备成员,不能删除设备组");
                }
                TbGroupCameraExample groupExample = new TbGroupCameraExample();
                TbGroupCameraExample.Criteria groupC = groupExample.createCriteria();
                groupC.andIdEqualTo(Integer.parseInt(cameraGroupId));
                groupCameraMapper.deleteByExample(groupExample);
                logger.info("【deleteMysqlCameraGroup】{}",cameraGroupId);

                deleteRedisCameraGroup(cameraGroupId);
            }
        }
        return ServerResponse.createBySuccess();
    }

    private List<TbCamera> getCameraListByGroupId(String cameraGroupId) {
        TbCameraExample cameraExample = new TbCameraExample();
        TbCameraExample.Criteria camC = cameraExample.createCriteria();
        camC.andGroupIdEqualTo(Integer.parseInt(cameraGroupId));
        return cameraMapper.selectByExample(cameraExample);
    }

    public ServerResponse<TbGroupCamera> update(TbGroupCamera groupCamera) {
        //设备组状态改变，改变组里的设备状态
        if(groupCamera.getGroupStatus() != null){
            String cameraGroupId = groupCamera.getId().toString();
            List<TbCamera> camList = getCameraListByGroupId(cameraGroupId);
            if(camList.size()>0){
                for(TbCamera camera : camList){
                    String cameraSdkId = camera.getSdkId();
                    if(groupCamera.getGroupStatus() == 0){
                        if(camera.getCameraStatus() == 1){
                            if(camera.getCameraType().equals(CameraType.CaptureCamera.getMsg())){
                                if(!StringUtils.isEmpty(cameraSdkId)){
                                    try {
                                        ipcOperations.standbyCaptureCamera(cameraSdkId);
                                    }catch (Exception e){
                                        continue;
                                    }
                                    ipcOperations.addToCache(cameraSdkId);
                                    ipcOperations.deleteFromInUseCache(cameraSdkId);
                                    camera.setCameraStatus(0);
                                }
                            }else {
                                camera.setCameraStatus(0);
                            }
                        }else {
                            continue;
                        }
                    }else if(groupCamera.getGroupStatus() == 1){
                        if(camera.getCameraStatus() == 0){
                            if(camera.getCameraType().equals(CameraType.CaptureCamera.getMsg())){
                                if(!StringUtils.isEmpty(cameraSdkId)){
                                    try {
                                        ipcOperations.activeCaptureCamera(cameraSdkId);
                                    }catch (Exception e){
                                        continue;
                                    }
                                    ipcOperations.addToInUseCache(cameraSdkId);
                                    ipcOperations.deleteFromCache(cameraSdkId);
                                    camera.setCameraStatus(1);
                                }
                            }else {
                                camera.setCameraStatus(1);
                            }
                        }else {
                            continue;
                        }
                    }
                    //更新mysql中设备信息
                    int updateCount = cameraMapper.updateByPrimaryKeySelective(camera);
                    if (updateCount > 0) {
                        //删除redis里的缓存
                        String redisKey = RedisConst.CAMERA_BY_SDKID;
                        if (redisTemplate.opsForHash().hasKey(redisKey, cameraSdkId)) {
                            redisTemplate.opsForHash().delete(redisKey, cameraSdkId);
                            logger.info("【deleteRedisCamera】{}",cameraSdkId);
                        }
                    }
                }
            }
        }
        int updateCount = groupCameraMapper.updateByPrimaryKeySelective(groupCamera);
        if (updateCount > 0) {
            TbGroupCamera camGroup = groupCameraMapper.selectByPrimaryKey(groupCamera.getId());
            deleteRedisCameraGroup(camGroup.getId().toString());
            return ServerResponse.createBySuccess("更新groupCamera信息成功", groupCamera);
        }
        return ServerResponse.createByErrorMessage("更新groupCamera信息失败");
    }

    private void deleteRedisCameraGroup(String camGroupId) {
        String redisKey = RedisConst.CAMERAGROUP_BY_ID;
        if (redisTemplate.opsForHash().hasKey(redisKey, camGroupId)) {
            redisTemplate.opsForHash().delete(redisKey, camGroupId);
            logger.info("【deleteRedisCameraGroup】{}",camGroupId);
        }
    }

    @Override
    public ServerResponse<Map<String, List<CameraDTO>>> getAllCameras(String status) {
        List<String> keyList = new ArrayList<>();
        Map<String, List<CameraDTO>> allCamera = new HashMap<>();
        TbGroupCameraExample example = new TbGroupCameraExample();
        List<TbGroupCamera> groupCameraList = groupCameraMapper.selectByExample(example);
        if (groupCameraList.size() > 0) {
            for (TbGroupCamera groupCamera : groupCameraList) {
                keyList.add(groupCamera.getName() + "," + groupCamera.getId());
            }
            for (String key : keyList) {
                String cameraGroupName = key.split(",")[0];
                String cameraGroupId = key.split(",")[1];
                TbCameraExample cexm = new TbCameraExample();
                TbCameraExample.Criteria c = cexm.createCriteria();
                c.andGroupIdEqualTo(Integer.parseInt(cameraGroupId));
                if (!StringUtils.isEmpty(status)) {
                    if (status.equals("activated")) {
                        c.andCameraStatusEqualTo(1);
                    }
                }
                List<TbCamera> cameraList = cameraMapper.selectByExample(cexm);
                List<CameraDTO> cameraDTOList = new ArrayList<>();
                if (cameraList.size() > 0) {
                    for (TbCamera camera : cameraList) {
                        CameraDTO cameraDTO = new CameraDTO();
                        BeanUtils.copyProperties(camera, cameraDTO);
                        cameraDTO.setGroupName(cameraGroupName);
                        long snapCount = mongoDBService.getSnapCountByCameraSdkId(camera.getSdkId());
                        cameraDTO.setSnapCount(snapCount);
                        cameraDTOList.add(cameraDTO);
                    }
                }
                allCamera.put(key, cameraDTOList);
            }
            return ServerResponse.createBySuccess(allCamera);
        }
        return ServerResponse.createByError();
    }

    public TbGroupCamera getGroupCameraById(String cameraGroupId) {
        String redisKey = RedisConst.CAMERAGROUP_BY_ID;
        if (redisTemplate.opsForHash().hasKey(redisKey, cameraGroupId)) {
            String cameraGroupStr = redisTemplate.opsForHash().get(redisKey, cameraGroupId).toString();
            TbGroupCamera groupCamera = JSONObject.parseObject(cameraGroupStr, TbGroupCamera.class);
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
        if (!CollectionUtils.isEmpty(groupCameraList)) {
            return true;
        }
        return false;
    }
}
