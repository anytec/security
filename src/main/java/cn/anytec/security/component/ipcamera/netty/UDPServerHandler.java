package cn.anytec.security.component.ipcamera.netty;

import cn.anytec.security.component.ipcamera.ipcService.IPCOperations;
import com.alibaba.fastjson.JSONObject;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import io.netty.util.CharsetUtil;
import org.springframework.beans.factory.annotation.Autowired;
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
   /* @Autowired
    private ProcessorMetrics processorMetrics;*/


    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, DatagramPacket datagramPacket) throws Exception {
        String req = datagramPacket.content().toString(CharsetUtil.UTF_8);
        System.out.println(req);
        JSONObject request = JSONObject.parseObject(req);
        if (request != null && request.containsKey("ipAddress") && request.containsKey("macAddress")) {
            String macAddress=request.getString("macAddress");
            String ipAddress = request.getString("ipAddress");
            if(ipcOperations.addToCache(macAddress,ipAddress)){
                channelHandlerContext.writeAndFlush(
                        new DatagramPacket(Unpooled.copiedBuffer("accepted",
                                CharsetUtil.UTF_8), datagramPacket.sender()));
            }
        } else {
            channelHandlerContext.writeAndFlush(
                    new DatagramPacket(Unpooled.copiedBuffer("no ip detected",
                            CharsetUtil.UTF_8), datagramPacket.sender()));
        }
//        InetSocketAddress insocket = (InetSocketAddress) channelHandlerContext.channel().remoteAddress();
//        String clientIP = insocket.getAddress().getHostAddress();

    }
//    class TestMetric extends SystemPublicMetrics{
//
//    }
}
