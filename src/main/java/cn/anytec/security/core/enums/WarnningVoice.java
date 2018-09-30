package cn.anytec.security.core.enums;

/**
 * @Description: TODO
 * @author: zhao
 * @date 2018/9/30 11:11
 */
public enum WarnningVoice {
    DANGER("danger","危险"),
    WARNNING("warnning","警告");

    String level;
    String msg;

    WarnningVoice(String level, String msg) {
        this.level = level;
        this.msg = msg;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
