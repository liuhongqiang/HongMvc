package cn.smallyard.hongmvc;


import cn.smallyard.hongmvc.exception.ConfNotFondException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.util.Properties;

import static org.apache.commons.logging.LogFactory.*;

public class Configuration {
    private static Log log = getLog(Configuration.class);
    public static final String SCAN_PACKAGES = "hongmvc.scan.packages";
    private Properties prop = new Properties();

    public Configuration() {
        try {
            prop.load(this.getClass().getClassLoader().getResourceAsStream("hongmvc.properties"));
        } catch (IOException e) {
            String msg = "hongmvc.properties not found";
            log.error(msg, new ConfNotFondException(msg));
        }
    }

    public String get(String key) {
        return prop.getProperty(key);
    }

    public void set(String key, String value) {
        prop.setProperty(key, value);
    }

}
