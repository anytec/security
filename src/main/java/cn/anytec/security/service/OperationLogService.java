package cn.anytec.security.service;

import cn.anytec.security.model.dto.OperationLogDTO;
import cn.anytec.security.model.dto.OperationRecordDTO;
import com.github.pagehelper.PageInfo;

/**
 * Created by imyzt on 2018/8/16 14:18
 * 日志系统
 */
public interface OperationLogService {

    /**
     * 搜索日志
     * @param firstTime 日志开始时间
     * @param lastTime 日志结束时间
     * @param logType 日志类型
     */
    PageInfo<OperationLogDTO> list(Integer pageNum, Integer pageSize,
                                   String firstTime, String lastTime,
                                   String logType);


    /**
     * 操作记录查询
     */
    PageInfo<OperationRecordDTO> operationRecordList(Integer pageNum, Integer pageSize,
                                                     String firstTime, String lastTime,
                                                     String operationType, String uname);
}
