package cn.anytec.security.core.util;

import cn.anytec.security.config.GeneralConfig;
import cn.anytec.security.core.enums.SecurityExceptionEnum;
import cn.anytec.security.core.exception.BussinessException;
import org.apache.commons.lang3.StringUtils;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by imyzt on 2018/8/15 16:09
 * 对比两个对象的变化的工具类
 */
public class Contrast {

    /** 分隔符 */
    public static final String separator = "\r\n";

    /**
     * @param o1
     * @param o2
     * @return
     */
    public static String contrastObj(Object o1, Map<String, String> o2) throws IntrospectionException, InvocationTargetException, IllegalAccessException {

        if (null == o1) {
            throw new BussinessException(SecurityExceptionEnum.REQUEST_NULL.getCode(), "非法请求.请先打开页面");
        }

        Class<?> o1Clazz = o1.getClass();
        Field[] fields = o1Clazz.getDeclaredFields();
        StringBuilder sb = new StringBuilder();

        for (Field field : fields) {
            // 序列号信息不对比
            if ("serialVersionUID".equals(field.getName())) {
                continue;
            }

            // 获取字段的get方法
            PropertyDescriptor pd = new PropertyDescriptor(field.getName(), o1Clazz);
            Method getMethod = pd.getReadMethod();

            // 执行get方法,获取第一个对象的value
            Object o1value = getMethod.invoke(o1);

            // 将第一个对象的name值作为key, 从第二个对象中(map)获取value
            Object o2value = o2.get(StrUtil.firstCharToLowerCase(getMethod.getName().substring(3)));

            if (null == o1value || null == o2value) {
                continue;
            }

            // 恢复成相应的对象
            if (o1value instanceof Date) {
                o1value = DateUtil.toStr((Date) o1value);
            }else if (o1value instanceof Integer) {
                o2value = Integer.parseInt(o2value.toString());
            }

            // 控制分隔符
            int SEPARATOR_INDEX = 1;

            // 如果不相同
            if (!o1value.toString().equals(o2value.toString())) {
                if (SEPARATOR_INDEX != 1) {
                    sb.append(separator);
                }
                sb.append("字段名称:").append(field.getName()).
                        append(",旧值:").append(o1value).
                        append(",新值:").append(o2value);

                SEPARATOR_INDEX++;
            }
        }
        return new String(sb);
    }

    /**
     * 解析多个key值,并获取业务主键值
     * @param key 主键值
     * @param req 请求参数
     * @return
     */
    public static String parseMutiKey(String key, HashMap<String, String> req) {

        StringBuilder sb = new StringBuilder(4);
        if (StringUtils.isBlank(key)) {
            return null;
        }
        if (key.indexOf(",") != -1){
            String[] keys = key.split(",");
            for (String ky : keys) {
                // 主键在此次请求中的请求内容
                String value = req.get(ky);
                sb.append(",").append(ky).append(" = ").append(value);
            }
            return sb.toString().substring(1);
        }else {
            // 主键在此次请求中的请求内容
            String value = req.get(key);
            sb.append(key).append(" = ").append(value);
            return sb.toString();
        }
    }

    /**
     * 转换权限
     * @param role
     * @return
     */
    public static String parseRole(int role) {
        GeneralConfig config = ApplicationContextHolder.getBean(GeneralConfig.class);
        if (role == config.getUserRole()) {
            return "用户";
        }else if (role == config.getAdminRole()) {
            return "管理员";
        }
        return "";
    }

}
