package cn.anytec.security.dao;

import cn.anytec.security.model.OperationLog;
import cn.anytec.security.model.vo.OperationLogVO;
import cn.anytec.security.model.vo.OperationRecordVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 日志
 */
@Repository
public interface OperationLogMapper {

    int insertLog(@Param("log") OperationLog log);

    List<OperationLogVO> list(@Param("firstTime") String firstTime, @Param("lastTime") String lastTime,
                              @Param("logType") String logType);

    List<OperationRecordVO> operationRecordList(@Param("firstTime") String firstTime, @Param("lastTime") String lastTime,
                                                @Param("operationType") String operationType, @Param("uname") String uname);
}