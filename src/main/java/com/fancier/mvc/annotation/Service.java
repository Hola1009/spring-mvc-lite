package com.fancier.mvc.annotation;

import java.lang.annotation.*;

/**
 *
 *
 * @author <a href="https://github.com/hola1009">fancier</a>
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Service {
    String value() default "";
}
