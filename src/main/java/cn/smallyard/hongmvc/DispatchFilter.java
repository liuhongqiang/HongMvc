package cn.smallyard.hongmvc;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class DispatchFilter implements Filter {

    private Context context;

    public void init(FilterConfig filterConfig) throws ServletException {
        context = new Context();
    }

    //filter入口  处理所有请求
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        context.getRouterHandler().route(request, response, chain);
    }

    public void destroy() {

    }

}
