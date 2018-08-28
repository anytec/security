package cn.anytec.security.model.vo;

import java.util.Date;

/**
 * Created by imyzt on 2018/8/23 19:04
 * ????-?????VO??
 */
public class OperationRecordVO {

    private String uname;

    private String operationType;

    private String operationObj;

    private Integer ObjId;

    private Date operationTime;

    public Date getOperationTime() {
        return operationTime;
    }

    public void setOperationTime(Date operationTime) {
        this.operationTime = operationTime;
    }

    private String operationResult;

    public String getUname() {
        return uname;
    }

    public void setUname(String uname) {
        this.uname = uname;
    }

    public String getOperationType() {
        return operationType.substring(0, 2);
    }

    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }

    public String getOperationObj() {
        return operationObj;
    }

    public void setOperationObj(String operationObj) {
        this.operationObj = operationObj;
    }

    public Integer getObjId() {
        return ObjId;
    }

    public void setObjId(Integer objId) {
        ObjId = objId;
    }

    public String getOperationResult() {
        return operationResult;
    }

    public void setOperationResult(String operationResult) {
        this.operationResult = operationResult;
    }

    @Override
    public String toString() {
        return "OperationRecordVO{" +
                "uname='" + uname + '\'' +
                ", operationType='" + operationType.substring(0, 2) + '\'' +
                ", operationObj='" + operationObj + '\'' +
                ", ObjId=" + ObjId +
                ", operationTime=" + operationTime +
                ", operationResult='" + operationResult + '\'' +
                '}';
    }
}
