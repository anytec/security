package cn.anytec.security.core.enums;

/**
 * @Description: 阔展抓拍机的状态
 * @author: zhao
 * @date 2018/9/13 10:25
 */
public enum  CaptureCameraStatus {
    ACTIVE("active"),
    STANDBY("standby"),
    INVAILD("invaild");

    String msg;

    CaptureCameraStatus(String msg) {
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
