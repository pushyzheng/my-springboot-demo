package site.pushy.shirodemo.shiro;

import com.alibaba.fastjson.JSON;
import com.sun.deploy.net.HttpResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.apache.shiro.authz.AuthorizationException;
import site.pushy.shirodemo.bean.BaseResponse;

/**
 * @author Pushy
 * @since 2018/11/21 13:39
 */
@RestControllerAdvice
public class AppWideExceptionHandler {

    /**
     * 捕捉AuthenticationException异常
     */
    @ExceptionHandler(AuthorizationException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public String authenticationException(AuthorizationException e) {
        BaseResponse response = new BaseResponse();
        response.setCode(HttpStatus.FORBIDDEN.value());
        response.setMessage(e.getMessage());
        response.setData(null);
        return JSON.toJSONString(response);
    }

}
