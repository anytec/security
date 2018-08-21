package cn.anytec.security.core.util;

import cn.anytec.security.core.enums.SecurityExceptionEnum;
import cn.anytec.security.core.exception.BussinessException;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * Created by imyzt on 2018/8/17 14:30
 * 此方法可以通过静态方法获取Spring Bean
 */
@Component
public class ApplicationContextHolder implements ApplicationContextAware{

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ApplicationContextHolder.applicationContext = applicationContext;
    }

    public static <T> T getBean(String beanName) {
        assertApplicationContext();
        return (T) applicationContext.getBean(beanName);
    }

    public static <T> T getBean(Class<T> clazz) {
        assertApplicationContext();
        return applicationContext.getBean(clazz);
    }

    /**
     * 健壮性判断
     */
    private static void assertApplicationContext() {
        if (null == ApplicationContextHolder.applicationContext) {
            throw new BussinessException(SecurityExceptionEnum.SERVER_ERROR.getCode(),
                    "未能成功获取Spring上下文");
        }
    }
}
