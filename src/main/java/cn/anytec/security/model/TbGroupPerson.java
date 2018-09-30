package cn.anytec.security.model;

public class TbGroupPerson {
    private Integer id;

    private String name;

    private String colorLabel;

    private String remarks;

    private Integer totalNumber;

    private String voiceLabel;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getColorLabel() {
        return colorLabel;
    }

    public void setColorLabel(String colorLabel) {
        this.colorLabel = colorLabel == null ? null : colorLabel.trim();
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks == null ? null : remarks.trim();
    }

    public Integer getTotalNumber() {
        return totalNumber;
    }

    public void setTotalNumber(Integer totalNumber) {
        this.totalNumber = totalNumber;
    }

    public String getVoiceLabel() {
        return voiceLabel;
    }

    public void setVoiceLabel(String voiceLabel) {
        this.voiceLabel = voiceLabel == null ? null : voiceLabel.trim();
    }
}