package cn.anytec.security.core.exception;

import cn.anytec.security.core.enums.SecurityExceptionEnum;

/**
 * Created by imyzt on 2018/8/21 18:17
 * 权限相关的异常
 */
public class PermissionException extends SecurityException{

    public PermissionException(int friendlyCode, String friendlyMsg, String urlPath) {
        super(friendlyCode, friendlyMsg, urlPath);
    }

    public PermissionException(int friendlyCode, String friendlyMsg) {
        super(friendlyCode, friendlyMsg);
    }

    public PermissionException(SecurityExceptionEnum securityExceptionEnum) {
        super(securityExceptionEnum);
    }
}
