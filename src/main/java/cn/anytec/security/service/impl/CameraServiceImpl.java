package cn.anytec.security.service.impl;

import cn.anytec.security.common.ServerResponse;
import cn.anytec.security.component.CameraStreamMonitor;
import cn.anytec.security.config.GeneralConfig;
import cn.anytec.security.dao.TbCameraMapper;
import cn.anytec.security.model.TbCamera;
import cn.anytec.security.model.TbCameraExample;
import cn.anytec.security.service.CameraService;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.base.Splitter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Service("CameraService")
public class CameraServiceImpl implements CameraService {
    private static final Logger logger = LoggerFactory.getLogger(CameraServiceImpl.class);
    @Autowired
    private TbCameraMapper cameraMapper;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private CameraStreamMonitor cameraStreamMonitor;
    @Autowired
    private GeneralConfig config;

    @Value("${camera.rtmpPrefix}")
    private String rtmpPrefix;

    //添加摄像头
    public ServerResponse<String> add(TbCamera camera) {
        if (camera != null && camera.getServerLabel() != null) {
            String cameraSdkId = UUID.randomUUID().toString();
            camera.setSdkId(cameraSdkId);
            camera.setPlayAddress(rtmpPrefix + cameraSdkId);
            logger.debug("add camera server label:" + camera.getServerLabel());
            int updateCount = cameraMapper.insertSelective(camera);
            if (updateCount > 0) {
                return ServerResponse.createBySuccess("新增camera成功", cameraSdkId);
            }
        }
        return ServerResponse.createByErrorMessage("新增camera失败");
    }

    public ServerResponse delete(String cameraIds) {
        List<String> cameraIdList = Splitter.on(",").splitToList(cameraIds);
        for (String cameraId : cameraIdList) {
            if (!StringUtils.isEmpty(cameraId)) {
                TbCameraExample example = new TbCameraExample();
                TbCameraExample.Criteria c = example.createCriteria();
                c.andIdEqualTo(Integer.parseInt(cameraId));
                cameraMapper.deleteByExample(example);
            }
        }
        return ServerResponse.createBySuccess();
    }

    public List<TbCamera> list(int pageNum, int pageSize, String name, Integer groupId,
                               String type, String serverLabel, Integer status, String cameraSdkId) {
        PageHelper.startPage(pageNum, pageSize);
        TbCameraExample example = new TbCameraExample();
        TbCameraExample.Criteria c = example.createCriteria();
        if (!StringUtils.isEmpty(name)) {
            c.andNameLike("%" + name + "%");
        }
        if (groupId != null) {
            c.andGroupIdEqualTo(groupId);
        }
        if (!StringUtils.isEmpty(type)) {
            c.andCameraTypeEqualTo(type);
        }
        if (!StringUtils.isEmpty(serverLabel)) {
            c.andServerLabelEqualTo(serverLabel);
        }
        if (status != null) {
            c.andCameraStatusEqualTo(status);
        }
        if (!StringUtils.isEmpty(cameraSdkId)) {
            c.andSdkIdEqualTo(cameraSdkId);
        }
        List<TbCamera> cameraList = cameraMapper.selectByExample(example);
        return cameraList;
    }

    public ServerResponse<TbCamera> update(TbCamera camera) {
        int updateCount = cameraMapper.updateByPrimaryKeySelective(camera);
        if (updateCount > 0) {
            TbCamera cam = cameraMapper.selectByPrimaryKey(camera.getId());
            changeRedisCameraInfo(cam);
            return ServerResponse.createBySuccess("更新camera信息成功", camera);
        }
        return ServerResponse.createByErrorMessage("更新camera信息失败");
    }

    private void changeRedisCameraInfo(TbCamera camera){
        String redisKey = config.getCameraBySdkId();
        String cameraSdkId = camera.getSdkId();
        if (redisTemplate.opsForHash().hasKey(redisKey, cameraSdkId)) {
            redisTemplate.opsForHash().put(redisKey, cameraSdkId, JSONObject.toJSONString(camera));
            redisTemplate.expire(redisKey, 1, TimeUnit.DAYS);
        }
    }

    public TbCamera getCameraBySdkId(String sdkId) {
        String redisKey = config.getCameraBySdkId();
        if (redisTemplate.opsForHash().hasKey(redisKey, sdkId)) {
            String cameraStr =  redisTemplate.opsForHash().get(redisKey, sdkId).toString();
            TbCamera camera = JSONObject.parseObject(cameraStr,TbCamera.class);
            return camera;
        }
        TbCameraExample example = new TbCameraExample();
        TbCameraExample.Criteria c = example.createCriteria();
        c.andSdkIdEqualTo(sdkId);
        List<TbCamera> cameraList = cameraMapper.selectByExample(example);
        if (!CollectionUtils.isEmpty(cameraList)) {
            TbCamera camera = cameraList.get(0);
            redisTemplate.opsForHash().put(redisKey, sdkId, JSONObject.toJSONString(camera));
            redisTemplate.expire(redisKey, 1, TimeUnit.DAYS);
            return camera;
        }
        return null;
    }

    public String cameras() {
        TbCameraExample example = new TbCameraExample();
        example.createCriteria().andCameraStatusEqualTo(1).andCameraTypeEqualTo("视频流");

        List<TbCamera> cameraList = cameraMapper.selectByExample(example);
        List list = new ArrayList();
        //将库中激活的摄像头筛选后放到集合中
        cameraList.forEach(tbCamera -> {
            Map object = new HashMap();
            object.put("detector", tbCamera.getServerLabel());
            object.put("id", tbCamera.getSdkId());
            object.put("url", tbCamera.getStreamAddress());
            list.add(object);
        });
        System.out.println(JSONArray.toJSONString(list));
        return JSONArray.toJSONString(list);
    }

    public ServerResponse<List<String>> getServerLabel() {
        List<String> resultList = new ArrayList<>();

        List<Map<String, Object>> serverLabelList = cameraMapper.selectServerLabel();
        if (serverLabelList.size() > 0) {
            for (Map<String, Object> map : serverLabelList) {
                resultList.add(map.get("serverLabel").toString());
            }
            return ServerResponse.createBySuccess(resultList);
        }
        return ServerResponse.createByError();
    }

    @Override
    public String connect(int cameraId) {
        TbCamera camera = getById(cameraId);
        //如果redis列表中已经存在，说明该摄像头的推流进程已经启动，只需要将连接数加1
//        camera.setPlayAddress(camera.getPlayAddress()+new Date().getTime());
        cameraStreamMonitor.newConnect(camera);
//        if(!cameraStreamMonitor.newConnect(camera)){
//            addCameraConnect(camera.getSdkId());
//        }
        return rtmpPrefix + camera.getPlayAddress();
    }

//    @Override
//    public ServerResponse addCameraConnect(String cameraId) {
////        redisTemplate.opsForHash().increment("stream-active",camera.getSdkId(),1);
////        if(redisTemplate.opsForHash().h.containsKey(cameraId)){
////            Integer conNum = Integer.parseInt(String.valueOf(map.get(cameraId)))+1;
////            map.put(cameraId,conNum);
////        }else{
////            map.put(cameraId,1);
////        }
////        redisService.putMap("stream-list",map);
////        return null;
//    }

    @Override
    public ServerResponse deleteCameraConnect(int cameraId) {
        cameraStreamMonitor.disconnect(getById(cameraId).getSdkId());
        return ServerResponse.createBySuccess();
    }

    @Override
    public ServerResponse killCameraProcess(int id) {
        TbCamera camera = getById(id);
        cameraStreamMonitor.destory(camera.getSdkId());

        return ServerResponse.createBySuccess();
    }


    public TbCamera getById(int cameraId) {
        TbCameraExample example = new TbCameraExample();
        TbCameraExample.Criteria c = example.createCriteria();
        return cameraMapper.selectByPrimaryKey(cameraId);
    }
}
