package cn.anytec.security.core.log.factory;

import cn.anytec.security.core.enums.LogStatus;
import cn.anytec.security.core.enums.LogType;
import cn.anytec.security.dao.OperationLogMapper;
import cn.anytec.security.model.OperationLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.TimerTask;

/**
 * Created by imyzt on 2018/8/15 17:05
 * 日志任务工厂类 <br/>
 * @see LogType 与本类建立强对应关系. 本类的工厂针对日志类型唯一对应.
 */
@Component
public class LogTaskFactory {


    private static OperationLogMapper logMapper;

    @Autowired
    private OperationLogMapper logMapper2;
    private static Logger log = LoggerFactory.getLogger(LogTaskFactory.class);

    @PostConstruct
    public void beforeInit() {
        logMapper = logMapper2;
    }

    public LogTaskFactory(OperationLogMapper mapper) {
        LogTaskFactory.logMapper = mapper;
    }


    /**
     * 业务日志
     */
    public static TimerTask bussinessLog(Integer userId, String bussinessName,
                                            String className, String methodName,
                                            String msg) {
        return new TimerTask() {

            @Override
            public void run() {
                OperationLog operationLog = new OperationLog();
                operationLog.setLogtype(LogType.BUSSINESS.getMessage());
                operationLog.setLogname(bussinessName);
                operationLog.setUserid(userId);
                operationLog.setClassname(className);
                operationLog.setMethod(methodName);
                operationLog.setCreatetime(new Date());
                operationLog.setSucceed(LogStatus.SUCCESS.getMessage());
                operationLog.setMessage(msg);

                try {
                    logMapper.insertLog(operationLog);
                }catch (Exception e){
                    log.error("记录业务日志失败");
                }
            }
        };
    }

    /**
     * 异常日志
     */
    public static TimerTask exceptionLog(Integer userId, Exception e,
                                         String clazzName, String methodName) {
        return new TimerTask() {

            @Override
            public void run() {

                StringWriter sw = new StringWriter();
                try {
                    e.printStackTrace(new PrintWriter(sw));
                } finally {
                    try {
                        sw.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
                String errorMsg = sw.getBuffer().toString().replaceAll("\\$", "T");

                OperationLog operationLog = new OperationLog();
                operationLog.setLogtype(LogType.EXCEPTION.getMessage());
                operationLog.setUserid(userId);
                operationLog.setCreatetime(new Date());
                operationLog.setLogname(e.getMessage());
                operationLog.setSucceed(LogStatus.FAIL.getMessage());
                operationLog.setClassname(clazzName);
                operationLog.setMethod(methodName);
                operationLog.setMessage(errorMsg);

                try {
                    logMapper.insertLog(operationLog);
                }catch (Exception e){
                    log.error("记录异常日志失败");
                }
            }
        };
    }

}
