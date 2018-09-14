package cn.anytec.security.core.handler;

import cn.anytec.security.common.ServerResponse;
import cn.anytec.security.core.enums.SecurityExceptionEnum;
import cn.anytec.security.core.exception.BussinessException;
import cn.anytec.security.core.exception.PermissionException;
import cn.anytec.security.core.log.LogManager;
import cn.anytec.security.core.log.factory.LogTaskFactory;
import cn.anytec.security.model.TbUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by imyzt on 2018/8/15 10:34
 * 全局异常拦截器
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private Logger log = LoggerFactory.getLogger(getClass());

    @ExceptionHandler(value = Throwable.class)
    public @ResponseBody
    ServerResponse handler(HttpServletRequest req, Object handler, Exception e) {

        String msg;
        int code = HttpStatus.INTERNAL_SERVER_ERROR.value();
        /**
         * 此变量用于标记是否需要统一存储
         * eg: PermissionException 不需要统一存储,执行到此处时可以先行返回
         */
        boolean needStorage = true;

        StackTraceElement traceElement= e.getStackTrace()[0];
        String typeName = traceElement.getClassName();
        String method = traceElement.getMethodName();
        Integer lineNumber = traceElement.getLineNumber();

        // 更直观的显示错误信息
        log.error(typeName + "." + method + "第" + lineNumber + "行出错.错误原因：" + e.toString());

        // 打印出错误信息
        e.printStackTrace();

        TbUser currentUser = (TbUser) req.getSession().getAttribute("currentUser");
        needStorage = null != currentUser;

        // 业务异常
        if (e instanceof BussinessException) {
            BussinessException bussinessException = (BussinessException) e;
            msg = bussinessException.getMsg();
            code = bussinessException.getCode();
        }

        // 权限不足 403
        else if (e instanceof PermissionException) {
            PermissionException permissionException = (PermissionException) e;
            msg = permissionException.getMsg();
            code = permissionException.getCode();

            needStorage = false;
        }

        // 400
        else if (e instanceof MissingServletRequestParameterException) {
            msg = SecurityExceptionEnum.REQUEST_NULL.getMsg();
            code = SecurityExceptionEnum.REQUEST_NULL.getCode();
        }

        // 404
        else if (e instanceof NoHandlerFoundException) {
            msg = SecurityExceptionEnum.NOT_FOUND.getMsg();
            code = SecurityExceptionEnum.NOT_FOUND.getCode();
        }

        // 405
        else if (e instanceof HttpRequestMethodNotSupportedException) {
            code = SecurityExceptionEnum.METHOD_NOT_ALLOWED.getCode();
            msg = SecurityExceptionEnum.METHOD_NOT_ALLOWED.getMsg();
        }

        // 运行时异常
        else if (e instanceof RuntimeException) {
            RuntimeException runtimeException = (RuntimeException) e;
            msg = runtimeException.getMessage();
        }

        // 常用的如登录失败,权限不足...都可以在此处拦截.

        // 其它所有异常
        else {
            msg = e.getMessage();
        }

        if (needStorage) {
            LogManager.me().execute(LogTaskFactory.exceptionLog(currentUser.getId(), e, typeName, method));
        }

        return ServerResponse.createByErrorCodeMessage(code, msg);
    }

}
