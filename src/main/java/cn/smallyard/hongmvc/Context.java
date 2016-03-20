package cn.smallyard.hongmvc;

public class Context {
    private Configuration config = new Configuration();
    private RouterHandler routerHandler = new RouterHandler(config);

    public Configuration getConfig() {
        return config;
    }

    public RouterHandler getRouterHandler() {
        return routerHandler;
    }

    public static void main(String[] args) {
        new Context();
    }
}
