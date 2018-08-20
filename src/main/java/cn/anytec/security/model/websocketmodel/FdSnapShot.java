package cn.anytec.security.model.websocketmodel;

import java.util.List;

public class FdSnapShot {
    private String catchTime;
    private String snapshotUrl;
    private String cameraName;
    private String gender;
    private String faceSdkId;
    private List<String> emotions;

    public String getCatchTime() {
        return catchTime;
    }

    public void setCatchTime(String catchTime) {
        this.catchTime = catchTime;
    }

    public String getSnapshotUrl() {
        return snapshotUrl;
    }

    public void setSnapshotUrl(String snapshotUrl) {
        this.snapshotUrl = snapshotUrl;
    }

    public String getCameraName() {
        return cameraName;
    }

    public void setCameraName(String cameraName) {
        this.cameraName = cameraName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getFaceSdkId() {
        return faceSdkId;
    }

    public void setFaceSdkId(String faceSdkId) {
        this.faceSdkId = faceSdkId;
    }

    public List<String> getEmotions() {
        return emotions;
    }

    public void setEmotions(List<String> emotions) {
        this.emotions = emotions;
    }
}
