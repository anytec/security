package cn.anytec.security.model.parammodel;

import cn.anytec.security.findface.model.FaceInfo;

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
}
