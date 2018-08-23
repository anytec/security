package cn.anytec.security.core.annotion;

import java.lang.annotation.*;

/**
 * Created by imyzt on 2018/8/21 16:33
 * 权限验证
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface Permission {

    /**
     * eg: 查询用户列表
     */
    String value() default "";

    /**
     * 需要验证的method
     */
    String method() default "";


}
