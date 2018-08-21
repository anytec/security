package cn.anytec.security.core.enums;

/**
 * Created by imyzt on 2018/8/15 17:13
 * 操作是否成功
 */
public enum LogStatus {

    SUCCESS("成功"),
    FAIL("失败");

    String message;

    LogStatus(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
