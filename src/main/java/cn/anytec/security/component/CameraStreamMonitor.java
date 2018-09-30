package cn.anytec.security.component;


import cn.anytec.security.model.TbCamera;
import cn.anytec.security.websocket.WSSendHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
@EnableScheduling
public class CameraStreamMonitor {


    private static final Logger logger = LoggerFactory.getLogger(CameraStreamMonitor.class);


    @Value("${constant.serverLabel}")
    private String serverLabel;

    @Value("${camera.allProcess}")
    private String allProcessLabel;

    @Value("${camera.rtmpPrefix}")
    private String rtmpPrefix;

    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private WSSendHandler wsSendHandler;

    static volatile ConcurrentHashMap<String, FFmpegStreamTask> streamProcessLocal = new ConcurrentHashMap<String, FFmpegStreamTask>();


    @Scheduled(fixedDelay = 10000)
    public void monitorProgress() {
        //获取redis中实际运行的Process记录
        HashMap<String, String> monitorMap = (HashMap) redisTemplate.opsForHash().entries(allProcessLabel);
        //获取本实例跑的进程记录
        Set<String> streamProcessSetRedis = (HashSet) redisTemplate.opsForSet().members(serverLabel);
        monitorMap.forEach((k,v)->{
            //判断redis中记录单个camera的连接数，小于等于0时，则认为没有前端流连接，清除本实例中维护的Process进程。
            if(Integer.parseInt(v)<=0){
                //清理redis中的总活动camrea和单实例camera中的该camera记录。
                redisTemplate.opsForSet().remove(serverLabel,k);
                redisTemplate.opsForHash().delete(allProcessLabel,k);
                logger.info("无回显连接,停止rtmp服务："+k);
                if(!streamProcessLocal.keySet().contains(k)){
                    FFmpegStreamTask task = streamProcessLocal.get(k);
                    if(task.isAlive()){
                        streamProcessLocal.get(k).destory();
                        //wsSendHandler.sendUnsubscribe(k);
                    }
                }
            }else {
                if (streamProcessLocal.keySet().contains(k)) {
                    FFmpegStreamTask task = streamProcessLocal.get(k);
                    if (!task.isAlive()) {
                        String[] cmds = task.getCmds();
                        FFmpegStreamTask newTask = new FFmpegStreamTask(cmds);
                        newTask.setDaemon(true);
                        newTask.start();
                        if(newTask.isAlive()){
                            streamProcessLocal.put(k, newTask);
                        }
                        //wsSendHandler.sendUnsubscribe(k);
                    }
                }
            }
        });
    }

    public boolean newConnect(TbCamera camera) {
        if (camera != null) {
            //查看redis中是否有该流的处理进程
            if (redisTemplate.opsForHash().hasKey(allProcessLabel, camera.getSdkId())) {
                logger.info(String.valueOf(redisTemplate.opsForHash().get(allProcessLabel, camera.getSdkId())));
                redisTemplate.opsForHash().increment(allProcessLabel, camera.getSdkId(), 1);
                logger.info(camera.getSdkId() + " is already in all process,no need to start again");
                return true;
            }
            logger.info("new connection for stream,prepare to create new process");

            String[] cmds = {"/usr/bin/ffmpeg", "-loglevel","quiet","-i", camera.getStreamAddress(), "-c", "copy", "-f", "flv", "-an", rtmpPrefix+camera.getSdkId()};

            FFmpegStreamTask task = new FFmpegStreamTask(cmds);
            task.setDaemon(true);
            task.start();
            streamProcessLocal.put(camera.getSdkId(), task);

            redisTemplate.opsForHash().put(allProcessLabel, camera.getSdkId(), "1");
            redisTemplate.opsForSet().add(serverLabel, camera.getSdkId());

        }
        return false;
    }

    public boolean disconnect(String cameraSdkId) {
        logger.info("reduce connect for camera "+cameraSdkId);
        if (redisTemplate.opsForHash().hasKey(allProcessLabel, cameraSdkId)) {
            redisTemplate.opsForHash().increment(allProcessLabel, cameraSdkId, -1);
            return true;
        }
        return false;
    }

    public boolean destory(String cameraId) {
        FFmpegStreamTask fFmpegStreamTask = streamProcessLocal.get(cameraId);
        if (fFmpegStreamTask != null) {
            logger.info("关停rtmp服务："+cameraId);
            boolean isDestory= fFmpegStreamTask.destory();
            if(isDestory){
                redisTemplate.opsForHash().delete(allProcessLabel, cameraId);
                redisTemplate.opsForSet().remove(serverLabel,cameraId);
            }
            return isDestory;
        }
        return false;
    }

    public String createViewProcess(TbCamera camera){
        if(camera == null ||camera.getSdkId() == null){
            return "error";
        }
        //判断本实例和所有实例里是否有该转发进程在跑，如果没有则重新启动，如果有则检查进程状态，非激活状态则重新启动
        redisTemplate.opsForSet().add(serverLabel, camera.getSdkId());
        Map<String,String> allProcessLabelmap = redisTemplate.opsForHash().entries(allProcessLabel);
        String count = allProcessLabelmap.get(camera.getSdkId());
        if (count == null) {
            String rtsp = camera.getStreamAddress();
            String rtmp = rtmpPrefix+camera.getSdkId();
            String[] cmds = new String[]{"/usr/bin/ffmpeg", "-loglevel","quiet","-i", rtsp, "-c", "copy", "-f", "flv", "-an", rtmp};
            FFmpegStreamTask task = new FFmpegStreamTask(cmds);
            task.setDaemon(true);
            task.start();

            try {
                for(int i=0 ; i<12 ;i++){
                    Thread.sleep(500);
                    if(task.getExistValue() != null && task.getExistValue() == 1){
                        return "error";
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                if(task.getExistValue() != null && task.getExistValue() == 1){
                    return "error";
                }
            }
            redisTemplate.opsForHash().put(allProcessLabel, camera.getSdkId(), "1");
            streamProcessLocal.put(camera.getSdkId(), task);
            return "success";
        }else if(Integer.parseInt(count) > 0){
            checkHealthForProcess(camera);
//            FFmpegStreamTask fFmpegStreamTask = streamProcessLocal.get(camera.getSdkId());
//            if(null == fFmpegStreamTask || !fFmpegStreamTask.isActive()){
//                logger.info("local app can not find push stream process ,starting for cmds :{}",Arrays.toString(cmds));
//                FFmpegStreamTask task = new FFmpegStreamTask(cmds);
//                task.setDaemon(true);
//                task.start();
//                redisTemplate.opsForHash().put(allProcessLabel, camera.getSdkId(), "1");
//                streamProcessLocal.put(camera.getSdkId(), task);
//                redisTemplate.opsForSet().add(serverLabel,camera.getSdkId());
//            }
            return "exist";
        }
        return "error";
//        monitorProgress();
//        return createViewProcess(camera,addFlag);
    }

    private boolean checkHealthForProcess(TbCamera camera){
        String rtsp = camera.getStreamAddress();
        String rtmp = rtmpPrefix+camera.getSdkId();
        String[] cmds = new String[]{"/usr/bin/ffmpeg", "-loglevel","quiet","-i", rtsp, "-c", "copy", "-f", "flv", "-an", rtmp};
        FFmpegStreamTask fFmpegStreamTask = streamProcessLocal.get(camera.getSdkId());
        if(null == fFmpegStreamTask || !fFmpegStreamTask.isActive()){
            logger.info("local app can not find push stream process ,starting for cmds :{}",Arrays.toString(cmds));
            FFmpegStreamTask task = new FFmpegStreamTask(cmds);
            task.setDaemon(true);
            task.start();
            streamProcessLocal.put(camera.getSdkId(), task);
            redisTemplate.opsForSet().add(serverLabel,camera.getSdkId());
        }
//        if(addFlag){
//            redisTemplate.opsForHash().increment(allProcessLabel, camera.getSdkId(), 1);
//        }
        return false;
    }
}
