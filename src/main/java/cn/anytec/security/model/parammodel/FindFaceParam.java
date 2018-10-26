package cn.anytec.security.model.parammodel;

import cn.anytec.security.findface.model.FaceInfo;
import org.springframework.web.multipart.MultipartFile;

public class FindFaceParam {
    private String meta;
    private boolean friend;
    private String mf_selector;
    private FaceInfo faceInfo;
    private String[] galleries;
    private String threshold;
    private String camid;
    private Integer n;
    private String photoUrl;
    private String bbox;
    private String sdkIp;
    private String sdkPort;
    private String sdkVersion;
    private String sdkToken;
    private MultipartFile photo;


    public Integer getN() {
        return n;
    }

    public void setN(Integer n) {
        this.n = n;
    }

    public String getMeta() {
        return meta;
    }

    public void setMeta(String meta) {
        this.meta = meta;
    }

    public boolean isFriend() {
        return friend;
    }

    public void setFriend(boolean friend) {
        this.friend = friend;
    }

    public String getMf_selector() {
        return mf_selector;
    }

    public void setMf_selector(String mf_selector) {
        this.mf_selector = mf_selector;
    }

    public FaceInfo getFaceInfo() {
        return faceInfo;
    }

    public void setFaceInfo(FaceInfo faceInfo) {
        this.faceInfo = faceInfo;
    }

    public String[] getGalleries() {
        return galleries;
    }

    public void setGalleries(String[] galleries) {
        this.galleries = galleries;
    }

    public String getThreshold() {
        return threshold;
    }

    public void setThreshold(String threshold) {
        this.threshold = threshold;
    }

    public String getCamid() {
        return camid;
    }

    public void setCamid(String camid) {
        this.camid = camid;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getBbox() {
        return bbox;
    }

    public void setBbox(String bbox) {
        this.bbox = bbox;
    }

    public String getSdkIp() {
        return sdkIp;
    }

    public void setSdkIp(String sdkIp) {
        this.sdkIp = sdkIp;
    }

    public String getSdkPort() {
        return sdkPort;
    }

    public void setSdkPort(String sdkPort) {
        this.sdkPort = sdkPort;
    }

    public String getSdkVersion() {
        return sdkVersion;
    }

    public void setSdkVersion(String sdkVersion) {
        this.sdkVersion = sdkVersion;
    }

    public String getSdkToken() {
        return sdkToken;
    }

    public void setSdkToken(String sdkToken) {
        this.sdkToken = sdkToken;
    }

    public MultipartFile getPhoto() {
        return photo;
    }

    public void setPhoto(MultipartFile photo) {
        this.photo = photo;
    }
}
