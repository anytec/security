package cn.anytec.security.core.log;

import java.util.TimerTask;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by imyzt on 2018/8/15 17:24
 * 日志管理器(用于异步入库,不对业务产生影响)
 */
public class LogManager {

    // 异步操作日志记录延时
    private final int OPERATION_DELAY_TIME = 10;

    // 异步操作记录日志的线程池
    private ScheduledThreadPoolExecutor pool = new ScheduledThreadPoolExecutor(10);

    private LogManager() {
    }

    private static class Inner {
        private final static LogManager instance = new LogManager();
    }

    /**
     * 获取日志管理器的单例实例
     */
    public static LogManager me() {
        return Inner.instance;
    }

    /** 加入线程池 */
    public void execute(TimerTask task) {
        pool.schedule(task, OPERATION_DELAY_TIME, TimeUnit.MILLISECONDS);
    }
}
