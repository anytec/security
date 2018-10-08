package cn.anytec.security.constant;

/**
 * @Description: redis键的键名
 * @author: zhao
 * @date 2018/9/25 10:53
 */
public interface RedisConst {
    String CAMERA_BY_SDKID = "getCameraBySdkId";
    String CAMERAGROUP_BY_ID = "getCameraGroupById";
    String PERSON_BY_SDKID = "getPersonBySdkId";
    String PERSONGROUP_BY_ID = "getPersonGroupById";
    String PERSON_COUNTING = "personCounting";
    String WARNING_THRESHOLD = "warningThrehsold";
    String IDENTIFY_SNAP_THRESOLD = "identifySnapThresold";
    String CAPTURECAMERAS = "captureCameras";
    String CAPTURECAMERAS_INUSE = "captureCamerasInUse";
    String CAPTURECAMERAS_OFFLINE = "captureCamerasOffline";
}
