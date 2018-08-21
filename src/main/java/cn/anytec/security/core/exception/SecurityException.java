package cn.anytec.security.core.exception;

import cn.anytec.security.core.enums.SecurityExceptionEnum;

/**
 * Created by imyzt on 2018/8/15 10:23
 * 全局异常的封装
 */
public class SecurityException extends RuntimeException {

    /** 响应码 */
    protected int friendlyCode;

    /** 友好提示信息 */
    protected String friendlyMsg;

    /** 业务异常需要跳转的页面 */
    protected String urlPath;

    protected SecurityException(int friendlyCode, String friendlyMsg, String urlPath) {
        super(friendlyMsg);
        this.setValues(
                friendlyCode,
                friendlyMsg).urlPath = urlPath;
    }

    protected SecurityException(int friendlyCode, String friendlyMsg) {
        super(friendlyMsg);
        this.setValues(
                friendlyCode,
                friendlyMsg);
    }

    protected SecurityException(SecurityExceptionEnum securityExceptionEnum) {
        super(securityExceptionEnum.getMsg());
        this.setValues(
                securityExceptionEnum.getCode(),
                securityExceptionEnum.getMsg()).urlPath = securityExceptionEnum.getUrlPath();
    }

    private SecurityException setValues(int friendlyCode, String friendlyMsg) {
        this.friendlyCode = friendlyCode;
        this.friendlyMsg = friendlyMsg;
//        this.urlPath = urlPath;
        return this;
    }

    public int getCode() {
        return friendlyCode;
    }

    public void setCode(int friendlyCode) {
        this.friendlyCode = friendlyCode;
    }

    public String getMsg() {
        return friendlyMsg;
    }

    public void setMsg(String friendlyMsg) {
        this.friendlyMsg = friendlyMsg;
    }

    public String getUrlPath() {
        return urlPath;
    }

    public void setUrlPath(String urlPath) {
        this.urlPath = urlPath;
    }
}
