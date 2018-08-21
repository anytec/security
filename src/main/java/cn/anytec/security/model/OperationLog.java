package cn.anytec.security.model;

import org.springframework.data.annotation.Id;

import java.util.Date;

/**
 * Created by imyzt on 2018/8/15 16:18
 * 操作日志
 */
public class OperationLog {

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
     * 用户id
     */
	private Integer userid;
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
     * 是否成功
     */
	private String succeed;
    /**
     * 备注
     */
	private String message;


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

	public Integer getUserid() {
		return userid;
	}

	public void setUserid(Integer userid) {
		this.userid = userid;
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

	public String getSucceed() {
		return succeed;
	}

	public void setSucceed(String succeed) {
		this.succeed = succeed;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public String toString() {
		return "OperationLog{" +
			"id=" + id +
			", logtype=" + logtype +
			", logname=" + logname +
			", userid=" + userid +
			", classname=" + classname +
			", method=" + method +
			", createtime=" + createtime +
			", succeed=" + succeed +
			", message=" + message +
			"}";
	}
}
