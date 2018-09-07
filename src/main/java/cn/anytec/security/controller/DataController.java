package cn.anytec.security.controller;

import cn.anytec.security.common.ServerResponse;
import cn.anytec.security.component.ServerStatus;
import cn.anytec.security.component.mongo.MongoDBService;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
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
    @Autowired
    ServerStatus serverStatus;

    @RequestMapping("/snapCounting")
    @ResponseBody
    public ServerResponse snapCounting(){
        JSONObject result = mongoDBService.snapCounting();
        if(result != null){
            return ServerResponse.createBySuccess(result);
        }
        return ServerResponse.createByErrorMessage("snapCounting发生错误！");
    }

    //改peopleCounting
    @RequestMapping("/peopleCounting")
    @ResponseBody
    public ServerResponse peopleCounting(HttpServletRequest request){
        JSONObject result = mongoDBService.peopleCounting(request.getParameterMap());
        if(result != null){
            return ServerResponse.createBySuccess(result);
        }
        return ServerResponse.createByErrorMessage("peopleCounting发生错误！");
    }

    @RequestMapping("/peopleAnalysis")
    @ResponseBody
    public ServerResponse peopleAnalysis(HttpServletRequest request){
        JSONObject result = mongoDBService.peopleAnalysis(request.getParameterMap());
        if(result != null){
            return ServerResponse.createBySuccess(result);
        }
        return ServerResponse.createByErrorMessage("peopleAnalysis发生错误！");
    }

    @RequestMapping("/serverStatus")
    @ResponseBody
    public ServerResponse ServerStatus(){
        String memoryStatus = serverStatus.getMemoryStatus();
        if(!StringUtils.isEmpty(memoryStatus)){
            return ServerResponse.createBySuccess(memoryStatus);
        }
        return ServerResponse.createByErrorMessage("peopleAnalysis发生错误！");
    }
}
