package cn.anytec.security.component.ipcamera.ipcService;

import cn.anytec.security.common.ServerResponse;
import cn.anytec.security.model.TbCamera;

import java.util.Map;

/**用于抓拍机的处理*/
public interface IPCOperations {

    /**添加抓拍机的处理*/
    void handleAddCaptureCamera(TbCamera camera);

    /**删除抓拍机的处理*/
    //void handleDeleteCaptureCamera();

    /**将抓拍机加入到redis*/
    boolean addToCache(String macAddress, String ipAddress);

    /**将抓拍机从redis中移除*/
    void deleteFromCache(String macAddress);

    /**将加入到系统中的抓拍机加入到Redis*/
    void addToInUseCache(String macAddress);

    /**将加入到系统中的抓拍机从Redis中移除*/
    void deleteFromInUseCache(String macAddress);

    /**抓拍机转变为激活状态*/
    void activeCaptureCamera(String macAddress);

    /**抓拍机转变为等待状态*/
    void standbyCaptureCamera(String macAddress);

    /**抓拍机转变为失效状态*/
    void invalidCaptureCamera(String macAddress);

    /**获取局域网内所有抓拍机，返回mac和ip组合的map*/
    ServerResponse getCaptureCameras();



}
