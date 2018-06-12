package site.pushy.websocketdemo.interceptor;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import site.pushy.websocketdemo.SessionIdUtil;

/**
 * 此类用来获取登录用户信息并交由websocket管理
 */
public class MyWebSocketInterceptor implements HandshakeInterceptor {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * websocket连接第一次握手的拦截器，用来获得客户端提交的token信息解密出userId，并交由websocket（session）管理
     * @param attributes 该参数实际上就是在WebSocketHandler中通过session.getAttributes()得到的Map对象
     * @author Pushy
     */
    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse arg1, WebSocketHandler arg2,
                                   Map<String, Object> attributes) throws Exception {
        // 将ServerHttpRequest转换成request请求相关的类，用来获取request域中的用户信息
        if (request instanceof ServletServerHttpRequest) {
            ServletServerHttpRequest servletRequest = (ServletServerHttpRequest) request;
            HttpServletRequest httpRequest = servletRequest.getServletRequest();
            String token = httpRequest.getParameter("token");
            String userId;
            /* 解密token，拿到用户的userId */
            try {
                userId = SessionIdUtil.decodeWithoutCatchError(token);
            } catch (Exception error) {
                return false;
            }
            attributes.put("userId", userId);
            logger.info(userId + "连接到我了");
        }
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest arg0, ServerHttpResponse arg1, WebSocketHandler arg2, Exception arg3) {
        // TODO Auto-generated method stub

    }

}