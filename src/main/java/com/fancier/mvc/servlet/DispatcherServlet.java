package com.fancier.mvc.servlet;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.fancier.mvc.annotation.Controller;
import com.fancier.mvc.annotation.RequestMapping;
import com.fancier.mvc.annotation.RequestParam;
import com.fancier.mvc.annotation.ResponseBody;
import com.fancier.mvc.context.WebApplicationContext;
import com.fancier.mvc.handler.UrlHandler;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 充当原生的 DispatcherServlet
 *
 * @author <a href="https://github.com/hola1009">fancier</a>
 */

public class DispatcherServlet extends HttpServlet {

    /**
     * handler 映射关系集合
     */
    private final List<UrlHandler> handlerList;

    /**
     * 工程路径
     */
    private String contextPath;

    {
        handlerList = new ArrayList<>();
    }

    /**
     * 类 spring 容器
     */
    WebApplicationContext webApplicationContext;

    /**
     * 初始化方法
     * @param servletConfig servlet 配置
     */
    @Override
    public void init(ServletConfig servletConfig) {
        // 获取工程路径
        this.contextPath = servletConfig.getServletContext().getContextPath();

        // 创建 类 spring 容器
        this.webApplicationContext = new WebApplicationContext();

        // 初始化, 包扫描
        // 1. 解析 mvc.xml 配置文件路径
        String configLocation = servletConfig.getInitParameter("contextConfigLocation");
        webApplicationContext.init(configLocation.split(":")[1]);
        this.initHandlerMapping();
        System.out.println();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        this.doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        this.executeDispatcher(req, resp);
    }

    /**
     * 初始化 handler 映射关系
     */
    private void initHandlerMapping() {
        if (webApplicationContext.ioc.isEmpty()) {
            return;
        }
        for (Map.Entry<String, Object> entry : webApplicationContext.ioc.entrySet()) {

            Object bean = entry.getValue();
            // 通过反射获取到 methods
            Class<?> clazz = bean.getClass();
            if (!clazz.isAnnotationPresent(Controller.class)) {
                continue;
            }
            Method[] methods = clazz.getMethods();

            for (Method method : methods) {
                if (method.isAnnotationPresent(RequestMapping.class)) {
                    // 获取 url
                    RequestMapping annotation = method.getAnnotation(RequestMapping.class);
                    String url = contextPath + annotation.value();
                    // 存入
                    handlerList.add(new UrlHandler(url, bean, method));
                }
            }
        }
    }

    /**
     * 通过 uri 匹配 handler 映射关系
     *
     */
    private UrlHandler getUrlHandler(HttpServletRequest request) {
        // 1. 获取用户请求的 uri
        // uri = /fancier/monster/list
        String requestURI = request.getRequestURI();
        for (UrlHandler urlHandler : handlerList) {
            if (urlHandler.getUrl().equals(requestURI)) {
                return urlHandler;
            }
        }
        return null;
    }

    /**
     * 执行分发器
     *
     */
    private void executeDispatcher(HttpServletRequest request, HttpServletResponse response) {
        UrlHandler urlHandler = getUrlHandler(request);

        // 请求资源不存在
        try {
            // 设置编码格式
            request.setCharacterEncoding("UTF-8");

            if (Objects.isNull(urlHandler)) {
                response.getWriter().println("<h1>404 NOT FOUND</h1>");
                return;
            }

            Object controller = urlHandler.getController();
            Method method = urlHandler.getMethod();
            // 解析 url 参数
            Parameter[] params = method.getParameters();
            Object[] args = new  Object[params.length];
            for (int i = 0; i < params.length; i++) {
                Parameter parameter = params[i];
                Object argument = null;
                if(HttpServletRequest.class.equals(parameter.getType())) {
                    // 解析 HttpServletRequest
                    argument = request;
                } else if (HttpServletResponse.class.equals(parameter.getType())) {
                    // 解析 HttpServletResponse
                    argument = response;
                } else if (parameter.isAnnotationPresent(RequestParam.class)) {
                    // 解析 @RequestParam
                    String value = parameter.getAnnotation(RequestParam.class).value();
                    argument = request.getParameter(value);
                } else if (Objects.nonNull(request.getParameter(parameter.getName()))) {
                    // 解析 url 路径参数
                    argument = request.getParameter(parameter.getName());
                }
                args[i] = argument;
            }

            // 执行目标方法
            Object result = method.invoke(controller, args);

            // 视图解析
            if (method.isAnnotationPresent(ResponseBody.class)
                    || controller.getClass().isAnnotationPresent(ResponseBody.class)) { // 解析 @ResponseBody 注解
                response.setContentType("application/json;charset=utf-8");
                JSONObject jsonStr = JSONUtil.parseObj(result);
                response.getWriter().println(jsonStr);
            } else if(result instanceof String) { // 解析 String 类型
                String viewName = (String) result;
                if (viewName.contains(":")) {
                    String[] split = viewName.split(":");
                    String viewType = split[0];
                    String viewPage = split[1];

                    if("forward".equals(viewType)) {
                        request.getRequestDispatcher(viewPage)
                                .forward(request, response);
                    } else if("redirect".equals(viewType)) {
                        response.sendRedirect(viewPage);
                    }
                } else { // 默认是请求转发
                    request.getRequestDispatcher(viewName)
                            .forward(request, response);
                }
            }
        } catch (IOException | IllegalAccessException | InvocationTargetException | ServletException e) {
            throw new RuntimeException(e);
        }
    }
}
