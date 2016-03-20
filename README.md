# hongmvc
A simple web mvc framework.

## usage

### 1. configure web.xml

``` xml
<filter>
        <filter-name>hongmvc</filter-name>
        <filter-class>cn.smallyard.hongmvc.DispatchFilter</filter-class>
</filter>
<filter-mapping>
        <filter-name>hongmvc</filter-name>
        <url-pattern>*</url-pattern>
</filter-mapping>
```

### 2. add hongmvc.properties to your root path

```
hongmvc.scan.packages = cn
```

### 3. write your controller

```java
public class IndexController {

    @RequestMapping("/")
    public String index() {
        return "index";
    }
}
```

