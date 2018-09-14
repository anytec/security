package cn.anytec.security.model.parammodel;

import org.springframework.web.multipart.MultipartFile;

public class IdenfitySnapParam {
    private String cameraGroupIds;
    private String cameraIds;
    private String startTime;
    private String endTime;
    private String confidence;
    private String photoUrl;
    private MultipartFile photo;
    private Integer identifyNumber;

    public IdenfitySnapParam(String cameraGroupIds, String cameraIds, String startTime, String endTime,
                             String confidence, String photoUrl, MultipartFile photo, Integer identifyNumber) {
        this.cameraGroupIds = cameraGroupIds;
        this.cameraIds = cameraIds;
        this.startTime = startTime;
        this.endTime = endTime;
        this.confidence = confidence;
        this.photoUrl = photoUrl;
        this.photo = photo;
        this.identifyNumber = identifyNumber;
    }

    public String getCameraGroupIds() {
        return cameraGroupIds;
    }

    public void setCameraGroupIds(String cameraGroupIds) {
        this.cameraGroupIds = cameraGroupIds;
    }

    public String getCameraIds() {
        return cameraIds;
    }

    public void setCameraIds(String cameraIds) {
        this.cameraIds = cameraIds;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getConfidence() {
        return confidence;
    }

    public void setConfidence(String confidence) {
        this.confidence = confidence;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public MultipartFile getPhoto() {
        return photo;
    }

    public void setPhoto(MultipartFile photo) {
        this.photo = photo;
    }

    public Integer getIdentifyNumber() {
        return identifyNumber;
    }

    public void setIdentifyNumber(Integer identifyNumber) {
        this.identifyNumber = identifyNumber;
    }
}
