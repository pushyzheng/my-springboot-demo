package site.pushy.springboot;

/**
 * @author Pushy
 * @date 2018/10/30 19:16
 * @blog https://pushy.site
 */
public class ExampleService {

    private String prefix;
    private String suffix;

    public ExampleService(String prefix, String suffix) {
        this.prefix = prefix;
        this.suffix = suffix;
    }

    public String wrap(String word) {
        return prefix + word + suffix;
    }

}
