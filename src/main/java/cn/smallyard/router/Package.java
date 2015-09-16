package cn.smallyard.router;

import java.util.Map;

public class Package {
    private String name;
    private Map<String, Action> actionMap;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, Action> getActionMap() {
        return actionMap;
    }

    public void setActionMap(Map<String, Action> actionMap) {
        this.actionMap = actionMap;
    }

}
