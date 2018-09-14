package cn.anytec.security.util;

import java.util.Random;

/**
 * @Title: KeyUtil
 * @ProjectName security
 * @Description: 随机产生key值
 * @author: zhao
 * @date 2018/9/12 9:00
 */
public class KeyUtil {
    public static synchronized String generate() {
        Random random = new Random();
        Integer number = random.nextInt(900000) + 100000;
        return System.currentTimeMillis() + String.valueOf(number);
    }
}
