package cn.smallyard;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface ContextDriven {
    void setRequest(HttpServletRequest request);

    void setResponse(HttpServletResponse response);
}
