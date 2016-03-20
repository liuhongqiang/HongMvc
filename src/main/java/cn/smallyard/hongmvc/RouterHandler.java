package cn.smallyard.hongmvc;

import cn.smallyard.hongmvc.annotation.RequestMapping;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class RouterHandler {
    private static Log logger = LogFactory.getLog(RouterHandler.class);

    private Map<String, Router> routeMap = new HashMap<String, Router>();

    public RouterHandler(Configuration config) {
        Scanner scanner = new Scanner(config);
        Set<String> classes = scanner.scan();
        reflectAndAddRouteMap(classes);
        for (String aa : routeMap.keySet()) {
            System.out.println(aa);
        }
    }


    private void reflectAndAddRouteMap(Set<String> classes) {
        for (String className : classes) {
            try {
                Class aClass = Class.forName(className);
                // 类注解
                RequestMapping classAnnotation = (RequestMapping) aClass.getAnnotation(RequestMapping.class);
                String parentPath = getPath(classAnnotation);
                Method[] methods = aClass.getMethods();
                for (Method method : methods) {
                    RequestMapping methodAnnotation = method.getAnnotation(RequestMapping.class);
                    if (methodAnnotation != null) {
                        String subPath = getPath(methodAnnotation);
                        String path = (parentPath + "/" + subPath).replaceAll("/+", "/");
                        Router router = new Router();
                        router.setClazz(aClass);
                        router.setMethods(method);
                        routeMap.put(path, router);
                    }
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private String getPath(RequestMapping classAnnotation) {
        String parentPath = "";
        if (classAnnotation != null) {
            parentPath = classAnnotation.value().trim();
        }
        return parentPath;
    }

    public void route(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        String urlString = httpRequest.getServletPath();
        logger.debug(urlString);
        Router router = routeMap.get(urlString);
        if (router != null) {
            try {
                Object controller = router.getClazz().newInstance();
                Method method = router.getMethods();
                String retStr = method.invoke(controller).toString();
                if (retStr.startsWith("redirect:")) {
                    retStr = retStr.substring(9, retStr.length());
                    httpResponse.sendRedirect(retStr);
                } else {
                    httpRequest.getRequestDispatcher(retStr + ".jsp").forward(httpRequest, httpResponse);
                }
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        } else {
            chain.doFilter(request, response);
        }
    }
}
