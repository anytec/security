package cn.anytec.security.core.log;

import cn.anytec.security.core.util.ApplicationContextHolder;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

/**
 * Created by imyzt on 2018/8/15 16:18
 * 被修改的bean临时存放的地方
 */
@Component
@Scope(scopeName = WebApplicationContext.SCOPE_SESSION)
public class LogObjectHolder {

    private Object object = null;

    public Object get() {
        return object;
    }

    public void set(Object object) {
        this.object = object;
    }

    public static LogObjectHolder me() {
        return ApplicationContextHolder.getBean(LogObjectHolder.class);
    }
}
