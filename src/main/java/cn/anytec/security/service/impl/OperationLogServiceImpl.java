package cn.anytec.security.service.impl;

import cn.anytec.security.dao.OperationLogMapper;
import cn.anytec.security.model.vo.OperationLogVO;
import cn.anytec.security.service.OperationLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by imyzt on 2018/8/16 14:21
 */
@Service
public class OperationLogServiceImpl implements OperationLogService {

    @Autowired
    private OperationLogMapper mapper;

    @Override
    public List<OperationLogVO> list(String firstTime, String lastTime, String logName, String logType) {

        List<OperationLogVO> logs = mapper.list(firstTime, lastTime, logName, logType);

        return logs;
    }
}
