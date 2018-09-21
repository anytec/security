package cn.anytec.security.controller;

import cn.anytec.security.common.ServerResponse;
import cn.anytec.security.core.annotion.OperLog;
import cn.anytec.security.core.annotion.Permission;
import cn.anytec.security.core.enums.PermissionType;
import cn.anytec.security.model.TbUser;
import cn.anytec.security.model.dto.UserDTO;
import cn.anytec.security.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @OperLog(value = "登录用户", key = "account")
    @PostMapping("/login")
    public ServerResponse<UserDTO> login(String account, String upass, HttpSession session){
        return userService.login(account,upass, session);
    }

    @PostMapping("/logout")
    public ServerResponse<String> logout(HttpSession session){
        session.removeAttribute("currentUser");
        return ServerResponse.createBySuccess();
    }

    @OperLog(value = "注册用户", key = "uname,notes")
    @Permission(value = "注册用户", method = PermissionType.IS_ADMIN)
    @PostMapping("/register")
    public ServerResponse register(TbUser user){
        return userService.register(user);
    }

    @PostMapping("/checkUsername")
    public ServerResponse<String> checkUsername(String username){
        return userService.checkaccount(username);
    }

    //@OperLog("查询用户详情信息")
    @PostMapping("/getUserInfo")
    public ServerResponse<UserDTO> getUserInfo(@RequestParam(value = "id") Integer id){
        ServerResponse<UserDTO> response = userService.getInformation(id);
        return response;
    }

    @OperLog(value = "修改用户信息", key = "avatar,uname,contact,notes")
    @Permission(value = "修改用户信息", method = PermissionType.IS_ADMIN)
    @PostMapping("/update")
    public ServerResponse update(TbUser user){
//        return userService.update(userVO);
        if (user != null && user.getId() > 0) {
            return userService.update(user);
        }else {
            return ServerResponse.createByErrorMessage("用户ID不能为空");
        }
    }

    //@OperLog(value = "查询用户列表", key = "pageNum,pageSize,keyword")
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
