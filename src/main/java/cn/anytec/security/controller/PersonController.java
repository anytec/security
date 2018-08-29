package cn.anytec.security.controller;

import cn.anytec.security.common.ServerResponse;
import cn.anytec.security.model.TbPerson;
import cn.anytec.security.service.PersonService;
import cn.anytec.security.model.vo.PersonVo;
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

    @RequestMapping("/add")
    @ResponseBody
    public ServerResponse add(PersonVo personVo){
        return personService.add(personVo);
    }

    @RequestMapping("/delete")
    @ResponseBody
    public ServerResponse delete(String personSdkIds){
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

    @RequestMapping("/update")
    @ResponseBody
    public ServerResponse update(PersonVo personVo){
        return personService.update(personVo);
    }
}
