package cn.anytec.security.websocket;

import cn.anytec.security.model.websocketmodel.EnrollResp;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.*;

@Controller
public class WSController {

    @Value("${camera.rtmpPrefix}")
    private String rtmpPrefix;
    @Autowired
    private WSHandler wsHandler;
    @Autowired
    private WSSendHandler wsSendHandler;
    @Autowired
    RedisTemplate redisTemplate;

    private static Logger logger = LoggerFactory.getLogger(WSController.class);

    @MessageMapping("/camera/registry")
    public void cameraLogin(SimpMessageHeaderAccessor headerAccessor, @RequestBody String message){
        String sessionId = (headerAccessor.getSessionId());
        JSONObject json = JSONObject.parseObject(message);
        String cameraId = json.getString("cameraId");
        String previous = json.getString("previous");
        String playerId = json.getString("playerId");
        String guid = json.getString("guid");
        if(cameraId == null){
            return;
        }
        EnrollResp enrollResp = new EnrollResp(playerId,rtmpPrefix+cameraId,guid);
        if(playerId == null){
            return;
        }
        if(previous != null && previous.equals(cameraId)){
            enrollResp.setPlay_stream("");
            //此处是进程健康性检查
            wsHandler.registerCamera(cameraId,sessionId,false);
            wsSendHandler.sendPlayStream(enrollResp,cameraId);
            return;
        }
        wsSendHandler.sendPlayStream(enrollResp,cameraId);
        if(null != previous && !cameraId.equals(previous) && !previous.equals("")){
            logger.info("remove camera : "+previous+" , sessionId : "+sessionId);
            wsHandler.removeSessionId(previous,sessionId);
        }
        logger.info("previous : "+previous);
        logger.info("camera register : "+cameraId +" , sessionId : "+sessionId);
        String info = wsHandler.registerCamera(cameraId,sessionId,true);

        logger.info("正确推送rtmp播放流地址："+cameraId);
        if(info != null && info.equals("error")){
            logger.error("rtmp流出现错误！");
            enrollResp.setPlay_stream("error");
            wsSendHandler.sendPlayStream(enrollResp,cameraId);
            return;
        }

    }

    @EventListener
    public void onDisconnectEvent(SessionDisconnectEvent event) {
        String sessionId = event.getSessionId();
        logger.info("session disconnect : "+sessionId);
        wsHandler.removeSessionId(sessionId);
    }

    @RequestMapping("/preview")
    @ResponseBody
    public void tt(){
        Object obj1 = redisTemplate.opsForHash().get("stream-active","camera1");
        if(obj1 instanceof String){
            System.out.println("RedisCount_camera1:"+obj1);
        }
        Object obj2 = redisTemplate.opsForHash().get("stream-active","camera2");
        if(obj2 instanceof String){
            System.out.println("RedisCount_camera2:"+obj2);
        }
        wsHandler.getCameraId_sessionIdList_map().forEach((k,v)->{
            v.forEach(n->{
                System.out.println(k+":"+n);
            });
        });

    }

    @RequestMapping("/ttt")
    @ResponseBody
    public String ttt(){
        Map<String,List<String>> mm = wsHandler.getCameraId_sessionIdList_map();
        Map<String,Object> map = new HashMap<>();
        mm.forEach((k,v)->{
            map.put(k,v.toString());
        });
        return new JSONObject(map).toJSONString();
    }



}
