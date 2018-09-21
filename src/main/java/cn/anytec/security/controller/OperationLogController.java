package cn.anytec.security.controller;

import cn.anytec.security.common.ServerResponse;
import cn.anytec.security.core.annotion.Permission;
import cn.anytec.security.core.enums.PermissionType;
import cn.anytec.security.model.dto.OperationLogDTO;
import cn.anytec.security.model.dto.OperationRecordDTO;
import cn.anytec.security.service.OperationLogService;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Created by imyzt on 2018/8/16 14:54
 */
@RestController
@RequestMapping("/log/")
public class OperationLogController {

    @Autowired
    private OperationLogService logService;

    @Permission(value = "查询所有日志", method = PermissionType.IS_ADMIN)
    //@OperLog(value = "查询所有日志")
    @GetMapping("list")
    public ServerResponse list(@RequestParam(value = "pageNum",defaultValue = "1") Integer pageNum,
                               @RequestParam(value = "pageSize",defaultValue = "10") Integer pageSize,
                               @RequestParam(required = false) String firstTime, @RequestParam(required = false) String lastTime,
                               @RequestParam(required = false) String logType) {

        PageInfo<OperationLogDTO> logs = logService.list(pageNum, pageSize, firstTime, lastTime, logType);

        return ServerResponse.createBySuccess(logs);
    }

    @Permission(value = "查询所有业务日志", method = PermissionType.IS_ADMIN)
    //@OperLog(value = "查询所有业务日志", key = "pageNum,pageSize,firstTime,lastTime,operationType,uname")
    @GetMapping("operationRecordList")
    public ServerResponse operationRecordList(@RequestParam(value = "pageNum",defaultValue = "1") Integer pageNum,
                               @RequestParam(value = "pageSize",defaultValue = "10") Integer pageSize,
                               @RequestParam(required = false) String firstTime, @RequestParam(required = false) String lastTime,
                               @RequestParam(required = false) String operationType, @RequestParam(required = false) String uname) {

        PageInfo<OperationRecordDTO> operationRecordList = logService.operationRecordList(pageNum, pageSize, firstTime, lastTime, operationType, uname);

        return ServerResponse.createBySuccess(operationRecordList);
    }

}
