package cn.smallyard.hongmvc;

import cn.smallyard.hongmvc.exception.ConfErrorException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

public class Scanner {
    private static Log logger = LogFactory.getLog(Scanner.class);

    private Configuration config;

    public Scanner(Configuration config) {
        this.config = config;
    }

    public Set<String> scan() {
        Set<String> classes = new HashSet<String>();
        String scanPackages = config.get(Configuration.SCAN_PACKAGES);
        if (scanPackages == null) {
            scanPackages = "";
        }
        String[] packages = scanPackages.split(",");

        for (String pkg : packages) {
            String path = "/" + pkg.replace(".", "/");
            URL url = this.getClass().getResource(path);
            if (url == null) {
                String msg = pkg + " not found or error";
                logger.error(msg, new ConfErrorException(msg));
            } else {
                findAndAdd(pkg, classes, new File(url.getPath()));
            }
        }
        return classes;
    }

    private void findAndAdd(String packageStr, Set<String> classes, File parentFile) {
        File[] files = parentFile.listFiles();
        if (files != null) {
            for (File file : files) {
                String fileName = file.getName();
                if (fileName.endsWith(".class")) {
                    String className = packageStr + "." + fileName.substring(0, fileName.length() - 6);
                    classes.add(className);
                } else if (file.isDirectory()) {
                    if (StringUtils.isNotEmpty(packageStr)) {
                        findAndAdd(packageStr + "." + fileName, classes, file);
                    } else {
                        findAndAdd(fileName, classes, file);
                    }

                }
            }
        }
    }
}
