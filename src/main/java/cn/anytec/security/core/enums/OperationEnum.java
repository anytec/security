package cn.anytec.security.core.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by imyzt on 2018/8/27 10:50
 */
public class OperationEnum {

    public final static Map<String, String> operationObject = new HashMap<String, String>(){{
        put("user", "�û�����");
        put("log", "��־����");
        put("camera", "�豸����");
        put("data", "���ݿ��ӻ�");
        put("groupCamera", "��ʷ����");
        put("groupPerson", "���ع���");
        put("history", "��ʷ����");
        put("person", "��Ա����");
    }};
}
