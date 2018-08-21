package cn.anytec.security.core.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by imyzt on 2018/8/17 15:32
 * 日期工具类
 */
public class DateUtil {

    /**
     * Date -> Str(yyyy-MM-dd)
     */
    public static String toStr(Date date) {

        return getDate("yyyy-MM-dd").format(date);
    }

    public static SimpleDateFormat getDate(String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf;
    }

}
