package site.pushy.websocketsdemo.controller;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;
import site.pushy.websocketsdemo.entity.ClientMessage;
import site.pushy.websocketsdemo.entity.ServerMessage;


@Controller
public class WebSocketController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 接受客户端发送的消息
     */
    @MessageMapping("/sendTest")
    @SendTo("/topic/subscribeTest")
    public ServerMessage sendDemo(ClientMessage message) {
        logger.info("接收到了信息" + message.getName());
        return new ServerMessage("你发送的消息为:" + message.getName());
    }

    /**
     * 接收客户端发送的订阅
     */
    @SubscribeMapping("/subscribeTest")
    public ServerMessage sub() {
        logger.info("XXX用户订阅了我。。。");
        return new ServerMessage("感谢你订阅了我。。。");
    }


}
