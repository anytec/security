package cn.anytec.security.model.dto;

import org.springframework.data.annotation.Id;

import java.util.Date;

/**
 * Created by imyzt on 2018-8-16 15:12
 * 日志信息VO对象
 */
public class OperationLogDTO {

    /**
     * 主键
     */
    @Id
    private Integer id;
    /**
     * 日志类型
     */
    private String logtype;
    /**
     * 日志名称
     */
    private String logname;
    /**
     * 操作用户名称
     */
    private String username;
    /**
     * 类名称
     */
    private String classname;
    /**
     * 方法名称
     */
    private String method;
    /**
     * 创建时间
     */
    private Date createtime;
    /**
     * 备注
     */
    private String message;

    public OperationLogDTO() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getLogtype() {
        return logtype;
    }

    public void setLogtype(String logtype) {
        this.logtype = logtype;
    }

    public String getLogname() {
        return logname;
    }

    public void setLogname(String logname) {
        this.logname = logname;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getClassname() {
        return classname;
    }

    public void setClassname(String classname) {
        this.classname = classname;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Date getCreatetime() {
        return createtime;
    }

    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "OperationLogDTO{" +
                "id=" + id +
                ", logtype='" + logtype + '\'' +
                ", logname='" + logname + '\'' +
                ", username='" + username + '\'' +
                ", classname='" + classname + '\'' +
                ", method='" + method + '\'' +
                ", createtime=" + createtime +
                ", message='" + message + '\'' +
                '}';
    }
}
