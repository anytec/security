package cn.anytec.security.service;

import cn.anytec.security.model.vo.OperationLogVO;

import java.util.List;

/**
 * Created by imyzt on 2018/8/16 14:18
 * 日志系统
 */
public interface OperationLogService {

    /**
     * 搜索日志
     * @param firstTime 日志开始时间
     * @param lastTime 日志结束时间
     * @param logName 日志名称
     * @param logType 日志类型
     */
    List<OperationLogVO> list(String firstTime, String lastTime,
                              String logName, String logType);

}
