package cn.anytec.security.model.websocketmodel;

import java.util.List;

public class FrWarning {

    private String snapshotUrl;
    private String faceUrl;
    private Double confidence;
    private String personName;
    private String gender;
    private Integer age;
    private List<String> emotions;
    private Integer personGroupId;
    private String personGroupName;
    private Integer cameraId;
    private String catchTime;
    private String cameraName;
    private String cameraGroupName;
    private Integer warningOfWeek;

    public FrWarning(String snapshotUrl, String faceUrl){
        this.faceUrl = faceUrl;
        this.snapshotUrl = snapshotUrl;
    }

    public Double getConfidence() {
        return confidence;
    }

    public void setConfidence(Double confidence) {
        this.confidence = confidence;
    }

    public String getSnapshotUrl() {
        return snapshotUrl;
    }

    public String getFaceUrl() {
        return faceUrl;
    }

    public String getPersonName() {
        return personName;
    }

    public void setPersonName(String personName) {
        this.personName = personName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPersonGroupName() {
        return personGroupName;
    }

    public void setPersonGroupName(String personGroupName) {
        this.personGroupName = personGroupName;
    }

    public String getCatchTime() {
        return catchTime;
    }

    public void setCatchTime(String catchTime) {
        this.catchTime = catchTime;
    }

    public String getCameraName() {
        return cameraName;
    }

    public void setCameraName(String cameraName) {
        this.cameraName = cameraName;
    }

    public String getCameraGroupName() {
        return cameraGroupName;
    }

    public void setCameraGroupName(String cameraGroupName) {
        this.cameraGroupName = cameraGroupName;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Integer getPersonGroupId() {
        return personGroupId;
    }

    public void setPersonGroupId(Integer personGroupId) {
        this.personGroupId = personGroupId;
    }

    public Integer getCameraId() {
        return cameraId;
    }

    public void setCameraId(Integer cameraId) {
        this.cameraId = cameraId;
    }

    public Integer getWarningOfWeek() {
        return warningOfWeek;
    }

    public void setWarningOfWeek(Integer warningOfWeek) {
        this.warningOfWeek = warningOfWeek;
    }

    public List<String> getEmotions() {
        return emotions;
    }

    public void setEmotions(List<String> emotions) {
        this.emotions = emotions;
    }
}
