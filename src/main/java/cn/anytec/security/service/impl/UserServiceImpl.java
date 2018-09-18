package cn.anytec.security.service.impl;

import cn.anytec.security.common.ResponseCode;
import cn.anytec.security.common.ServerResponse;
import cn.anytec.security.config.GeneralConfig;
import cn.anytec.security.core.enums.SecurityExceptionEnum;
import cn.anytec.security.core.enums.UserStatus;
import cn.anytec.security.core.exception.BussinessException;
import cn.anytec.security.core.log.LogObjectHolder;
import cn.anytec.security.core.util.Contrast;
import cn.anytec.security.dao.TbUserMapper;
import cn.anytec.security.model.TbUser;
import cn.anytec.security.model.TbUserExample;
import cn.anytec.security.model.vo.UserVO;
import cn.anytec.security.service.UserService;
import cn.anytec.security.util.MD5Util;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.base.Splitter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@Service("UserService")
public class UserServiceImpl implements UserService {
    @Autowired
    private TbUserMapper userMapper;
    @Autowired
    private GeneralConfig config;

    @Override
    public ServerResponse<UserVO> login(String account, String upass, HttpSession session) {

        // 构造查询
        TbUserExample userExample = new TbUserExample();
        TbUserExample.Criteria criteria = userExample.createCriteria();
        criteria.andaccountEqualTo(account);
        criteria.andStatusEqualTo(UserStatus.ENABLE);
        List<TbUser> users = userMapper.selectByExample(userExample);

        // 登录逻辑判断
        if (users.size() == 0) {
            return ServerResponse.createByErrorMessage("账号不存在");
        }
        String md5Pwd = MD5Util.MD5EncodeUtf8(upass + config.getPasswordSalt());
        criteria.andUpassEqualTo(md5Pwd);
        List<TbUser> userList = userMapper.selectByExample(userExample);
        if (userList.size() == 0) {
            return ServerResponse.createByErrorMessage("密码错误");
        }
        TbUser currentUser = userList.get(0);

        // 登录成功后系统处理
        session.setAttribute("currentUser",currentUser);
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(currentUser, userVO, "role");
        userVO.setRole(Contrast.parseRole(currentUser.getRole()));

        return ServerResponse.createBySuccess("登录成功", userVO);
    }

    public ServerResponse register(TbUser user) {
        ServerResponse validResponse = this.checkaccount(user.getAccount());
        if (!validResponse.isSuccess()) {
            return validResponse;
        }
        user.setStatus(UserStatus.ENABLE);
        user.setRole(config.getUserRole());
        //MD5加密
        user.setUpass(MD5Util.MD5EncodeUtf8(user.getUpass() + config.getPasswordSalt()));
        int resultCount = userMapper.insert(user);
        if (resultCount == 0) {
            return ServerResponse.createByErrorMessage("注册失败");
        }
        return ServerResponse.createBySuccessMessage("注册成功");
    }

    public ServerResponse<PageInfo> list(int pageNum, int pageSize, String keyword) {
        PageHelper.startPage(pageNum, pageSize);
        TbUserExample example = new TbUserExample();
        TbUserExample.Criteria criteria = example.createCriteria();
        criteria.andStatusEqualTo(UserStatus.ENABLE);
        // 用于关键字查询
        if (StringUtils.isNoneBlank(keyword)) {
            criteria.andUnameLike("%" + keyword + "%");
        }
        List<TbUser> userList = userMapper.selectByExample(example);

        // 数据组装
        ArrayList<UserVO> userVOList = new ArrayList<>();
        if (null != userList && userList.size() > 0) {
            userList.forEach(tbUser -> {
                UserVO userVO = new UserVO();
                BeanUtils.copyProperties(tbUser, userVO, "role");
                userVO.setRole(Contrast.parseRole(tbUser.getRole()));
                userVOList.add(userVO);
            });
        }
        return ServerResponse.createBySuccess(PageInfo.of(userVOList));
    }

    public ServerResponse<String> checkaccount(String account) {
        //开始校验
        TbUserExample example = new TbUserExample();
        TbUserExample.Criteria c = example.createCriteria();
        c.andStatusEqualTo(UserStatus.ENABLE);
        c.andaccountEqualTo(account);
        List<TbUser> userList = userMapper.selectByExample(example);
        if (userList.size() > 0) {
            return ServerResponse.createByErrorMessage("账号已存在");
        }
        return ServerResponse.createBySuccessMessage("校验成功");
    }

    @Transactional
    public ServerResponse delete(String userIds, HttpSession session){
        List<String> userIdList = Splitter.on(",").splitToList(userIds);
        if(CollectionUtils.isEmpty(userIdList)){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        TbUser currentUser = (TbUser) session.getAttribute("currentUser");
        for(String userId : userIdList){
            TbUserExample example = new TbUserExample();
            TbUserExample.Criteria c = example.createCriteria();
            c.andStatusEqualTo(UserStatus.ENABLE);
            c.andIdEqualTo(Integer.valueOf(userId));
            TbUser updateUser = new TbUser();
            updateUser.setStatus(UserStatus.DELETED);
            if (userId.equals(String.valueOf(currentUser.getId()))) {
                throw new BussinessException(SecurityExceptionEnum.SERVER_ERROR.getCode(), "不能删除当前管理员");
            }
            int update = userMapper.updateByExampleSelective(updateUser, example);
            if (update == 0) {
                return ServerResponse.createByErrorMessage("不存在该用户");
            }
        }
        return ServerResponse.createBySuccessMessage("用户删除成功");
    }

    public ServerResponse update(TbUser user) {
        TbUser selectByPrimaryKey = userMapper.selectByPrimaryKey(user.getId());
        if (selectByPrimaryKey == null || selectByPrimaryKey.getStatus().equals(UserStatus.DELETED)) {
            return ServerResponse.createByErrorMessage("用户不存在");
        }

        TbUser updateUser = new TbUser();
        BeanUtils.copyProperties(user, updateUser, "id","upass", "account", "role");
        updateUser.setAccount(selectByPrimaryKey.getAccount());
        updateUser.setUpass(user.getUpass() == null ? null : MD5Util.MD5EncodeUtf8(user.getUpass() + config.getPasswordSalt()));
        TbUserExample example = new TbUserExample();
        TbUserExample.Criteria criteria = example.createCriteria();
        criteria.andStatusEqualTo(UserStatus.ENABLE);
        criteria.andIdEqualTo(user.getId());
        int updateCount = userMapper.updateByExampleSelective(updateUser, example);
        if (updateCount > 0) {
            return ServerResponse.createBySuccessMessage("更新个人信息成功");
        }
        return ServerResponse.createByErrorMessage("更新个人信息失败");
    }

    public ServerResponse<UserVO> getInformation(Integer userId) {
        TbUser user = userMapper.selectByPrimaryKey(userId);
        if (user == null) {
            return ServerResponse.createByErrorMessage("找不到当前用户");
        }

        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO, "role","upass");
        userVO.setRole(Contrast.parseRole(user.getRole()));
        LogObjectHolder.me().set(user);
        return ServerResponse.createBySuccess(userVO);
    }

    public ServerResponse checkAdminRole(TbUser user) {
        if (user != null && user.getRole().intValue() == config.getAdminRole()) {
            return ServerResponse.createBySuccess();
        }
        return ServerResponse.createByError();
    }
}
