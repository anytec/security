package cn.anytec.security.findface.model;

import java.util.List;

public class IdentifyFace{
    private String id;
    private String person_id;
    private double age;
    private String gender;
    private List<String> emotions;
    private boolean friend;
    private List<String> galleries;
    private String meta;
    private String normalized;
    private String photo;
    private String thumbnail;
    private String photo_hash;
    private String timestamp;
    private int x1;
    private int x2;
    private int y1;
    private int y2;

    public void setFriend(boolean friend) {
        this.friend = friend;
    }

    public void setGalleries(List<String> galleries) {
        this.galleries = galleries;
    }

    public void setMeta(String meta) {
        this.meta = meta;
    }

    public String getId() {
        return id;
    }

    public String getPerson_id() {
        return person_id;
    }

    public Double getAge() {
        return age;
    }

    public String getGender() {
        return gender;
    }

    public List<String> getEmotions() {
        return emotions;
    }

    public boolean isFriend() {
        return friend;
    }

    public List<String> getGalleries() {
        return galleries;
    }

    public String getMeta() {
        return meta;
    }

    public String getNormalized() {
        return normalized;
    }

    public String getPhoto() {
        return photo;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public String getPhoto_hash() {
        return photo_hash;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public int getX1() {
        return x1;
    }

    public int getX2() {
        return x2;
    }

    public int getY1() {
        return y1;
    }

    public int getY2() {
        return y2;
    }

    public String getBbox(){
        return "[["+x1+","+y1+","+x2+","+y2+"]]";
    }

    public int getHeight(){
        return y2-y1;
    }
    public int getWidth(){
        return x2-x1;
    }
}