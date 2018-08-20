package cn.anytec.security.config;
/**
 * 服务器启动时自动执行
 */

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;
import java.util.Set;


@Configuration
@Order(value = 1)
public class MyApplicationRunner implements ApplicationRunner {

    private static final Logger logger = LoggerFactory.getLogger(MyApplicationRunner.class);
    @Autowired
    private RedisTemplate redisTemplate;
    @Value("${constant.serverLabel}")
    private String serverLabel;
    @Value("${camera.allProcess}")
    private String allProcessLabel;

    @Override
    public void run(ApplicationArguments arg) throws Exception {
//        logger.info("====== 启动时执行 =======");
        logger.info("开始删除redis库中的原先该端口应用的摄像头列表。。。。。。。");
//        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if(redisTemplate.hasKey(serverLabel)) {
            Set<String> preStream = redisTemplate.opsForSet().members(serverLabel);
            preStream.forEach(singleCamera->{
                if(redisTemplate.opsForHash().hasKey(allProcessLabel,singleCamera)) {
                    redisTemplate.opsForHash().delete(allProcessLabel, singleCamera);
                }
            });
            redisTemplate.delete(serverLabel);
        }
        /*logger.info("删除完毕");
        logger.info("启动fkvideo视频侦测！");
        String[] cmd = new String[]{"/usr/bin/fkvideo_detector"
                ,"--start-ts"
                ,format.format(System.currentTimeMillis())
                ,"-c"
                ,"/home/anytec-z/tmp/fkvideo.ini"
        };
        FFmpegStreamTask task = new FFmpegStreamTask(cmd);
        task.setDaemon(true);
        task.start();*/
    }

}
