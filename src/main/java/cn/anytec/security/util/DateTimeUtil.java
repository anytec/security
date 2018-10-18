package cn.anytec.security.util;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CancellationException;

public class DateTimeUtil {

    //joda-time

    //str->Date
    //Date->str
    public static final String STANDARD_FORMAT = "yyyy-MM-dd HH:mm:ss";



    public static Date strToDate(String dateTimeStr,String formatStr){
        DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern(formatStr);
        DateTime dateTime = dateTimeFormatter.parseDateTime(dateTimeStr);
        return dateTime.toDate();
    }

    public static String dateToStr(Date date,String formatStr){
        if(date == null){
            return StringUtils.EMPTY;
        }
        DateTime dateTime = new DateTime(date);
        return dateTime.toString(formatStr);
    }

    public static Date strToDate(String dateTimeStr){
        DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern(STANDARD_FORMAT);
        DateTime dateTime = dateTimeFormatter.parseDateTime(dateTimeStr);
        return dateTime.toDate();
    }

    public static String dateToStr(Date date){
        if(date == null){
            return StringUtils.EMPTY;
        }
        DateTime dateTime = new DateTime(date);
        return dateTime.toString(STANDARD_FORMAT);
    }

    /**
     * 获取当前时间N天前的时间
     * @param past N
     * @return
     */
    public static Date daysAgo(int past, Date today, String format) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(today);
        calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) - past + 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        Date daysAgo = calendar.getTime();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        try {
            String now = simpleDateFormat.format(daysAgo);
            return simpleDateFormat.parse(now);
        } catch (ParseException e) {
            throw new SecurityException("时间转换异常");
        }
    }

    public static List<String> daysAgoStr(int past, Date today, String format) {

        Calendar instance = Calendar.getInstance();
        instance.setTime(today);
        ArrayList<String> result = new ArrayList<>();
        for (int i = 1; i <= past; i++) {
            Date time = instance.getTime();
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            String now = sdf.format(time);
            result.add(now);
            instance.set(Calendar.DAY_OF_YEAR, instance.get(Calendar.DAY_OF_YEAR) - 1);
        }
        Collections.reverse(result);
        return result;
    }

    public static List<Long> generateTimestamp(int past, Date today) {

        // 抓拍机只到秒,不需要毫秒
        Date daysAgo = daysAgo(past, today, "yyyy-MM-dd HH:mm:ss");

        ArrayList<Long> result = new ArrayList<>();

        Calendar instance = Calendar.getInstance();
        instance.setTime(daysAgo);

        result.add(instance.getTimeInMillis());

        for (int i = 0; i < past; i++) {
            instance.add(Calendar.DAY_OF_YEAR, 1);
            result.add(instance.getTimeInMillis());
        }
        return result;
    }

    /**
     * 从request中获取前台传输的统计时间. 如果没有,按照past时间默认统计
     * @param paramMap 前台参数
     * @param past 默认过去多少天
     * @param format 时间格式
     * @return
     */
    public static Date[] getDaysAgoAndToday(Map<String, String[]> paramMap, int past, String format) {

        Date today, daysAgo;
        String[] startTimes = new String[0], endTimes = new String[0];
        if (paramMap.containsKey("startTime") && paramMap.containsKey("endTime")) {
            startTimes = paramMap.get("startTime");
            endTimes = paramMap.get("endTime");
        }
        long startTime, endTime;
        // 默认取前台传的时间区间
        if (startTimes.length == 1 && endTimes.length == 1) {
            startTime = Long.valueOf(startTimes[0]);
            endTime = Long.valueOf(endTimes[0]);

            today = new Date(endTime);
            daysAgo = new Date(startTime);
            // 前台没有传时间区间默认取过去一周
        }else {
            Calendar instance = Calendar.getInstance();
            instance.setTime(new Date());
            instance.set(Calendar.HOUR_OF_DAY, 23);
            instance.set(Calendar.MINUTE, 59);
            instance.set(Calendar.SECOND, 59);
            // 今天 23:59:59
            today = instance.getTime();
            // 一周前
            daysAgo = DateTimeUtil.daysAgo(past, today, format);
        }

        Date[] dates = new Date[2];
        dates[0] = daysAgo;
        dates[1] = today;
        return dates;
    }

    public static JSONObject countTime(Date daysAgo, Date today, int past, ArrayList<Map<String, Map<String, Object>>> ret) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(daysAgo);

        // 一周7天
        List<String> daysAgoStr = daysAgoStr(past, today, "yyyy-MM-dd");

        JSONObject result = new JSONObject();

        for (int i = 1; i <= daysAgoStr.size(); i++) {

            if (null != ret && ret.size() > 0) {

                Map<String, List<Integer>> resultCameraMap = new HashMap<>();
                for (Map<String, Map<String, Object>> cameraMap : ret) {

                    for (Map.Entry<String, Map<String, Object>> camera : cameraMap.entrySet()) {
                        // 单个设备
                        String cameraSdkId = camera.getKey();
                        Map<String, Object> countAndTimeList = camera.getValue();
                        Integer count = (Integer) countAndTimeList.get("count");
                        int[] countIndex = (int[]) countAndTimeList.get("countIndex");

                        ArrayList<Integer> daysCount = new ArrayList<>();
                        daysCount.add(0);
                        for (int k = (1 + (i - 1) * 12) - 1; k < (1 + (i - 1) * 12) + 12 - 1; k++) {
                            daysCount.add(countIndex[k]);
                        }
                        daysCount.add(daysCount.stream().mapToInt(Integer::intValue).sum());
                        resultCameraMap.put(cameraSdkId, daysCount);
                    }
                }

                result.put(daysAgoStr.get(i - 1), resultCameraMap);
            }
        }

        return result;
    }

    public static void main(String[] args) {
        List<Long> longs = generateTimestamp(7, new Date());
        System.out.println(longs);
    }



}
