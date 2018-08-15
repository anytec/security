package cn.anytec.security.service.impl;

import cn.anytec.security.common.ResponseCode;
import cn.anytec.security.common.ServerResponse;
import cn.anytec.security.config.GeneralConfig;
import cn.anytec.security.dao.TbUserMapper;
import cn.anytec.security.model.TbUser;
import cn.anytec.security.model.TbUserExample;
import cn.anytec.security.service.UserService;
import cn.anytec.security.util.MD5Util;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.base.Splitter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Service("UserService")
public class UserServiceImpl implements UserService {
    @Autowired
    private TbUserMapper userMapper;
    @Autowired
    private GeneralConfig config;


    @Override
    public ServerResponse<TbUser> login(String username, String password) {
        TbUserExample example = new TbUserExample();
        TbUserExample.Criteria c = example.createCriteria();
        c.andUsernameEqualTo(username);
        List<TbUser> userList = userMapper.selectByExample(example);
        if (userList.size() == 0) {
            return ServerResponse.createByErrorMessage("用户名不存在");
        }
        String md5Password = MD5Util.MD5EncodeUtf8(password+config.getPasswordSalt());
        c.andPasswordEqualTo(md5Password);
        List<TbUser> users = userMapper.selectByExample(example);
        if (users.size() == 0) {
            return ServerResponse.createByErrorMessage("密码错误");
        }
        TbUser user = users.get(0);
        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.createBySuccess("登录成功", user);
    }

    public ServerResponse<String> register(TbUser user) {
        ServerResponse validResponse = this.checkUsername(user.getUsername());
        if (!validResponse.isSuccess()) {
            return validResponse;
        }
        user.setRole(config.getUserRole());
        //MD5加密
        user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()+config.getPasswordSalt()));
        int resultCount = userMapper.insert(user);
        if (resultCount == 0) {
            return ServerResponse.createByErrorMessage("注册失败");
        }
        return ServerResponse.createBySuccessMessage("注册成功");
    }

    public ServerResponse<PageInfo> list(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        TbUserExample example = new TbUserExample();
        List<TbUser> userList = userMapper.selectByExample(example);
        PageInfo pageResult = new PageInfo(userList);
        return ServerResponse.createBySuccess(pageResult);
    }

    public ServerResponse<String> checkUsername(String username) {
        //开始校验
        TbUserExample example = new TbUserExample();
        TbUserExample.Criteria c = example.createCriteria();
        c.andUsernameEqualTo(username);
        List<TbUser> userList = userMapper.selectByExample(example);
        if (userList.size() > 0) {
            return ServerResponse.createByErrorMessage("用户名已存在");
        }
        return ServerResponse.createBySuccessMessage("校验成功");
    }

    public ServerResponse delete(String userIds){
        List<String> userIdList = Splitter.on(",").splitToList(userIds);
        if(CollectionUtils.isEmpty(userIdList)){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        for(String userId : userIdList){
            TbUserExample example = new TbUserExample();
            TbUserExample.Criteria c = example.createCriteria();
            c.andIdEqualTo(Integer.parseInt(userId));
            userMapper.deleteByExample(example);
        }
        return ServerResponse.createBySuccess();
    }

    public ServerResponse<TbUser> update(TbUser user) {
        TbUser updateUser = new TbUser();
        updateUser.setId(user.getId());
        updateUser.setRole(user.getRole());
        updateUser.setPassword(user.getPassword());
        int updateCount = userMapper.updateByPrimaryKeySelective(updateUser);
        if (updateCount > 0) {
            return ServerResponse.createBySuccess("更新个人信息成功", updateUser);
        }
        return ServerResponse.createByErrorMessage("更新个人信息失败");
    }

    public ServerResponse<TbUser> getInformation(Integer userId) {
        TbUser user = userMapper.selectByPrimaryKey(userId);
        if (user == null) {
            return ServerResponse.createByErrorMessage("找不到当前用户");
        }
        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.createBySuccess(user);
    }

    public ServerResponse checkAdminRole(TbUser user) {
        if (user != null && user.getRole().intValue() == config.getAdminRole()) {
            return ServerResponse.createBySuccess();
        }
        return ServerResponse.createByError();
    }
}
