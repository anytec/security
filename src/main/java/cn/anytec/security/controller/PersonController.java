package cn.anytec.security.controller;

import cn.anytec.security.common.ServerResponse;
import cn.anytec.security.core.annotion.OperLog;
import cn.anytec.security.model.TbPerson;
import cn.anytec.security.service.PersonService;
import cn.anytec.security.model.vo.PersonVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/person")
public class PersonController {
    @Autowired
    private PersonService personService;

    @OperLog(value = "新增底库人员", key="id,name")
    @RequestMapping("/add")
    @ResponseBody
    public ServerResponse add(PersonVO personVO){
        return personService.add(personVO);
    }

    @OperLog(value = "删除底库人员", key = "personSdkIds")
    @RequestMapping("/delete")
    @ResponseBody
    public ServerResponse delete(@RequestParam(value = "personSdkIds")String personSdkIds){
        return personService.delete(personSdkIds);
    }

    @RequestMapping("/list")
    @ResponseBody
    public ServerResponse list(@RequestParam(value = "pageNum",defaultValue = "1") int pageNum, @RequestParam(value = "pageSize",defaultValue = "10") int pageSize,
                               @RequestParam(value = "name",required = false)String name,
                               @RequestParam(value = "idNumber",required = false)String idNumber,
                               @RequestParam(value = "gender",required = false)String gender,
                               @RequestParam(value = "groupId",required = false)Integer groupId,
                               @RequestParam(value = "faceSdkId",required = false)String faceSdkId){
        TbPerson tbPerson = new TbPerson();
        tbPerson.setName(name);
        tbPerson.setIdNumber(idNumber);
        tbPerson.setGender(gender);
        tbPerson.setGroupId(groupId);
        tbPerson.setSdkId(faceSdkId);
        return personService.list(pageNum,pageSize,tbPerson);
    }

    @OperLog(value = "修改底库人员信息", key="id,name")
    @RequestMapping("/update")
    @ResponseBody
    public ServerResponse update(PersonVO personVO){
        return personService.update(personVO);
    }
}
