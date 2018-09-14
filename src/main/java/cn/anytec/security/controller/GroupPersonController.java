package cn.anytec.security.controller;

import cn.anytec.security.common.ServerResponse;
import cn.anytec.security.core.annotion.OperLog;
import cn.anytec.security.core.annotion.Permission;
import cn.anytec.security.core.enums.PermissionType;
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

    @OperLog(value = "添加底库", key="id,name")
    @RequestMapping("/add")
    @ResponseBody
    @Permission(value = "添加底库", method = PermissionType.IS_ADMIN)
    public ServerResponse add(TbGroupPerson groupPerson){
        String personGroupName = groupPerson.getName();
        if(groupPersonService.isPersonGroupNameExist(personGroupName)){
            return ServerResponse.createByErrorMessage("底库名称 "+personGroupName+" 已存在");
        }
        return groupPersonService.add(groupPerson);
    }

    @OperLog(value = "删除底库", key = "groupPersonIds")
    @RequestMapping("/delete")
    @ResponseBody
    @Permission(value = "删除底库", method = PermissionType.IS_ADMIN)
    public ServerResponse delete(@RequestParam(value = "groupPersonIds") String groupPersonIds){
        return groupPersonService.delete(groupPersonIds);
    }

    @RequestMapping("/list")
    @ResponseBody
//    @Permission(value = "查询底库", method = PermissionType.IS_ADMIN)
    public ServerResponse list(@RequestParam(value = "pageNum",defaultValue = "0") Integer pageNum, @RequestParam(value = "pageSize",defaultValue = "0") Integer pageSize,
                               @RequestParam(value = "groupName",required = false)String groupName){
        return groupPersonService.list(pageNum,pageSize,groupName);
    }

    @OperLog(value = "修改底库", key="id,name")
    @RequestMapping("/update")
    @ResponseBody
    @Permission(value = "修改底库", method = PermissionType.IS_ADMIN)
    public ServerResponse update(TbGroupPerson groupPerson){
        return groupPersonService.update(groupPerson);
    }
}
