package cn.anytec.security.model.vo;

/**
 * Created by imyzt on 2018/8/22 17:20
 * 用户登录成功后返回对象
 */
public class UserVO {

    private Integer id;

    private String uname;

    private String role;

    private String notes;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUname() {
        return uname;
    }

    public void setUname(String uname) {
        this.uname = uname;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
