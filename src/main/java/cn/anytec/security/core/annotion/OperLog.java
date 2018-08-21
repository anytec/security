package cn.anytec.security.core.annotion;

import java.lang.annotation.*;

/**
 * Created by imyzt on 2018/8/15 15:41
 * 标记需要记录业务日志的方法
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface OperLog {

    /**
     * 业务日志的名称，例如： ”添加摄像头“
     * 修改操作一定要以"修改"或"编辑"开头
     */
    String value() default "";

    /**
     * 业务日志的唯一标识，例如：摄像头的唯一标识是”cameraId“,多个字段才能唯一标识一条业务时使用英文逗号隔开
     */
    String key() default "id";

}
