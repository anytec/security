package cn.anytec.security.controller;

import cn.anytec.security.common.ServerResponse;
import cn.anytec.security.core.annotion.OperLog;
import cn.anytec.security.model.TbGroupPerson;
import cn.anytec.security.service.GroupPersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/groupPerson")
public class GroupPersonController {
    @Autowired
    private GroupPersonService groupPersonService;

    @OperLog(value = "添加人员底库", key="id,name")
    @RequestMapping("/add")
    @ResponseBody
    public ServerResponse add(TbGroupPerson groupPerson){
        return groupPersonService.add(groupPerson);
    }

    @OperLog(value = "删除人员底库", key = "groupPersonIds")
    @RequestMapping("/delete")
    @ResponseBody
    public ServerResponse delete(@RequestParam(value = "groupPersonIds") String groupPersonIds){
        return groupPersonService.delete(groupPersonIds);
    }

    @RequestMapping("/list")
    @ResponseBody
    public ServerResponse list(@RequestParam(value = "pageNum",defaultValue = "0") Integer pageNum, @RequestParam(value = "pageSize",defaultValue = "0") Integer pageSize,
                               @RequestParam(value = "groupName",required = false)String groupName){
        return groupPersonService.list(pageNum,pageSize,groupName);
    }

    @OperLog(value = "修改人员底库信息", key="id,name")
    @RequestMapping("/update")
    @ResponseBody
    public ServerResponse update(TbGroupPerson groupPerson){
        return groupPersonService.update(groupPerson);
    }
}
