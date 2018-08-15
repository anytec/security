package cn.anytec.security.findface.model;

public class FaceDefine {
    public int confidence = 0;
    public int ID = 0;
    public double left = 0;
    public double right = 0;
    public double top = 0;
    public double bottom = 0;

    public FaceDefine() {
    }

    @Override
    public String toString() {
        return "FaceDefine [confidence=" + confidence + ", ID=" + ID + ", left=" + left + ", right=" + right + ", top="
                + top + ", bottom=" + bottom + "]";
    }


}
