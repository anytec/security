package cn.anytec.security.core.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static cn.anytec.security.core.enums.OperationEnum.operationObject;

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

    public static String replaceOperationObj(String requestURI) {

        Pattern p = Pattern.compile("/(.*?)/");
        Matcher m = p.matcher(requestURI);
        String operationObj = m.find() ? m.group(1) : "";

        return operationObject.get(operationObj);
    }

}
