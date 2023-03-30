package com.danebrown.algtech.algcomp;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by danebrown on 2021/8/2
 * mail: tain198127@163.com
 *
 * @author danebrown
 */
@Target(value = ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface AlgName {
    /**
     * 算法名称
     *
     * @return
     */
    String value() default "";

    /**
     * 超时时间(S)
     * @return
     */
    int timeout() default DefaultValue.TIMEOUT;

    /**
     * 默认循环次数
     * @return
     */
    int times() default DefaultValue.TIMES;

    /**
     * 数据量范围
     * @return
     */
    int range() default DefaultValue.RANGE;

}
