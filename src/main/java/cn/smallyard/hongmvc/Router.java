package cn.smallyard.hongmvc;

import java.lang.reflect.Method;

public class Router {
    private Class<?> Clazz;
    private Method methods;

    public Class<?> getClazz() {
        return Clazz;
    }

    public void setClazz(Class<?> clazz) {
        this.Clazz = clazz;
    }

    public Method getMethods() {
        return methods;
    }

    public void setMethods(Method methods) {
        this.methods = methods;
    }
}
