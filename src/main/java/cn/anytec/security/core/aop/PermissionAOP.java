package cn.anytec.security.core.aop;

import cn.anytec.security.core.annotion.Permission;
import cn.anytec.security.core.enums.PermissionType;
import cn.anytec.security.core.enums.SecurityExceptionEnum;
import cn.anytec.security.core.exception.BussinessException;
import cn.anytec.security.core.exception.PermissionException;
import cn.anytec.security.core.log.LogManager;
import cn.anytec.security.core.log.factory.LogTaskFactory;
import cn.anytec.security.model.TbUser;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

/**
 * Created by imyzt on 2018/8/21 16:37
 */
@Aspect
@Component
public class PermissionAOP {

    private Logger log = LoggerFactory.getLogger(getClass());

    @Pointcut(value = "@annotation(cn.anytec.security.core.annotion.Permission)")
    public void cut() {
    }

    @Around("cut()")
    public Object recordPermission(ProceedingJoinPoint point) throws Throwable {

        return handler(point);
    }

    private Object handler(ProceedingJoinPoint point) throws Throwable {

        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        TbUser user = (TbUser) request.getSession().getAttribute("currentUser");
        if (null == user) {
            throw new BussinessException(SecurityExceptionEnum.UNAUTHORIZED);
        }

        Signature signature = point.getSignature();
        MethodSignature methodSignature = null;
        if (!(signature instanceof MethodSignature)) {
            throw new BussinessException(500, "此注解只能用于方法");
        }
        methodSignature = (MethodSignature) signature;
        Object pointTarget = point.getTarget();
        Method currentMethod = pointTarget.getClass().getMethod(methodSignature.getName(), methodSignature.getParameterTypes());

        Permission annotation = currentMethod.getAnnotation(Permission.class);
        String method = annotation.method();
        String value = annotation.value();

        Class<?> clazz = Class.forName(PermissionType.PERMISSION_FACTORY);
        Object PermissionFactory = clazz.newInstance();
        Method checkMethod = clazz.getMethod(method);

        Object result = checkMethod.invoke(PermissionFactory);

        if ((Boolean) result) {
            result = point.proceed();
        }else {
            LogManager.me().execute(LogTaskFactory.permissionLog(user.getId(), value,
                    pointTarget.getClass().getTypeName(),
                    currentMethod.getName()));

            throw new PermissionException(SecurityExceptionEnum.INSUFFICIENT_PERMISSIONS);

        }

        return result;
    }
}
