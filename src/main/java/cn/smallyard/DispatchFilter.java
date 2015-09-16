package cn.smallyard;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.smallyard.router.Action;
import cn.smallyard.router.Package;
import cn.smallyard.router.Result;
import cn.smallyard.router.Router;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class DispatchFilter implements Filter {

    private static Log logger = LogFactory.getLog(DispatchFilter.class);
    private static Map<String, Package> routeMap;
    private static Map<String, Result> globalResultsMap;

    //初始化路由map
    public void init(FilterConfig filterConfig) throws ServletException {
        Router router = new Router();
        routeMap = router.getRouterMap();
        globalResultsMap = router.getGlobalResultsMap();
    }

    public void destroy() {
        routeMap.clear();
    }

    //filter入口  处理所有请求
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        httpRequest.setCharacterEncoding("utf-8");
        String urlString = httpRequest.getServletPath();
        String[] subStrings = StringUtils.split(urlString, "/");
        if (subStrings.length == 2) {
            String packageName = subStrings[0];
            String actionName = subStrings[1].substring(0, subStrings[1].lastIndexOf("."));
            if (routeMap.get(packageName) != null && routeMap.get(packageName).getActionMap().get(actionName) != null) {
                Action actionConfig = routeMap.get(packageName).getActionMap().get(actionName);
                Map<String, Result> resultMap = actionConfig.getResultMap();
                String actionClassPath = actionConfig.getClazz();
                if (actionClassPath != null) {
                    String methodName = actionConfig.getMethod();
                    try {
                        Object action = Class.forName(actionClassPath).newInstance();
                        doContext(httpRequest, httpResponse, action);
                        doModel(httpRequest, action);
                        Method method = action.getClass().getMethod(methodName);
                        Object retStr = method.invoke(action);
                        if (retStr != null) {
                            Result result = resultMap.get(retStr);
                            if (result == null) {//如果找不到result，则从global-result中取
                                result = globalResultsMap.get(retStr);
                            }
                            dispatch(result, httpRequest, httpResponse);
                        }
                    } catch (Exception e) {
                        logger.error("", e);
                    }
                } else {
                    dispatch(resultMap.get("success"), httpRequest, httpResponse);
                }
            } else {
                logger.info("Can't find package '" + packageName + "' or action:" + actionName + "'");
                httpResponse.setStatus(404);
            }
        } else {
            logger.info("Can't route URL '" + urlString + "'");
            httpResponse.setStatus(404);
        }
    }

    //渲染result
    private void dispatch(Result result, HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        if (result.getType() != null && result.getType().equals("redirect")) {
            try {
                httpResponse.sendRedirect(result.getUrl());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                if (result.getUrl() != null && !result.getUrl().equals("")) {
                    httpRequest.getRequestDispatcher(result.getUrl()).forward(httpRequest, httpResponse);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //将请求参数封装为model并注入action类
    private void doModel(HttpServletRequest httpRequest, Object action) throws Exception {
        Map<String, Map<String, String>> paraMap = getParaMap(httpRequest);
        Field[] fields = action.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (paraMap.containsKey(field.getName())) {//自定义类型
                Map<String, String> map = paraMap.get(field.getName());
                Class<?> fieldClass = field.getType();
                Object model = fieldClass.newInstance();
                Field[] modelField = model.getClass().getDeclaredFields();
                for (Field aModelField : modelField) {
                    String name = aModelField.getName();
                    if (map.get(name) != null) {
                        BeanUtils.setProperty(model, name, map.get(name));
                    }
                }
                BeanUtils.setProperty(action, field.getName(), model);
            } else if (paraMap.get("_base").containsKey(field.getName())) {//基础类型
                BeanUtils.setProperty(action, field.getName(), paraMap.get("_base").get(field.getName()));
            } else {
                logger.info("can not find '" + field.getName() + "' from Form.");
            }
        }

    }

    //获取请求参数
    private Map<String, Map<String, String>> getParaMap(HttpServletRequest httpRequest) {
        Map<String, Map<String, String>> paraMap = new HashMap<String, Map<String, String>>();
        Map<String, String> baseMap = new HashMap<String, String>();
        paraMap.put("_base", baseMap);
        for (Object key : httpRequest.getParameterMap().keySet()) {
            String[] keysp = StringUtils.split(key.toString(), ".");
            Object[] obj = httpRequest.getParameterMap().get(key);

            if (keysp.length == 1) {
                for (Object anObj : obj) {
                    baseMap.put(key.toString(), anObj.toString());
                }
            } else if (keysp.length == 2) {
                Map<String, String> map;
                if (paraMap.containsKey(keysp[0])) {
                    map = paraMap.get(keysp[0]);
                } else {
                    map = new HashMap<String, String>();
                    paraMap.put(keysp[0], map);
                }
                for (Object anObj : obj) {
                    map.put(keysp[1], anObj.toString());
                }
            } else {
                logger.info("can not deal the parameter of '" + key + "'");
            }

        }
        return paraMap;
    }

    //将request和response注入action类
    private void doContext(HttpServletRequest httpRequest, HttpServletResponse httpResponse, Object action) throws Exception {
        if (action instanceof ContextDriven) {
            action.getClass().getMethod("setRequest", HttpServletRequest.class).invoke(action, httpRequest);
            action.getClass().getMethod("setResponse", HttpServletResponse.class).invoke(action, httpResponse);
        }
    }
}
