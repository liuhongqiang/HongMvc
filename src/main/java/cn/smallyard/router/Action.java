package cn.smallyard.router;

import java.util.Map;

public class Action {
    private String name;
    private String clazz;
    private String method;
    Map<String, Result> resultMap;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getClazz() {
        return clazz;
    }

    public void setClazz(String clazz) {
        this.clazz = clazz;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Map<String, Result> getResultMap() {
        return resultMap;
    }

    public void setResultMap(Map<String, Result> resultMap) {
        this.resultMap = resultMap;
    }
}
