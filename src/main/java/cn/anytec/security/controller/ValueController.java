package cn.anytec.security.controller;

import cn.anytec.security.common.ServerResponse;
import cn.anytec.security.config.GeneralConfig;
import cn.anytec.security.constant.RedisConst;
import cn.anytec.security.core.enums.WarnningVoice;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @Description: TODO
 * @author: zhao
 * @date 2018/9/29 10:30
 */
@RestController
@RequestMapping("/")
public class ValueController {

    private static final Logger logger = LoggerFactory.getLogger(ValueController.class);

    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private GeneralConfig config;

    private String warningThreshold = RedisConst.WARNING_THRESHOLD;
    private String identifySnapThresold = RedisConst.IDENTIFY_SNAP_THRESOLD;

    /**设置预警阈值*/
    @RequestMapping("/setWarningThreshold")
    @ResponseBody
    public ServerResponse setWarningThreshold(@RequestParam("threshold") String threshold){
        logger.info("【setWarningThreshold】threshold: {}",threshold);
        if(!StringUtils.isEmpty(threshold)){
            redisTemplate.opsForValue().set(warningThreshold,String.valueOf(thresholdConvert(threshold)),1, TimeUnit.DAYS);
            return ServerResponse.createBySuccess("setWarningThreshold success");
        }
        return ServerResponse.createByErrorMessage("setWarningThreshold传入的threshold值有误：{}"+threshold);
    }

    /**获取预警阈值*/
    @RequestMapping("/getWarningThreshold")
    @ResponseBody
    public ServerResponse getWarningThreshold(){
        int thresholdValue;
        String threshold = (String)redisTemplate.opsForValue().get(warningThreshold);
        if(!StringUtils.isEmpty(threshold)){
            thresholdValue = (int)((Double.parseDouble(threshold))*100);
        }else {
            thresholdValue = (int)(Double.parseDouble(config.getWarningThreshold())*100);
        }
        return ServerResponse.createBySuccess(thresholdValue);
    }

    /**设置人脸搜索的阈值*/
    @RequestMapping("/setIdentifySnapThreshold")
    @ResponseBody
    public ServerResponse setIdentifySnapThreshold(@RequestParam("threshold") String threshold){
        if(!StringUtils.isEmpty(threshold)){
                redisTemplate.opsForValue().set(identifySnapThresold,String.valueOf(thresholdConvert(threshold)),1, TimeUnit.DAYS);
                return ServerResponse.createBySuccess("setIdentifySnapThreshold success");
            }
        return ServerResponse.createByErrorMessage("setIdentifySnapThreshold传入的threshold值有误："+ threshold);
    }

    /**获取人脸搜索的阈值*/
    @RequestMapping("/getIdentifySnapThreshold")
    @ResponseBody
    public ServerResponse getIdentifySnapThreshold(){
        int thresholdValue;
        String threshold = (String)redisTemplate.opsForValue().get(identifySnapThresold);
        if(!StringUtils.isEmpty(threshold)){
            thresholdValue = (int)((Double.parseDouble(threshold))*100);
        }else {
            thresholdValue = (int)(Double.parseDouble(config.getIdentifySnapThreshold())*100);
        }
        return ServerResponse.createBySuccess(thresholdValue);
    }

    /**获取预警声音*/
    @RequestMapping("/getWarningVoice")
    @ResponseBody
    public ServerResponse getWarningVoice(){
        List<String> voiceList = new ArrayList<>();
        WarnningVoice[] warnningVoices = WarnningVoice.values();
        for(WarnningVoice voice :warnningVoices){
            voiceList.add(voice.getMsg());
        }
        return ServerResponse.createBySuccess(voiceList);
    }

    private double thresholdConvert(String threshold){
        double thresholdValue = Double.parseDouble(threshold);
        if(thresholdValue > 1){
            thresholdValue = thresholdValue/100;
        }
        return thresholdValue;
    }
}
