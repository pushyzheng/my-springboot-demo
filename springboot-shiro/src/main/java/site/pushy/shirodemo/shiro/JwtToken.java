package site.pushy.shirodemo.shiro;

import org.apache.shiro.authc.AuthenticationToken;

/**
 * @author Pushy
 * @since 2018/11/20 21:11
 */
public class JwtToken implements AuthenticationToken {

    private String token;

    public JwtToken(String token) {
        this.token = token;
    }

    @Override
    public Object getPrincipal() {
        return token;
    }

    @Override
    public Object getCredentials() {
        return token;
    }
}
