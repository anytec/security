package cn.anytec.security.controller;

import cn.anytec.security.common.ServerResponse;
import cn.anytec.security.core.annotion.OperLog;
import cn.anytec.security.core.annotion.Permission;
import cn.anytec.security.core.enums.PermissionType;
import cn.anytec.security.model.TbPerson;
import cn.anytec.security.service.PersonService;
import cn.anytec.security.model.dto.PersonDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;

@Controller
@RequestMapping("/person")
public class PersonController {
    @Autowired
    private PersonService personService;

    private static final Logger logger = LoggerFactory.getLogger(PersonController.class);

    @OperLog(value = "添加底库人员", key="id,name")
    @RequestMapping("/add")
    @ResponseBody
    @Permission(value = "添加底库人员", method = PermissionType.IS_ADMIN)
    public ServerResponse add(@Valid PersonDTO personDTO, BindingResult bindingResult){
        if (bindingResult.hasErrors()) {
            logger.error("【添加底库人员】 添加底库人员， 原因：" + bindingResult.getFieldError().getDefaultMessage());
            return ServerResponse.createByErrorMessage(bindingResult.getFieldError().getDefaultMessage());
        }
        String idNumber = personDTO.getIdNumber();
        if(!personService.checkIdNumber(idNumber)){
            return ServerResponse.createByErrorMessage("标志编码: "+idNumber+" 已存在！");
        }
        return personService.add(personDTO);
    }

    @OperLog(value = "删除底库人员", key = "personSdkIds")
    @RequestMapping("/delete")
    @ResponseBody
    @Permission(value = "删除底库人员", method = PermissionType.IS_ADMIN)
    public ServerResponse delete(@RequestParam(value = "personSdkIds")String personSdkIds){
        return personService.delete(personSdkIds);
    }

    @RequestMapping("/list")
    @ResponseBody
//    @Permission(value = "查询人员底库", method = PermissionType.IS_ADMIN)
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

    @OperLog(value = "修改底库人员", key="id,name")
    @RequestMapping("/update")
    @ResponseBody
    @Permission(value = "修改底库人员", method = PermissionType.IS_ADMIN)
    public ServerResponse update(@Valid PersonDTO personDTO, BindingResult bindingResult){
        if (bindingResult.hasErrors()) {
            logger.error("【添加底库人员】 添加底库人员， 原因：" + bindingResult.getFieldError().getDefaultMessage());
            return ServerResponse.createByErrorMessage(bindingResult.getFieldError().getDefaultMessage());
        }
        return personService.update(personDTO);
    }
}
