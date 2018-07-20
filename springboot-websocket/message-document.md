## 1. 接收广播消息

### 客户端

```JavaScript
const subscription_broadcast = stompClient.subscribe('/topic/response',
(response) => {
  if (response.body) {
    printToScreen("【广播】" + response.body);
  } else {
    printToScreen("收到一个空消息");
  }
});
```

对应的服务端的controller代码为：

```java
@MessageMapping(value = "/chat")
@SendTo("/topic/response")
public String talk(@Payload String text, @Header("simpSessionId") String sessionId) {
    return "【" + sessionId + "】说:【" + text + "】";
}
```

`/chat`则是发送的地址，当客户端发送消息到该地址时，服务端将会将消息发送到`/topic/response`，只要客户端订阅了这条路径，不管是哪个用户，都会接收到消息：

```JavaScript
const headers = {};
const body = {
    'message': "Hello World"
};
stompClient.send("/app/chat", headers, JSON.stringify(body));
```

这里的`/app`前缀是在服务端中设置了客户端发送消息的前缀：

```java
@Override
public void configureMessageBroker(MessageBrokerRegistry registry) {
    registry.enableSimpleBroker("/topic", "/queue");
    /* 客户端发送过来的消息，需要以"/app"为前缀，再经过Broker转发给响应的Controller */
    registry.setApplicationDestinationPrefixes("/app");
}
```

## 2. 接收个人消息

这里的个人消息指的是服务器发送给客户端的个人消息，相当于客户端通过webSocket请求服务器，而服务器只把消息发送给请求的客户端，而不是通过广播推送给全部的用户。


### 服务端实现

```java
/**
 * 私人消息，通过@SendtoUser发送给请求消息的那个人
 * @param text
 * @param sessionId
 * @return
 */
@MessageMapping(value = "/personal")
@SendToUser(value = "/topic/personal/response", broadcast = false)
public String personalSocket(@Payload String text,
                             @Header("simpSessionId") String sessionId) {
    logger.info(String.format("【%s】 发布了一条私人消息 【%s】", sessionId, text));
    return String.format("这是一条只发给你【%s】的消息", sessionId);
}
```

这里的`/topic/personal/response`会被UserDestinationMessageHandler转换为`/user/sessionId/topic/personal/response`，为了避免发送给同一个用户的不同session（不同浏览器），可以通过`broadcast=false`避免推送到该用户的所有session当中，另外也可以改变客户端订阅的地址，通过具体的订阅地址，如`/user/jzbfppaj/topic/personal/response`。


### 客户端

订阅接收私人消息的地址：

```JavaScript
stompClient.subscribe('/user/topic/personal/response', response => {
    console.log(response.body);
    setMessageInnerHTML("/user/topic/personal/response 你接收到的消息为:" + response.body);
});
```

发送给服务器消息：

```JavaScript
const headers = {};
const body = {
    'message': 'Hello World'
};
stompClient.send("/app/personal", headers, JSON.stringify(body));
```

同样，发送的地址需要以`/app`为前缀。


## 3. 聊天消息

### 服务端

创建继承自DefaultHandshakeHandler一个类，在该类中对用户进行认证和将客户端session与用户绑定：

```
class CustomHandshakeHandler extends DefaultHandshakeHandler {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    protected Principal determineUser(ServerHttpRequest request,
                                      WebSocketHandler wsHandler,
                                      Map<String, Object> attributes) {
        /* 从客户端连接的查询字符串中获得客户端的表示 */
        String url = request.getURI().toString();
        String token = url.substring(url.indexOf("?") + 7, url.length());
        // Todo 解析token获得用户的userId作为Principal name
        logger.info(String.format("有一个客户端【%s】连接上了...", token));
        return new StompPrincipal(token);
    }
}
```

并且需要在registerStompEndpoints方法中注册这个HandshakeHandler：

```
registry.addEndpoint("/webSocketServer")
        .setAllowedOrigins("*")
        .setHandshakeHandler(new CustomHandshakeHandler())  // Set custom handshake handler
        .withSockJS();
```

在Controller中，`principal`参数则是在CustomHandshakeHandler中返回的StompPrincipal对象：

```java
/**
 * 发送聊天消息，通过convertAndSendToUser实现向指定用户发送消息
 * @param principal
 * @param text 客户端发送的body
 */
@MessageMapping("/chat")
public void specifiedSocket(Principal principal, @Payload String text) {
    logger.info("【%s】发了一条消息 %s", principal.getName(), text);
    JSONObject jsonObject = JSON.parseObject(text);
    String content = jsonObject.getString("message");
    String destUser = jsonObject.getString("destUser");
    /* 创建消息实体类 */
    ChatMsgEntity msg = new ChatMsgEntity();
    msg.setContent(content);
    msg.setDestUser(destUser);
    msg.setSender(principal.getName());
    String message = RespEntity.message(msg);
    messagingTemplate.convertAndSendToUser(principal.getName(), "/queue/chat", message);
}
```


### 客户端

首先需要修改连接的URL：

```
let userId = "5169dee39ac842f0a2ff79e24187c2ee";
let socket = new SockJS('http://localhost:8080/webSocketServer?token='+ userId);
```

然后进行订阅

```JavaScript
stompClient.subscribe('/user/queue/chat', response => {
    console.log(response.body);
    setMessageInnerHTML("【聊天消息】你接收到的消息为:" + response.body);
});
```

发送消息

```JavaScript
const body = {
    'message': message,
    'destUser':'42cf02385d324b8cab786e4aeb31cc55'
};
stompClient.send("/app/chat", headers, JSON.stringify(body));
```