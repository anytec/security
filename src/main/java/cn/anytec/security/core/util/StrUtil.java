package cn.anytec.security.core.util;

/**
 * Created by imyzt on 2018/8/17 15:22
 * 字符串工具类
 */
public class StrUtil {

    /**
     * 首字母变小写
     * @param str
     * @return
     */
    public static String firstCharToLowerCase(String str) {

        char[] chars = str.toCharArray();
        if (chars[0] >= 'A' && chars[0] <= 'Z') {
            chars[0] += 32;
            return new String(chars);
        }
        return str;
    }

}
