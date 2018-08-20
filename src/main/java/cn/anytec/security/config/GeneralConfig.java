package cn.anytec.security.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class GeneralConfig {
    @Value("${sdk.hostIp}")
    private String sdkIp;
    @Value("${sdk.port}")
    private String sdkPort;
    @Value("${sdk.url}")
    private String sdkUrl;
    @Value("${sdk.token}")
    private String sdkToken;
    @Value("${sdk.version}")
    private String sdkVersion;
    @Value("${sdk.identifyType}")
    private String identifyType;
    @Value("${sdk.snapGallery}")
    private String snapGallery;
    @Value("${sdk.staticGallery}")
    private String staticGallery;
    @Value("${sdk.warningThreshold}")
    private String warningThreshold;
    @Value("${sdk.snapIdentifyNumber}")
    private Integer snapIdentifyNumber;
    @Value("${sdk.emotionsOpen}")
    private boolean emotionsOpen;
    @Value("${sdk.genderOpen}")
    private boolean genderOpen;
    @Value("${sdk.ageOpen}")
    private boolean ageOpen;
    @Value("${password.salt}")
    private String passwordSalt;
    @Value("${role.user}")
    private int user;
    @Value("${role.admin}")
    private int admin;
    @Value("${constant.camera}")
    private String cameraUrl;
    @Value("${redisKeys.cameraBySdkId}")
    private String cameraBySdkId;
    @Value("${redisKeys.cameraGroupById}")
    private String cameraGroupById;
    @Value("${redisKeys.peronBySdkId}")
    private String peronBySdkId;
    @Value("${redisKeys.personGroupById}")
    private String personGroupById;
    @Value("${camera.rtmpPrefix}")
    private String rtmpPrefix;


    public String getCameraUrl() {
        return cameraUrl;
    }

    public String getSdkIp() {
        return sdkIp;
    }

    public String getSdkPort() {
        return sdkPort;
    }

    public String getSdkUrl() {
        return sdkUrl;
    }

    public String getSdkToken() {
        return sdkToken;
    }

    public String getSdkVersion() {
        return sdkVersion;
    }

    public String getIdentifyType() {
        return identifyType;
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

    public String getPasswordSalt() {
        return passwordSalt;
    }

    public int getUserRole() {
        return user;
    }

    public int getAdminRole() {
        return admin;
    }

    public String getCameraBySdkId() {
        return cameraBySdkId;
    }

    public String getCameraGroupById() {
        return cameraGroupById;
    }

    public String getPeronBySdkId() {
        return peronBySdkId;
    }

    public String getPersonGroupById() {
        return personGroupById;
    }
    public String getRtmpPrefix() {
        return rtmpPrefix;
    }
}
