package cn.anytec.security.service.impl;

import cn.anytec.security.common.ResponseCode;
import cn.anytec.security.common.ServerResponse;
import cn.anytec.security.config.GeneralConfig;
import cn.anytec.security.dao.TbGroupPersonMapper;
import cn.anytec.security.model.TbGroupPerson;
import cn.anytec.security.model.TbGroupPersonExample;
import cn.anytec.security.service.GroupPersonService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.base.Splitter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;

@Service("GroupPersonService")
public class GroupPersonServiceImpl implements GroupPersonService {
    @Autowired
    private TbGroupPersonMapper groupPersonMapper;

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
        List<TbGroupPerson> personList = groupPersonMapper.selectByExample(example);
        PageInfo pageResult = new PageInfo(personList);
        return ServerResponse.createBySuccess(pageResult);
    }

    @Override
    public ServerResponse<String> getGroupName(Integer groupId) {
        TbGroupPersonExample example = new TbGroupPersonExample();
        TbGroupPersonExample.Criteria c = example.createCriteria();
        c.andIdEqualTo(groupId);
        List<TbGroupPerson> personList = groupPersonMapper.selectByExample(example);
        if(personList.size() > 0){
            return ServerResponse.createBySuccess(personList.get(0).getName());
        }
        return ServerResponse.createByErrorMessage("底库不存在！");
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
}
