package cn.anytec.security.core.aop;

import cn.anytec.security.core.annotion.OperLog;
import cn.anytec.security.core.exception.BussinessException;
import cn.anytec.security.core.log.LogManager;
import cn.anytec.security.core.log.LogObjectHolder;
import cn.anytec.security.core.log.factory.LogTaskFactory;
import cn.anytec.security.core.util.Contrast;
import cn.anytec.security.core.util.StrUtil;
import cn.anytec.security.model.TbUser;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.beans.IntrospectionException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.HashMap;

/**
 * Created by imyzt on 2018/8/15 15:46
 * 日志记录AOP
 */
@Aspect
@Component
public class OperLogAop {

    @Pointcut(value = "@annotation(cn.anytec.security.core.annotion.OperLog)")
    public void cut() {
    }

    @Around("cut()")
    public Object recordOperLog(ProceedingJoinPoint point) throws Throwable {

        // 先保证业务执行完成
        Object result = point.proceed();

        handler(point);

        return result;
    }

    /** 执行操作 */
    public void handler(ProceedingJoinPoint point) throws NoSuchMethodException, IllegalAccessException, IntrospectionException, InvocationTargetException {

        // 获取方法名称
        Signature signature = point.getSignature();
        MethodSignature methodSignature = null;
        if (!(signature instanceof MethodSignature)) {
            throw new BussinessException(500, "此注解只能用于方法");
        }
        methodSignature = (MethodSignature) signature;
        Object pointTarget = point.getTarget();
        Method currentMethod = pointTarget.getClass().getMethod(methodSignature.getName(), methodSignature.getParameterTypes());
        String methodName = currentMethod.getName();

        // 判断用户是否登录，未登录不记录日志
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        TbUser user = (TbUser) request.getSession().getAttribute("currentUser");
        if (null == user) {
            return;
        }

        // 获取方法所在的类、方法参数
        String className = point.getTarget().getClass().getName();
        //Object[] methodArgs = point.getArgs();

        // 获取操作的名称、唯一主键
        OperLog annotation = currentMethod.getAnnotation(OperLog.class);
        String operName = annotation.value();
        String operKey = annotation.key();

        // 业务日志不同于异常日志,业务日志message需要保存标识本条业务的唯一标识
        String msg = null;

        // 获取请求所有的值
        HashMap<String, String> reqParam = new HashMap<>();
        Enumeration<String> names = request.getParameterNames();
        while (names.hasMoreElements()) {
            String paramName = names.nextElement();
            String parameter = request.getParameter(paramName);
            reqParam.put(paramName, parameter);
        }

        // 如果涉及到修改，对比修改的变化   indexOf性能优于contains
       // boolean edit = (operName.indexOf("修改") != -1) || (operName.indexOf("编辑") != -1);
        boolean edit = (operName.indexOf("编辑") != -1);
        if (edit) {
            Object obj1 = LogObjectHolder.me().get();
            msg = Contrast.contrastObj(obj1, reqParam);
        }else {

            msg = Contrast.parseMutiKey(operKey, reqParam);
        }

        // 业务日志: 匹配操作对象
        String requestURI = request.getRequestURI();
        String operationObj = StrUtil.replaceOperationObj(requestURI);

        // 业务日志入库
        LogManager.me().execute(LogTaskFactory.bussinessLog(user.getId(), operName,
                className, methodName, msg, operationObj));

    }
}
