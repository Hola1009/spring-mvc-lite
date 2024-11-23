package com.fancier.mvc.annotation;

import java.lang.annotation.*;

/**
 *
 *
 * @author <a href="https://github.com/hola1009">fancier</a>
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestMapping {
    String value() default "";
}
