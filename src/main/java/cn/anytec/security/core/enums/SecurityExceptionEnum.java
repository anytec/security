package cn.anytec.security.core.enums;

/**
 * Created by imyzt on 2018/8/15 10:20
 * 所有异常的枚举类
 */
public enum SecurityExceptionEnum {

    /**
     * 文件上传
     */
    FILE_READING_ERROR(400,"FILE_READING_ERROR!"),
    FILE_NOT_FOUND(400,"FILE_NOT_FOUND!"),

    /**
     * 错误的请求
     */
    REQUEST_NULL(400, "缺少请求参数"),
    METHOD_NOT_ALLOWED(405, "不支持当前请求方法"),
    SERVER_ERROR(500, "服务器异常"),

    /**
     * 资源未找到
     */
    NOT_FOUND(404, "资源未找到"),

    /**
     * 其他
     */
    WRITE_ERROR(500,"渲染界面错误");


    /** 响应码 */
    private int friendlyCode;

    /** 友好提示信息 */
    private String friendlyMsg;

    /** 业务异常需要跳转的页面 */
    private String urlPath;


    SecurityExceptionEnum(int friendlyCode, String friendlyMsg, String urlPath) {
        this.friendlyCode = friendlyCode;
        this.friendlyMsg = friendlyMsg;
        this.urlPath = urlPath;
    }

    SecurityExceptionEnum(int friendlyCode, String friendlyMsg) {
        this.friendlyCode = friendlyCode;
        this.friendlyMsg = friendlyMsg;
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
