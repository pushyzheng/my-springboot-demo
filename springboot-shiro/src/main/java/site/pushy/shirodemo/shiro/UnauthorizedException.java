package site.pushy.shirodemo.shiro;

/**
 * @author Pushy
 * @since 2018/11/21 13:44
 */
public class UnauthorizedException extends RuntimeException {

    public UnauthorizedException(String message) {
        super(message);
    }
}
