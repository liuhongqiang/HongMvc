package cn.smallyard.router;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

public class Router {
    private Map<String, Package> routeMap = new HashMap<String, Package>();
    private Map<String, Result> globalResultsMap = new HashMap<String, Result>();

    public Router() {
        init();
    }

    private void init() {
        SAXBuilder builder = new SAXBuilder();
        try {
            Document doc = builder.build(new File(getClass().getClassLoader().getResource("mvc.xml").getPath()));
            Element rootEl = doc.getRootElement();
            //获得 global package
            initGlobalResult(rootEl);

            List<Element> packageEls = rootEl.getChildren("package");
            for (Element onePackageEl : packageEls) {
                Package onePackage = new Package();
                String packageName = onePackageEl.getAttributeValue("name");
                onePackage.setName(packageName);

                //获取所有action
                Map<String, Action> actionMap = new HashMap<String, Action>();
                List<Element> actionEls = onePackageEl.getChildren("action");
                for (Element oneActionEl : actionEls) {
                    Action oneAction = new Action();
                    String actionName = oneActionEl.getAttributeValue("name");
                    String actionClass = oneActionEl.getAttributeValue("class");
                    String actionMethod = oneActionEl.getAttributeValue("method");
                    oneAction.setName(actionName);
                    oneAction.setClazz(actionClass);
                    oneAction.setMethod(actionMethod);

                    //获取所有result
                    List<Element> resultEls = oneActionEl.getChildren("result");
                    Map<String, Result> resultMap = new HashMap<String, Result>();
                    for (Element oneResultEl : resultEls) {
                        Result oneResult = new Result();
                        String resultName = oneResultEl.getAttributeValue("name");
                        String resultType = oneResultEl.getAttributeValue("type");
                        String resultUrl = oneResultEl.getText();
                        oneResult.setName(resultName);
                        oneResult.setType(resultType);
                        oneResult.setUrl(resultUrl);
                        resultMap.put(resultName, oneResult);
                    }
                    oneAction.setResultMap(resultMap);
                    actionMap.put(actionName, oneAction);
                }
                onePackage.setActionMap(actionMap);
                routeMap.put(packageName, onePackage);
            }
        } catch (JDOMException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initGlobalResult(Element rootEl) {
        Element globalResultsEl = rootEl.getChild("global-results");
        //获取所有result
        List<Element> resultEls = globalResultsEl.getChildren("result");
        for (Element oneResultEl : resultEls) {
            Result oneResult = new Result();
            String resultName = oneResultEl.getAttributeValue("name");
            String resultType = oneResultEl.getAttributeValue("type");
            String resultUrl = oneResultEl.getText();
            oneResult.setName(resultName);
            oneResult.setType(resultType);
            oneResult.setUrl(resultUrl);
            globalResultsMap.put(resultName, oneResult);
        }
    }

    public Map<String, Package> getRouterMap() {
        return routeMap;
    }

    public Map<String, Result> getGlobalResultsMap() {
        return globalResultsMap;
    }

}
