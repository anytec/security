package cn.anytec.security.component.ipcamera.netty;

import cn.anytec.security.component.ipcamera.ipcService.IPCOperations;
import cn.anytec.security.constant.RedisConst;
import cn.anytec.security.model.TbCamera;
import cn.anytec.security.service.CameraService;
import com.alibaba.fastjson.JSONObject;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class UDPServerHandler extends SimpleChannelInboundHandler<DatagramPacket> {

//    @Value("${ipc.port}")
//    public int ipcPort;
//
//    @Value("${ipc.broadcast.response}")
//    public String ipcResponse;
//
//    @Value("${ipc.redis-key}")
//    public String ipcRedisKey;

    @Autowired
    private IPCOperations ipcOperations;
    @Autowired
    private CameraService cameraService;
    @Autowired
    private RedisTemplate redisTemplate;
   /* @Autowired
    private ProcessorMetrics processorMetrics;*/
   private static final Logger logger = LoggerFactory.getLogger(UDPServerHandler.class);
    private String captureCameras = RedisConst.CAPTURECAMERAS;
    private String captureCamerasInUse = RedisConst.CAPTURECAMERAS_INUSE;
    private String captureCameraOffline = RedisConst.CAPTURECAMERAS_OFFLINE;

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, DatagramPacket datagramPacket) throws Exception {
        String req = datagramPacket.content().toString(CharsetUtil.UTF_8);
        JSONObject request = JSONObject.parseObject(req);
        if (request != null && request.containsKey("ipAddress") && request.containsKey("macAddress")) {
            String macAddress=request.getString("macAddress");
            String ipAddress = request.getString("ipAddress");
            //回复抓拍机
            channelHandlerContext.writeAndFlush(
                    new DatagramPacket(Unpooled.copiedBuffer("accepted",
                            CharsetUtil.UTF_8), datagramPacket.sender()));
            logger.info("【抓拍机accepted】macAddress:{}",macAddress);
            //判断抓拍机是否在mysql库中
            TbCamera camera = cameraService.getCameraBySdkId(macAddress);
            if(camera != null){
                //判断离线摄像机的ip是否变化，更新播放rstp地址
                if(redisTemplate.opsForHash().hasKey(captureCameraOffline,macAddress)){
                    String redisIp = redisTemplate.opsForHash().get(captureCameraOffline,macAddress).toString();
                    updateCameraPlayAddr(ipAddress, camera, redisIp);
                    ipcOperations.deleteFromOfflineCache(macAddress);
                    ipcOperations.addToCache(macAddress,ipAddress);
                }
                if(camera.getCameraStatus().equals(1)){
                    logger.info("【抓拍机存在，状态为active】macAddress:{}",macAddress);
                    ipcOperations.activeCaptureCamera(macAddress);
                    ipcOperations.deleteFromCache(macAddress);
                    if(redisTemplate.opsForHash().hasKey(captureCamerasInUse,macAddress)){
                        String redisIp = redisTemplate.opsForHash().get(captureCamerasInUse,macAddress).toString();
                        updateCameraPlayAddr(ipAddress, camera, redisIp);
                    }
                    redisTemplate.opsForHash().put(captureCamerasInUse,macAddress,ipAddress);
                }else if(camera.getCameraStatus().equals(0)){
                    logger.info("【抓拍机存在，状态为standby】macAddress:{}",macAddress);
                    ipcOperations.standbyCaptureCamera(macAddress);
                    ipcOperations.deleteFromInUseCache(macAddress);
                    if(redisTemplate.opsForHash().hasKey(captureCameras,macAddress)){
                        String redisIp = redisTemplate.opsForHash().get(captureCameras,macAddress).toString();
                        updateCameraPlayAddr(ipAddress, camera, redisIp);
                    }
                    redisTemplate.opsForHash().put(captureCameras,macAddress,ipAddress);
                }
            }else {
                ipcOperations.addToCache(macAddress,ipAddress);
            }
        } else {
            channelHandlerContext.writeAndFlush(
                    new DatagramPacket(Unpooled.copiedBuffer("no ip detected",
                            CharsetUtil.UTF_8), datagramPacket.sender()));
            logger.info("【抓拍机no ip mac detected】");
        }
//        InetSocketAddress insocket = (InetSocketAddress) channelHandlerContext.channel().remoteAddress();
//        String clientIP = insocket.getAddress().getHostAddress();

    }

    private void updateCameraPlayAddr(String ipAddress, TbCamera camera, String redisIp) {
        logger.info("【redisIp】{}"+redisIp);
        logger.info("【receiveIp】{}",ipAddress);
        if(!redisIp.equals(ipAddress)){
            logger.info("【抓拍机ip发生变化】newIp:{}",ipAddress);
            String playAddress = camera.getPlayAddress();
            playAddress = playAddress.replace(redisIp,ipAddress);
            camera.setPlayAddress(playAddress);
            cameraService.update(camera);
        }
    }
//    class TestMetric extends SystemPublicMetrics{
//
//    }
}
