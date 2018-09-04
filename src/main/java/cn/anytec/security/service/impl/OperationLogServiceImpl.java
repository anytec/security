package cn.anytec.security.service.impl;

import cn.anytec.security.dao.OperationLogMapper;
import cn.anytec.security.model.vo.OperationLogVO;
import cn.anytec.security.model.vo.OperationRecordVO;
import cn.anytec.security.service.OperationLogService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
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
    public PageInfo<OperationLogVO> list(Integer pageNum, Integer pageSize, String firstTime, String lastTime, String logType) {

        PageHelper.startPage(pageNum, pageSize);

        List<OperationLogVO> logs = mapper.list(firstTime, lastTime, logType);

        return PageInfo.of(logs);
    }

    @Override
    public PageInfo<OperationRecordVO> operationRecordList(Integer pageNum, Integer pageSize, String firstTime, String lastTime, String operationType, String uname) {

        PageHelper.startPage(pageNum, pageSize);

        List<OperationRecordVO> logs = mapper.operationRecordList(firstTime, lastTime, operationType, uname);

        return PageInfo.of(logs);
    }
}
