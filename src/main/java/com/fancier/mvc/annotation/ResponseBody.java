package com.fancier.mvc.annotation;

import java.lang.annotation.*;

/**
 * 标识控制器组件
 *
 * @author <a href="https://github.com/hola1009">fancier</a>
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ResponseBody {
    String value() default "";
}
