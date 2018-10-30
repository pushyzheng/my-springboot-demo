package site.pushy.springboot;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Pushy
 * @date 2018/10/30 19:17
 * @blog https://pushy.site
 */
@ConfigurationProperties("example.service")
public class ExampleServiceProperties {

    private String prefix;

    private String suffix;

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }
}
