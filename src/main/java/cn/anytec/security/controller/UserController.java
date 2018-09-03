package cn.anytec.security.controller;

import cn.anytec.security.common.ServerResponse;
import cn.anytec.security.core.annotion.OperLog;
import cn.anytec.security.core.annotion.Permission;
import cn.anytec.security.core.enums.PermissionType;
import cn.anytec.security.core.log.LogObjectHolder;
import cn.anytec.security.model.TbUser;
import cn.anytec.security.model.vo.UserVO;
import cn.anytec.security.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ServerResponse<UserVO> login(String uname, String upass, HttpSession session){
        return userService.login(uname,upass, session);
    }

    @PostMapping("/logout")
    public ServerResponse<String> logout(HttpSession session){
        session.removeAttribute("currentUser");
        return ServerResponse.createBySuccess();
    }

    @OperLog(value = "注册用户", key = "uname,notes")
    @Permission(value = "注册用户", method = PermissionType.IS_ADMIN)
    @PostMapping("/register")
    public ServerResponse<String> register(TbUser user){
        return userService.register(user);
    }

    @PostMapping("/checkUsername")
    public ServerResponse<String> checkUsername(String username){
        return userService.checkUsername(username);
    }

    @OperLog("查询用户详情信息")
    @PostMapping("/getUserInfo")
    public ServerResponse<UserVO> getUserInfo(@RequestParam(value = "id") Integer id){
        ServerResponse<UserVO> response = userService.getInformation(id);
        LogObjectHolder.me().set(response.getData());
        return response;
    }

    @OperLog("修改用户信息")
    @Permission(value = "修改用户信息", method = PermissionType.IS_ADMIN)
    @PostMapping("/update")
    public ServerResponse update(TbUser user){
        return userService.update(user);
    }

    @OperLog(value = "查询用户列表", key = "pageNum,pageSize,keyword")
    @Permission(value = "查询用户列表", method = PermissionType.IS_ADMIN)
    @PostMapping("/list")
    public ServerResponse list(@RequestParam(value = "pageNum",defaultValue = "1") int pageNum,
                               @RequestParam(value = "pageSize",defaultValue = "10") int pageSize,
                               @RequestParam(value = "keyword", required = false) String keyword){
        return userService.list(pageNum,pageSize, keyword);
    }

    @OperLog(value = "删除用户", key = "userIds")
    @Permission(value = "删除用户", method = PermissionType.IS_ADMIN)
    @PostMapping("/delete")
    public ServerResponse delete(String userIds, HttpSession session){
        return userService.delete(userIds, session);
    }

}
