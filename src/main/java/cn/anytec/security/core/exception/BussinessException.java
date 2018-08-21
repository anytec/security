package cn.anytec.security.core.exception;

import cn.anytec.security.core.enums.SecurityExceptionEnum;

/**
 * Created by imyzt on 2018-8-15 10:18
 * 业务异常的封装
 */
public class BussinessException extends SecurityException {

    public BussinessException(SecurityExceptionEnum securityExceptionEnum) {
        super(securityExceptionEnum);
    }

    public BussinessException(int friendlyCode, String friendlyMsg) {
        super(friendlyCode, friendlyMsg);
    }

    public BussinessException(int friendlyCode, String friendlyMsg, String urlPath) {
        super(friendlyCode, friendlyMsg, urlPath);
    }
}
