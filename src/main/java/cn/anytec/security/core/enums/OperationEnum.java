package cn.anytec.security.core.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by imyzt on 2018/8/27 10:50
 */
public class OperationEnum {

    public final static Map<String, String> operationObject = new HashMap<String, String>(){{
        put("user", "用户管理");
        put("log", "日志管理");
        put("camera", "设备管理");
        put("data", "数据查询");
        put("groupCamera", "设备组管理");
        put("groupPerson", "人员底库管理");
        put("history", "历史查询");
        put("person", "人员管理");
    }};
}
