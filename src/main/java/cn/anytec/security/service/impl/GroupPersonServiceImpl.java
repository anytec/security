package cn.anytec.security.service.impl;

import cn.anytec.security.common.ServerResponse;
import cn.anytec.security.config.GeneralConfig;
import cn.anytec.security.dao.TbGroupPersonMapper;
import cn.anytec.security.dao.TbPersonMapper;
import cn.anytec.security.model.TbGroupPerson;
import cn.anytec.security.model.TbGroupPersonExample;
import cn.anytec.security.model.TbPerson;
import cn.anytec.security.model.TbPersonExample;
import cn.anytec.security.service.GroupPersonService;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.base.Splitter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service("GroupPersonService")
public class GroupPersonServiceImpl implements GroupPersonService {
    @Autowired
    private TbGroupPersonMapper groupPersonMapper;
    @Autowired
    private TbPersonMapper personMapper;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private GeneralConfig config;

    public ServerResponse add(TbGroupPerson groupPerson) {
        int updateCount = groupPersonMapper.insertSelective(groupPerson);
        if (updateCount > 0) {
            return ServerResponse.createBySuccess("新增groupPerson成功", groupPerson);
        }
        return ServerResponse.createByErrorMessage("新增groupPerson失败");
    }

    public ServerResponse<PageInfo> list(Integer pageNum, Integer pageSize,String groupName) {
        if(pageNum != 0 && pageSize != 0){
            PageHelper.startPage(pageNum, pageSize);
        }
        TbGroupPersonExample example = new TbGroupPersonExample();
        TbGroupPersonExample.Criteria c = example.createCriteria();
        if(groupName != null){
            c.andNameLike("%"+groupName.trim()+"%");
        }
        List<TbGroupPerson> personGroupList = groupPersonMapper.selectByExample(example);
        for(TbGroupPerson personGroup : personGroupList){
            getPersonGroupNum(personGroup);
        }
        PageInfo pageResult = new PageInfo(personGroupList);
        return ServerResponse.createBySuccess(pageResult);
    }

    private void getPersonGroupNum(TbGroupPerson personGroup){
        TbPersonExample example = new TbPersonExample();
        TbPersonExample.Criteria c = example.createCriteria();
        c.andGroupIdEqualTo(personGroup.getId());
        List<TbPerson> personList = personMapper.selectByExample(example);
        if(!CollectionUtils.isEmpty(personList)){
            personGroup.setTotalNumber(personList.size());
        }else {
            personGroup.setTotalNumber(0);
        }
    }

    public ServerResponse delete(String groupPersonIds){
        List<String> groupPersonIdList = Splitter.on(",").splitToList(groupPersonIds);
        for(String personGroupId : groupPersonIdList){
            if(!StringUtils.isEmpty(personGroupId)){
                TbGroupPersonExample example = new TbGroupPersonExample();
                TbGroupPersonExample.Criteria c = example.createCriteria();
                c.andIdEqualTo(Integer.parseInt(personGroupId));
                groupPersonMapper.deleteByExample(example);
            }
        }
        return ServerResponse.createBySuccess();
    }

    public ServerResponse<TbGroupPerson> update(TbGroupPerson groupPerson) {
        int updateCount = groupPersonMapper.updateByPrimaryKeySelective(groupPerson);
        if (updateCount > 0) {
            return ServerResponse.createBySuccess("更新groupPerson信息成功", groupPerson);
        }
        return ServerResponse.createByErrorMessage("更新groupPerson信息失败");
    }

    public ServerResponse<TbGroupPerson> getGroupPersonById(String personGroupId) {
        String redisKey = config.getPersonGroupById();
        if (redisTemplate.opsForHash().hasKey(redisKey, personGroupId)) {
            String groupPersonStr= (String)redisTemplate.opsForHash().get(redisKey, personGroupId);
            redisTemplate.expire(redisKey, 1, TimeUnit.DAYS);
            TbGroupPerson groupPerson = JSONObject.parseObject(groupPersonStr,TbGroupPerson.class);
            return ServerResponse.createBySuccess("getGroupPersonById返回成功", groupPerson);
        }
        TbGroupPersonExample example = new TbGroupPersonExample();
        TbGroupPersonExample.Criteria c = example.createCriteria();
        c.andIdEqualTo(Integer.parseInt(personGroupId));
        List<TbGroupPerson> groupCameraList = groupPersonMapper.selectByExample(example);
        if (!CollectionUtils.isEmpty(groupCameraList)) {
            TbGroupPerson groupPerson = groupCameraList.get(0);
            redisTemplate.opsForHash().put(redisKey, personGroupId, JSONObject.toJSONString(groupPerson));
            return ServerResponse.createBySuccess("getGroupPersonById返回成功", groupPerson);
        }
        return ServerResponse.createByErrorMessage("getGroupPersonById未查到符合条件的信息");
    }
}
