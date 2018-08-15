package cn.anytec.security.controller;

import cn.anytec.security.common.ServerResponse;
import cn.anytec.security.model.TbUser;
import cn.anytec.security.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @RequestMapping(value = "/login",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<TbUser> login(String username, String password, HttpSession session){
        ServerResponse<TbUser> response = userService.login(username,password);
        if(response.isSuccess()){
            session.setAttribute("currentUser",response.getData());
        }
        return response;
    }

    @RequestMapping(value = "/logout",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> logout(HttpSession session){
        session.removeAttribute("currentUser");
        return ServerResponse.createBySuccess();
    }

    @RequestMapping(value = "/register",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> register(TbUser user){
        return userService.register(user);
    }


    @RequestMapping(value = "/checkUsername",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> checkUsername(String username){
        return userService.checkUsername(username);
    }


    @RequestMapping(value = "/getUserInfo",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<TbUser> getUserInfo(){
        return ServerResponse.createByErrorMessage("用户未登录,无法获取当前用户的信息");
    }

    @RequestMapping(value = "/update",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<TbUser> update(TbUser user){
        ServerResponse<TbUser> response = userService.update(user);
        return response;
    }

    @RequestMapping(value = "/list",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse list(@RequestParam(value = "pageNum",defaultValue = "1") int pageNum, @RequestParam(value = "pageSize",defaultValue = "10") int pageSize){
        return userService.list(pageNum,pageSize);
    }


    @RequestMapping(value = "/delete",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse delete(String userIds){
        return userService.delete(userIds);
    }

}
