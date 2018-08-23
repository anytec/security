package cn.anytec.security.service.impl;

import cn.anytec.security.common.ResponseCode;
import cn.anytec.security.common.ServerResponse;
import cn.anytec.security.config.GeneralConfig;
import cn.anytec.security.core.enums.SecurityExceptionEnum;
import cn.anytec.security.core.exception.BussinessException;
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
    public ServerResponse<UserVO> login(String uname, String upass, HttpSession session) {

        // 构造查询
        TbUserExample userExample = new TbUserExample();
        TbUserExample.Criteria criteria = userExample.createCriteria();
        TbUserExample.Criteria orCriteria = userExample.or();
        criteria.andUnameEqualTo(uname);
        orCriteria.andAccentEqualTo(uname);
        List<TbUser> users = userMapper.selectByExample(userExample);

        // 登录逻辑判断
        if (users.size() == 0) {
            return ServerResponse.createByErrorMessage("用户名不存在");
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

    public ServerResponse<String> register(TbUser user) {
        ServerResponse validResponse = this.checkUsername(user.getUname());
        if (!validResponse.isSuccess()) {
            return validResponse;
        }
        user.setRole(config.getUserRole());
        //MD5加密
        user.setUpass(MD5Util.MD5EncodeUtf8(user.getUpass()+config.getPasswordSalt()));
        int resultCount = userMapper.insert(user);
        if (resultCount == 0) {
            return ServerResponse.createByErrorMessage("注册失败");
        }
        return ServerResponse.createBySuccessMessage("注册成功");
    }

    public ServerResponse<PageInfo> list(int pageNum, int pageSize, String keyword) {
        PageHelper.startPage(pageNum, pageSize);
        TbUserExample example = new TbUserExample();
        // 用于关键字查询
        if (StringUtils.isNoneBlank(keyword)) {
            TbUserExample.Criteria criteria = example.createCriteria();
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

    public ServerResponse<String> checkUsername(String username) {
        //开始校验
        TbUserExample example = new TbUserExample();
        TbUserExample.Criteria c = example.createCriteria();
        c.andUnameEqualTo(username);
        List<TbUser> userList = userMapper.selectByExample(example);
        if (userList.size() > 0) {
            return ServerResponse.createByErrorMessage("用户名已存在");
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
            c.andIdEqualTo(Integer.valueOf(userId));
            if (userId.equals(String.valueOf(currentUser.getId()))) {
                throw new BussinessException(SecurityExceptionEnum.SERVER_ERROR.getCode(), "不能删除当前管理员");
            }
            userMapper.deleteByExample(example);
        }
        return ServerResponse.createBySuccess();
    }

    public ServerResponse update(TbUser user) {
        TbUser updateUser = new TbUser();
        updateUser.setId(user.getId());
        updateUser.setNotes(user.getNotes());
        updateUser.setUpass(user.getUpass() == null ? null : MD5Util.MD5EncodeUtf8(user.getUpass() + config.getPasswordSalt()));
        int updateCount = userMapper.updateByPrimaryKeySelective(updateUser);
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
        BeanUtils.copyProperties(user, userVO, "role");
        userVO.setRole(Contrast.parseRole(user.getRole()));
        return ServerResponse.createBySuccess(userVO);
    }

    public ServerResponse checkAdminRole(TbUser user) {
        if (user != null && user.getRole().intValue() == config.getAdminRole()) {
            return ServerResponse.createBySuccess();
        }
        return ServerResponse.createByError();
    }
}
