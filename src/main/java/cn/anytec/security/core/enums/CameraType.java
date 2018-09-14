package cn.anytec.security.core.enums;

/**
 * @Description: 设备类型
 * @author: zhao
 * @date 2018/9/13 11:35
 */
public enum CameraType {
    CaptureCamera("抓拍机"),
    VideoStreamCamera("视频流");

    String msg;

    CameraType(String msg){
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
