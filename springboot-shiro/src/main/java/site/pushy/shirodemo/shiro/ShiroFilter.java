package site.pushy.shirodemo.shiro;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.BasicHttpAuthenticationFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Pushy
 * @since 2018/11/20 22:06
 */
public class ShiroFilter extends BasicHttpAuthenticationFilter {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    protected boolean isLoginAttempt(ServletRequest request, ServletResponse response) {
        HttpServletRequest req = (HttpServletRequest) request;
        String token = req.getHeader("token");
        return token != null;
    }

    @Override
    protected boolean executeLogin(ServletRequest request, ServletResponse response) throws AuthenticationException {
        HttpServletRequest req = (HttpServletRequest) request;
        String authorization = req.getHeader("token");

        JwtToken token = new JwtToken(authorization);
        // 提交给自定义realm进行登入，如果错误他会抛出异常并被捕获
        try {
            Subject subject = getSubject(request, response);
            subject.login(token);
        } catch (Exception e) {
            return false;
        }

        return true;
    }

    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
        if (isLoginAttempt(request, response)) {
            try {
                boolean result = executeLogin(request, response);
                if (!result) {
                    logger.error("Login error.");
                    return response401(request, response);
                }
                return true;
            } catch (Exception e) {
                logger.error("Login error => " + e.getMessage());
                return response401(request, response);
            }
        }
        logger.error("Token is empty.");
        return response401(request, response);
    }

    /**
     * 对跨域提供支持
     */
    @Override
    protected boolean preHandle(ServletRequest request, ServletResponse response) throws Exception {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        httpServletResponse.setHeader("Access-control-Allow-Origin", httpServletRequest.getHeader("Origin"));
        httpServletResponse.setHeader("Access-Control-Allow-Methods", "GET,POST,OPTIONS,PUT,DELETE");
        httpServletResponse.setHeader("Access-Control-Allow-Headers", httpServletRequest.getHeader("Access-Control-Request-Headers"));
        // 跨域时会首先发送一个option请求，这里我们给option请求直接返回正常状态
        if (httpServletRequest.getMethod().equals(RequestMethod.OPTIONS.name())) {
            httpServletResponse.setStatus(HttpStatus.OK.value());
            return false;
        }
        return super.preHandle(request, response);
    }

    private boolean response401(ServletRequest request, ServletResponse response) {
        try {
            HttpServletResponse resp = (HttpServletResponse) response;
            resp.sendRedirect("/401");
            resp.setStatus(HttpStatus.UNAUTHORIZED.value());
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        return false;
    }

}
