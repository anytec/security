package cn.anytec.security.model;

public class TbCamera {
    private Integer id;

    private String sdkId;

    private String streamAddress;

    private String playAddress;

    private Integer cameraStatus;

    private String cameraType;

    private String serverLabel;

    private String name;

    private Integer groupId;

    private String location;

    private String groupName;

    private String remarks;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSdkId() {
        return sdkId;
    }

    public void setSdkId(String sdkId) {
        this.sdkId = sdkId == null ? null : sdkId.trim();
    }

    public String getStreamAddress() {
        return streamAddress;
    }

    public void setStreamAddress(String streamAddress) {
        this.streamAddress = streamAddress == null ? null : streamAddress.trim();
    }

    public String getPlayAddress() {
        return playAddress;
    }

    public void setPlayAddress(String playAddress) {
        this.playAddress = playAddress == null ? null : playAddress.trim();
    }

    public Integer getCameraStatus() {
        return cameraStatus;
    }

    public void setCameraStatus(Integer cameraStatus) {
        this.cameraStatus = cameraStatus;
    }

    public String getCameraType() {
        return cameraType;
    }

    public void setCameraType(String cameraType) {
        this.cameraType = cameraType == null ? null : cameraType.trim();
    }

    public String getServerLabel() {
        return serverLabel;
    }

    public void setServerLabel(String serverLabel) {
        this.serverLabel = serverLabel == null ? null : serverLabel.trim();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location == null ? null : location.trim();
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName == null ? null : groupName.trim();
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks == null ? null : remarks.trim();
    }
}