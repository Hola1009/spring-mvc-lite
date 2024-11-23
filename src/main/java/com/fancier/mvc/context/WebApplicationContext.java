package com.fancier.mvc.context;

import com.fancier.mvc.annotation.AutoWired;
import com.fancier.mvc.annotation.Controller;
import com.fancier.mvc.annotation.Service;
import com.fancier.mvc.xml.XMLParser;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * spring lite
 *
 * @author <a href="https://github.com/hola1009">fancier</a>
 */

public class WebApplicationContext {
    /**
     * bean 的全类名列表
     */
    public final List<String> classFullPathList;

    /**
     * 类 ioc 容器
     */
    public final ConcurrentHashMap<String, Object> ioc;

    {
        ioc = new ConcurrentHashMap<>();
        classFullPathList = new ArrayList<>();
    }

    public WebApplicationContext() {
    }

    /**
     * 初始化
     *
     */
    public void init(String xmlFile) {
        // 包扫描
        // 1. 获取扫描包路径
        String beanPackages = XMLParser.getBeanPackage(xmlFile);
        for (String beanPackage : beanPackages.split(",")) {
            scanPackage(beanPackage);
        }
        // 向容器中注入 bean
        executeInstance();
        // 向 bean 中依赖注入
        executeAutowired();
    }

    /**
     * 扫描包
     *
     */
    public void scanPackage(String pack) {
        URL url = this.getClass().getClassLoader()
                .getResource("/" + pack.replaceAll("\\.", "/"));

        String path = Objects.requireNonNull(url).getFile();
        File dir = new File(path);

        // 遍历目录
        for (File file : Objects.requireNonNull(dir.listFiles())) {
            if(file.isDirectory()) {
                scanPackage(pack + "."  + file.getName());
            } else {
                classFullPathList
                        .add(pack + "." + file.getName().replace(".class", ""));
            }
        }
    }

    /**
     * 实例化 bean 并注入 ioc 容器中
     *
     */
    public void executeInstance() {
        // 判断是否扫描到类
        if (classFullPathList.isEmpty()) {
            return;
        }
        try {
            for (String classFullPath : classFullPathList) {
                Class<?> clazz = Class.forName(classFullPath);
                if (clazz.isAnnotationPresent(Controller.class)) {
                    // 获取小驼峰类名
                    String beanName = getBeanName(clazz);

                    // 实例化
                    Object bean = clazz.newInstance();

                    // 存入
                    ioc.put(beanName, bean);
                } else if (clazz.isAnnotationPresent(Service.class)) {
                    Service serviceAnnotation = clazz.getAnnotation(Service.class);
                    String beanName = serviceAnnotation.value();

                    // 实例化
                    Object bean = clazz.newInstance();

                    // 如果没有指定 beanName 则使用接口名作为 beanName
                    if (StringUtils.isBlank(beanName)) {
                        Class<?>[] interfaces = clazz.getInterfaces();
                        for (Class<?> interfaze : interfaces) {
                            beanName = getBeanName(interfaze);
                            ioc.put(beanName, bean);
                        }
                    } else { // 如果指定了 beanName 则使用指定的 beanName
                        ioc.put(beanName, bean);
                    }

                }
            }
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 执行依赖注入
     *
     */
    private void executeAutowired() {
       ioc.forEach((key, bean) -> {
           Class<?> clazz = bean.getClass();
           if (clazz.isAnnotationPresent(Controller.class)) {
               Field[] fields = clazz.getDeclaredFields();
               for (Field field : fields) {
                   if (field.isAnnotationPresent(AutoWired.class)) {
                       AutoWired autoWiredAnnotation = field.getAnnotation(AutoWired.class);
                       // 默认 按照 注解上的 value 查找 bean
                       String name = autoWiredAnnotation.value();

                       // 如果没有指定 beanName 则使用小驼峰类名作为 beanName
                       if (StringUtils.isBlank(name)) {
                           name = getBeanName(field.getType());
                       }

                       Object dependency = ioc.get(name);

                       // 如果没有找到 bean 则使用属性名作为 beanName
                       if (Objects.isNull(dependency)) {
                           name = field.getName();
                           dependency = ioc.get(name);
                       }

                       //防止属性是private, 暴力破解
                       field.setAccessible(true);

                       // 依赖注入
                       try {
                           field.set(bean, dependency);
                       } catch (IllegalAccessException e) {
                           throw new RuntimeException(e);
                       }
                   }
               }
           }
       });
    }


    /**
     * 获取小驼峰类名即默认 beanName
     *
     */
    private String getBeanName(Class<?> clazz) {
        String className = clazz.getSimpleName();
        char lowerCase = Character.toLowerCase(clazz.getSimpleName().charAt(0));
        return lowerCase + className.substring(1);
    }
}
