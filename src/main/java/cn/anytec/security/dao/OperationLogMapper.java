package cn.anytec.security.dao;

import cn.anytec.security.model.OperationLog;
import cn.anytec.security.model.vo.OperationLogVO;
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
                              @Param("logName") String logName, @Param("logType") String logType);
}