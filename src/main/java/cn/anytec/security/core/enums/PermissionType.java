package cn.anytec.security.core.enums;

/**
 * Created by imyzt on 2018/8/21 17:48
 * 标记需要检验的权限类型 <br/>
 * eg: (是否是管理员?是否是普通用户?...)
 */
public interface PermissionType {

    /** 权限检验方法全限定名 */
    String PERMISSION_FACTORY = "cn.anytec.security.core.util.PermissionFactory";

    /** 是否管理员 */
    String IS_ADMIN = "isAdmin";



}
