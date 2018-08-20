package cn.anytec.security.websocket;

import cn.anytec.security.common.ServerResponse;
import cn.anytec.security.model.websocketmodel.EnrollResp;
import cn.anytec.security.model.websocketmodel.FdSnapShot;
import cn.anytec.security.model.websocketmodel.FrWarning;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class WSSendHandler {

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;
    @Autowired
    private WSHandler wsHandler;

    public void sendSnapShot(FdSnapShot snapshot,String cameraId){
        Map<String, String> monitorMap = wsHandler.getViewingCameraIdList();
        String count = monitorMap.get(cameraId);
        if(count != null && Integer.parseInt(count) > 0){
            simpMessagingTemplate.convertAndSend("/topic/camera/"+cameraId, ServerResponse.createBySuccess("normal",snapshot));
        }
    }
    public void sendWarning(FrWarning frWarning){
        simpMessagingTemplate.convertAndSend("/topic/camera/warning", ServerResponse.createBySuccess("warning",frWarning));
    }
    public void sendPlayStream(EnrollResp enrollResp,String cameraId){
        try {
            Thread.sleep(200);
            simpMessagingTemplate.convertAndSend("/topic/camera/"+cameraId, ServerResponse.createBySuccess("stream",enrollResp));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    /*public void sendUnsubscribe(String cameraId){
        simpMessagingTemplate.convertAndSend("/topic/camera/"+cameraId, ServerResponse.createBySuccess("unsubscribe",cameraId));
    }*/
    public void sendSnapshotOfDay(long times){
        simpMessagingTemplate.convertAndSend("/topic/camera/warning",ServerResponse.createBySuccess("snapshotOfDay",times));
    }
}
