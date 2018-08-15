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
@RequestMapping("/history")
public class HistoryController {
    @Autowired
    private MongoDBService mongoDBService;

    @RequestMapping("/getSnapshotList")
    @ResponseBody
    public ServerResponse getSnapshotList(HttpServletRequest request){
        JSONObject snapshotList = mongoDBService.getSnapshotList(request.getParameterMap());
        if(snapshotList != null){
            return ServerResponse.createBySuccess(snapshotList);
        }
        return ServerResponse.createByErrorMessage("查询mongo snapshot发生错误！");
    }

    @RequestMapping("/getWarningFaceList")
    @ResponseBody
    public ServerResponse getWarningFaceList(HttpServletRequest request){
        JSONObject warningFaceList = mongoDBService.getWarningFaceList(request.getParameterMap());
        if(warningFaceList != null){
            return ServerResponse.createBySuccess(warningFaceList);
        }
        return ServerResponse.createByErrorMessage("查询mongo warningFace发生错误！");
    }

}
