package cn.anytec.security.component.ipcamera.ipcService;

import java.util.Map;

public interface IPCOperations {

    boolean addToCache(String macAddress, String ipcAddress);

    void deleteFromCache(String mac);

    boolean activeCaptureCamera(String macAddress, String ipcAddress);

    boolean invalidCaptureCamera(String macAddress, String ipcAddress);

    Map<String,String> getCaptureCameras();



}
