package site.pushy.websocketdemo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import site.pushy.websocketdemo.handler.WebSocketPushHandler;
import site.pushy.websocketdemo.interceptor.MyWebSocketInterceptor;

@Configuration
@EnableWebSocket
public class WebSocketConfig extends WebMvcConfigurerAdapter implements WebSocketConfigurer {
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {

        registry.addHandler(WebSocketPushHandler(), "/webSocketServer")
                .addInterceptors(new MyWebSocketInterceptor())
                .setAllowedOrigins("*");
    }

    @Bean
    public WebSocketHandler WebSocketPushHandler() {
        return new WebSocketPushHandler();
    }

}