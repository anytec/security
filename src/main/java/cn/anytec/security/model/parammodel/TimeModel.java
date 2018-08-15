package cn.anytec.security.model.parammodel;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class TimeModel {
    private String sdkTimeStr;
    private LocalDateTime localDateTime;
    private long timestamp;
    private String catchTime;
    private int dayOfWeek;
    private int hour;
    private int minute;
    private SimpleDateFormat format;

    public TimeModel(String sdkTimeStr, SimpleDateFormat format){
        this.sdkTimeStr = sdkTimeStr;
        this.format = format;
        LocalDateTime localDateTime = LocalDateTime.parse(sdkTimeStr);
        this.localDateTime = localDateTime;
        this.dayOfWeek = localDateTime.getDayOfWeek().getValue();
        this.hour = localDateTime.getHour();
        this.minute = localDateTime.getMinute();
        long timestamp = localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        this.timestamp = timestamp;
        this.catchTime = format.format(timestamp);
    }

    public LocalDateTime getLocalDateTime() {
        return localDateTime;
    }

    public void setLocalDateTime(LocalDateTime localDateTime) {
        this.localDateTime = localDateTime;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getCatchTime() {
        return catchTime;
    }

    public void setCatchTime(String catchTime) {
        this.catchTime = catchTime;
    }

    public String getSdkTimeStr() {
        return sdkTimeStr;
    }

    public void setSdkTimeStr(String sdkTimeStr) {
        this.sdkTimeStr = sdkTimeStr;
    }

    public int getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(int dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public SimpleDateFormat getFormat() {
        return format;
    }

    public void setFormat(SimpleDateFormat format) {
        this.format = format;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }
}
