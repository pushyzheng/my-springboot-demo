# Websocket实现

[SpringBoot学习－（十三）SpringBoot中建立WebSocket连接(STOMP)](https://blog.csdn.net/qq_28988969/article/details/78113463)

[微信小程序websocket,后台SSM(SpringMVC+Spring+Mybatis)](https://blog.csdn.net/qq_28988969/article/details/76057789)

[WebSocket消息推送（群发和指定到个人）](https://www.cnblogs.com/zhang-bo/p/7844062.html)

[spring配置websocket并实现群发/单独发送消息](https://blog.csdn.net/u014520745/article/details/62046396)

websocket的介绍：

在HTTP1.1中进行了改进，使得有一个keep-alive，也就是说，在一个HTTP连接中，可以发送多个Request，接收多个Response

与HTTP的报文所有不同的是，在websocket的握手中多个两个报文参数

```
Upgrade: websocket
Connection: Upgrade
```

在websocket未出现之前，主要时通过如下的两种方式来达到相同的效果：

- ajax轮询：让浏览器隔几秒发送一次请求，询问服务器是否有新的消息

- long poll：长轮询，采用阻塞模型，发起一次请求之后等待客户端发送消息，返回完之后再重新建立连接。



## 1. 原生实现

通过原生的`javax`包内的websocket实现，我们只需要通过`@ServerEndpoint`注解，将注解的当前类定义成一个`websocket`服务端，注解的值将被用于监听用户连接的终端访问的URL，客户端通过该URL进行连接：

```java
@ServerEndpoint("/websocket/{userno}")
@Component
public class WebSocketTest {
    
    //与某个客户端的连接会话，需要通过它来给客户端发送数据
    private Session session;
    private String userno = "";
    // concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象
    private static ConcurrentHashMap<String, WebSocketTest> webSocketSet = new ConcurrentHashMap<String, WebSocketTest>();
    
    // 连接建立成功调用的方法
    @OnOpen
    public void onOpen(@PathParam(value = "userno") String param, Session session, EndpointConfig config) {
        userno = param;
        this.session = session;
        webSocketSet.put(param, this);//加入map中
    }
    
    // 连接关闭的方法
    @OnClose
    public void onClose() {
        if (!userno.equals("")) {
            webSocketSet.remove(userno);  //从set中删除
        }
    }
    
    // 接到客户端发送的消息调用的方法
    @OnMessage
    public void onMessage(String message, Session session) {
        System.out.println("来自客户端的消息:" + message);
    }
    
    // 给指定用户发消息
    private void sendToUser(String message,String userno) {
        try {
            if (webSocketSet.get(userno) != null) {
                webSocketSet.get(userno).sendMessage(message);
            } else {
                System.out.println("当前用户不在线");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
```

这样就相当于创建了一个`handler`，定义了一系列客户端行为触发的函数，然后需要在`config`包下创建一个`WebSocketConfig`配置类：

```java
@Configuration
public class WebSocketConfig {

    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }
}
```

这样就简单地通过原生的`websocket`来搭建一个简单的服务端。配置完成之后，客户端即可通过如下的方式进行连接：

```
websocket = new WebSocket("ws://localhost:8080/websocket/123);
```

这样在连接到服务器后触发`onOpen()`方法时，就会将该客户端发送的`userno`（123）以及对应的`Session`对象添加到`Map`对象当中。


## 2. 整合Spring实现：

### 2.1 配置类：

首先在`config`包下创建`WebSocketConfig`配置类，这与原生实现的配置有所不同，实现了`WebSocketConfigurer`接口，注册了一个handler `WebSocketPushHandler`,和一个用于握手的拦截器`WebSocketInterceptor`，并且配置了客户端连接的地址：`/webSocketServer`。

```java
@Configuration
@EnableWebSocket  // 开启websocket
public class WebSocketConfig implements WebSocketConfigurer {

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        // 将WebSocketPushHandler映射到 /webSocketServer
        registry.addHandler(WebSocketPushHandler(), "/webSocketServer")
                .addInterceptors(new WebSocketInterceptor());
    }

    // 声明WebSocketHandler bean
    @Bean
    public WebSocketHandler WebSocketPushHandler() {
        return new WebSocketPushHandler();
    }

}
```

### 2.2 添加拦截器

在`WebSocketInterceptor`拦截器中实现了`HandshakeInterceptor`接口。拦截器的作用主要是用于获取用户标识的记录，例如这里通过查询字符串的方式得到客户端发送的`token`，通过`JWT`解密之后拿到用户的`userId`：

```java
public class WebSocketInterceptor implements HandshakeInterceptor {

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
            /* 解密token，拿到用户的userId */
            String userId = "1faf0c33-0ead-415c-9bf1-4570e550e614";
            attributes.put("userId", token);
            logger.info(token + "连接到我了");
        }
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest arg0, ServerHttpResponse arg1, WebSocketHandler arg2, Exception arg3) {
        // TODO Auto-generated method stub

    }

}
```

另外在得到用户的`userId`后，我们需要将它保存在`WebSocketSession`的`attributes`对象当中，这样在后面的`handler`方法内，就可以通过下面的方式来得到当前`session`，也就是当前连接用户的`userId`

```
String userId = session.getAttributes().get("userId")
```

### 添加控制器

创建一个`WebSocketPushHandler`实现`WebSocketHandler`以下的方法：

- `afterConnectionEstablished`：连接建立成功之后，记录用户的连接标识，便于后面发信息

- `handleTextMessage`：可以对`H5 websocket`的`send`方法进行处理，接收客户端通过`websocket`主动发送的消息

- `handleTransportError`：连接出错处理，主要是关闭出错会话的连接，和删除在Map集合中的记录

- `afterConnectionClosed`：连接已关闭，移除在Map集合中的记录


```java
public class WebSocketPushHandler implements WebSocketHandler {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final ConcurrentHashMap<String, WebSocketSession> usersMap = new ConcurrentHashMap<>();

    
    /**
     * 在这个方法内来记录用户标识，从之前的WebSocketInterceptor拦截器添加的attributes中取出用户userId
     * 然后将该用户的id作为键，当前链接客户端的session作为值映射到usersMap当中
     * @author Pushy
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String userId = (String) session.getAttributes().get("userId");
        logger.info(userId +"成功进入了系统。。。");
        usersMap.put(userId, session);
        sendMessagesToUsers(new TextMessage("欢迎您连接到我的websocket服务器"));
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        TextMessage msg = (TextMessage)message.getPayload();
        logging.info("收到客户端消息 " + msg)
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
        // 迭代map取出所有的session发送消息
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

    /**
     * 发送消息给指定的用户
     */
    public void sendMessageToUser(String userId, TextMessage message) {
        // 通过userId从map中取出对应的session发送消息
        WebSocketSession user = usersMap.get(userId);
        try {
            // isOpen()在线就发送
            if (user.isOpen()) {
                user.sendMessage(message);
            } else {
                logger.info("用户不在线");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

```

## 3. 前端实现

如果采用集成Spring的方式，则通过如下的代码的来进行客户端的连接：

```
if ('WebSocket' in window) {
    let userno = "1faf0c33-0ead-415c-9bf1-4570e550e614";
    websocket = new WebSocket("ws://localhost:8080/webSocketServer?token="+userno);
}
else {
    alert('当前浏览器 Not support websocket')
}

//连接发生错误的回调方法
websocket.onerror = function () {
    setMessageInnerHTML("WebSocket连接发生错误");
};

//连接成功建立的回调方法
websocket.onopen = function () {
    setMessageInnerHTML("WebSocket连接成功");
}

//接收到消息的回调方法
websocket.onmessage = function (event) {
    setMessageInnerHTML(event.data);
}

//连接关闭的回调方法
websocket.onclose = function () {
    setMessageInnerHTML("WebSocket连接关闭");
}
```