package cn.anytec.security.controller;

import cn.anytec.security.common.ServerResponse;
import cn.anytec.security.component.mongo.MongoDBService;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/data")
public class DataController {

    @Autowired
    private MongoDBService mongoDBService;
    
    @RequestMapping("/getPersonCount")
    @ResponseBody
    public ServerResponse getPersonCount(HttpServletRequest request){
        JSONObject result = mongoDBService.getPersonCount(request.getParameterMap());
        if(result != null){
            return ServerResponse.createBySuccess(result);
        }
        return ServerResponse.createByErrorMessage("查询mongo snapshot发生错误！");
    }
}
