package cn.anytec.security.core.enums;

/**
 * @Description: TODO
 * @author: zhao
 * @date 2018/9/30 9:54
 */
public enum UserRole {
    USER(0,"用户"),
    ADMIN(1,"管理员");
    Integer role;
    String msg;

    UserRole(Integer role, String msg) {
        this.role = role;
        this.msg = msg;
    }

    public Integer getRole() {
        return role;
    }

    public void setRole(Integer role) {
        this.role = role;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
