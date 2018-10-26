package cn.anytec.security.findface.model;

import java.util.List;

public class FaceInfo{
    private double age;
    private List<String> emotions;
    private String gender;
    private int x1;
    private int x2;
    private int y1;
    private int y2;

    public double getAge() {
        return age;
    }

    public List<String> getEmotions() {
        return emotions;
    }

    public String getGender() {
        return gender;
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
        return x2-x1;
    }
    public int getWidth(){
        return y2-y1;
    }
}