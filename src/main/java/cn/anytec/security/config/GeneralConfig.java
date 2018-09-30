package cn.anytec.security.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class GeneralConfig {
    //动态库sdk
    @Value("${snapSdk.hostIp}")
    private String snapSdkIp;
    @Value("${snapSdk.port}")
    private String snapSdkPort;
    @Value("${snapSdk.url}")
    private String snapSdkUrl;
    @Value("${snapSdk.token}")
    private String snapSdkToken;
    @Value("${snapSdk.version}")
    private String snapSdkVersion;
    @Value("${snapSdk.identifyType}")
    private String snapIdentifyType;
    @Value("${snapSdk.snapGallery}")
    private String snapGallery;
    @Value("${snapSdk.snapIdentifyNumber}")
    private Integer snapIdentifyNumber;
    @Value("${snapSdk.emotionsOpen}")
    private boolean emotionsOpen;
    @Value("${snapSdk.genderOpen}")
    private boolean genderOpen;
    @Value("${snapSdk.ageOpen}")
    private boolean ageOpen;
    @Value("${snapSdk.identifySnapThreshold}")
    private String identifySnapThreshold;
    //静态库sdk
    @Value("${staticSdk.hostIp}")
    private String staticSdkIp;
    @Value("${staticSdk.port}")
    private String staticSdkPort;
    @Value("${staticSdk.url}")
    private String staticSdkUrl;
    @Value("${staticSdk.token}")
    private String staticSdkToken;
    @Value("${staticSdk.version}")
    private String staticSdkVersion;
    @Value("${staticSdk.warningThreshold}")
    private String warningThreshold;
    @Value("${staticSdk.staticGallery}")
    private String staticGallery;

    @Value("${constant.camera}")
    private String cameraUrl;
    @Value("${camera.rtmpPrefix}")
    private String rtmpPrefix;


    public String getSnapSdkIp() {
        return snapSdkIp;
    }

    public String getSnapSdkPort() {
        return snapSdkPort;
    }

    public String getSnapSdkUrl() {
        return snapSdkUrl;
    }

    public String getSnapSdkToken() {
        return snapSdkToken;
    }

    public String getSnapSdkVersion() {
        return snapSdkVersion;
    }

    public String getSnapIdentifyType() {
        return snapIdentifyType;
    }

    public String getStaticSdkIp() {
        return staticSdkIp;
    }

    public String getStaticSdkPort() {
        return staticSdkPort;
    }

    public String getStaticSdkUrl() {
        return staticSdkUrl;
    }

    public String getStaticSdkToken() {
        return staticSdkToken;
    }

    public String getStaticSdkVersion() {
        return staticSdkVersion;
    }

    public String getCameraUrl() {
        return cameraUrl;
    }

    public String getSnapGallery() {
        return snapGallery;
    }

    public String getStaticGallery() {
        return staticGallery;
    }

    public String getWarningThreshold() {
        return warningThreshold;
    }

    public String getIdentifySnapThreshold() {
        return identifySnapThreshold;
    }

    public void setIdentifySnapThreshold(String identifySnapThreshold) {
        this.identifySnapThreshold = identifySnapThreshold;
    }

    public Integer getSnapIdentifyNumber() {
        return snapIdentifyNumber;
    }

    public boolean isEmotionsOpen() {
        return emotionsOpen;
    }

    public boolean isGenderOpen() {
        return genderOpen;
    }

    public boolean isAgeOpen() {
        return ageOpen;
    }

    public String getRtmpPrefix() {
        return rtmpPrefix;
    }
}
