package site.pushy.websocketdemo.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Component
public class WebSocketPushHandler implements WebSocketHandler {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。
     * 若要实现服务端与单一客户端通信的话，可以使用Map来存放，其中Key可以为用户标识
     * @author Pushy
     */
    private static final ConcurrentHashMap<String, WebSocketSession> usersMap = new ConcurrentHashMap<>();

    // 用户进入系统监听
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String userId = (String) session.getAttributes().get("userId");
        logger.info(userId +"成功进入了系统。。。");
        usersMap.put(userId, session);
        sendMessagesToUsers(new TextMessage("** 欢迎您连接到我的webSocket服务器 ***"));
    }

    /**
     * 对H5 websocket的send方法进行处理，接受客户端主动发送的消息
     * @param session 接受的用户的session对象
     * @param message 客户端发送的TextMessage对象
     * @author Pushy
     */
    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        System.out.println(message);
    }

    // 后台错误信息处理方法
    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {

    }

    /**
     * 用户退出后的处理，当用户关闭连接后，将session关闭后，并将usersMap的session给remove掉
     * 这样用户就处于离线状态了，也不会占用系统资源
     * @author Pushy
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        if (session.isOpen()) {
            session.close();
        }
        String userId = (String) session.getAttributes().get("userId");
        logger.info(userId + "安全退出了系统");
        usersMap.remove(userId);
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

    /**
     * 给所有的用户发送消息
     */
    public void sendMessagesToUsers(TextMessage message) {
        for (Map.Entry<String,WebSocketSession> entry : usersMap.entrySet()) {
            String userId = entry.getKey();
            WebSocketSession session = entry.getValue();
            if (session.isOpen()) {
                try {
                    session.sendMessage(message);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public void sendMessageToUser(String userId, TextMessage message) {
        logger.info("sendMessageToUser -- userId -- " + userId);
        WebSocketSession user = usersMap.get(userId);
        try {
            // isOpen()在线就发送
            if (user.isOpen()) {
                user.sendMessage(message);
            } else {
                logger.info(userId + "用户不在线，存入缓存");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送消息给指定的用户
     * @param userId 发送目标用户的ID
     * @param content 发送消息的内容
     * @author Pushy
     */
    public void sendMessageToUser(String userId,String content) {
        WebSocketSession user = usersMap.get(userId);
        if (user != null) {
            try {
                // isOpen()在线就发送
                if (user.isOpen()) {
                    logger.info("用户在线，直接发送消息，并存入数据库");
                    user.sendMessage(new TextMessage(content));
                } else {
                    logger.info("用户不在线，存入缓存");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            logger.info("用户不在线，存入缓存");
        }
    }

}
