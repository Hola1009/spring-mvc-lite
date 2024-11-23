package com.fancier.mvc.handler;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.lang.reflect.Method;

/**
 * url 和 控制器方法的映射关系
 *
 * @author <a href="https://github.com/hola1009">fancier</a>
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UrlHandler {
    private String url;
    private Object controller;
    private Method method;
}
