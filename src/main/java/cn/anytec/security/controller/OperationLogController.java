package cn.anytec.security.controller;

import cn.anytec.security.common.ServerResponse;
import cn.anytec.security.core.annotion.OperLog;
import cn.anytec.security.model.vo.OperationLogVO;
import cn.anytec.security.service.OperationLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by imyzt on 2018/8/16 14:54
 */
@RestController
@RequestMapping("/log/")
public class OperationLogController {

    @Autowired
    private OperationLogService logService;

    @OperLog(value = "查询所有日志")
    @GetMapping("list")
    public ServerResponse list(@RequestParam(required = false) String firstTime, @RequestParam(required = false) String lastTime,
                               @RequestParam(required = false) String logName, @RequestParam(required = false) String logType) {

        List<OperationLogVO> logs = logService.list(firstTime, lastTime, logName, logType);

        return ServerResponse.createBySuccess(logs);
    }

}
