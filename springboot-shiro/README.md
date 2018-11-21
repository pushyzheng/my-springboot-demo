---
title: Spring Boot集成Shiro打造强大RESTful认证授权系统
date: 2018-11-21 19:15:44
categories: Spring
tags:
  - Shiro
  - Spring
---

> 代码上传到[Github](https://github.com/PushyZqin/my-springboot-demo/tree/master/springboot-shiro)，推荐大家结合代码看该博文



# 1. 介绍

首先在使用Shiro框架之前，来了解一下Shiro的内部组成以及各个组件。首先是内部组成：

![](http://wiki.jikexueyuan.com/project/shiro/images/1.png)

我们针对部分的组成来看：

- `Authentication`：身份认证/登录，验证用户是否被允许登录。
- `Authorization`：授权，即权限验证。验证某个已经认证的用户是否拥有某个角色/权限。
- `Caching`：缓存，比如在用户登录之后可以缓存认证的信息，提高认证的效率。

其次，我们再来看看Shiro中的各个组件：

![](https://i.loli.net/2018/11/21/5bf540925c29b.png)

从图中可以看到各个组件的关系，即：

- `SecurityManager`：安全管理器，即所有的安全有关的操作都会与`SecurityManager`交互；且它管理者所有`Subject`。它是Shiro的核心，相当于`SpringMVC`中的`DispatcherServlet `前端控制器。
- `Subject`：主体，代表了当前的“用户”，所有 `Subject ` 都绑定到 `SecurityManager`。
- `Realm`：Shiro从`Realm`中获取安全数据（如用户、角色、权限），就是说`SecurityManager`如果要验证用户的身份或者角色/权限，那么它需要从`Realm`中获取相应用户进行校验。可以把`Realm`看成是`DataSource`，即安全数据源。

需要注意的是，Shiro并不提供维护用户的的角色/权限的控制，需要让开发者在`Realm`实现，接下来，我们就通过Shiro集成Spring Boot来搭建一个强大的权限认证系统！

# 2. 准备工作

## 2.1 JWT

因为我们要实现的`RESTful`风格的认证系统，采用的是当下最流行的前后端分离的Token认证。因此，就需要用JWT相关的库。首先，我们添加相关的Maven依赖：

```xml
<!--shiro集成Spring库-->
<dependency>
	<groupId>org.apache.shiro</groupId>
	<artifactId>shiro-spring</artifactId>
	<version>1.4.0</version>
</dependency>
<!--jjwt 用于解码和编码Token-->
<dependency>
	<groupId>io.jsonwebtoken</groupId>
	<artifactId>jjwt</artifactId>
	<version>0.9.1</version>
</dependency>
```

并且准备一个`JWTUtil`工具类，用来生成和解密Token：

```java
public class JWTUtil {

    private static final String SECRET_KEY = "f9251e38-7f92-469a-8c04-7c8d2f9a7edc";

    public static String encode(String userId) {
        Integer ept = 10080;  // 一周
        return JWTUtil.encode(userId, ept);
    }

    // 加密Token
    public static String encode(String userId, Integer exceptionTime) {
        Map<String, Object> claims = new HashMap<>();
        long nowMillis = System.currentTimeMillis();
        long expirationMillis = nowMillis + exceptionTime * 60000L;
        claims.put("userId", userId);
        return Jwts.builder()
                .setSubject("subValue")
                .addClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(new Date(expirationMillis))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY).compact();
    }

    // 解密Token
    public static String decode(String accessToken) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(SECRET_KEY)
                    .parseClaimsJws(accessToken).getBody();
            return (String) claims.get("userId");
        } catch (Exception e) {  // 解密失败，返回null
            return null;
        }
    }
}
```

## 2.2 数据源

准备用户和角色的实体类，这里用的持久化框架是JPA。这里的`Role`表的设计因为简化存在一些问题，即用户-角色应该是多对多关系，这里为了方便，只做了一对多的映射关系：

```java
@Data
@Entity
public class User {
    
    @Id
    private String id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private String password;

    // 一对多映射
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = {CascadeType.ALL})
    private List<Role> roles;  // 用户的角色列表
    
}
```

```java
@Data
@Entity
public class Role {
    
    @Id
    private String id;

    @Column(nullable = false, unique = true)
    private String name;

    // 多对一映射
    @ManyToOne(optional = false)
    private User user;
    
}
```

提供`UserService`接口，可以通过用户的`id`查询出用户对象以及他所关联的所有角色列表，这将提供给之后的`Realm`安全数据源里：

```java
@Service
public class UserServiceImpl implements UserService {

    @Resource
    private UserRepository userRepository;
    @Resource
    private RoleRepository roleRepository;

    /**
     * 通过userId查询出对应的用户对象
     */
    @Override
    public User getUserById(String id) {
        return userRepository.findById(id).orElse(null);
    }

    /**
     * 通过userId查询出该用户对象的所有的角色列表
     */
    @Override
    public List<Role> listRoleByUserId(String id) {
        return roleRepository.findByUserId(id);
    }

}
```

准备好了数据源和工具类， 下面我们开始关键的Shiro的配置。

# 3. Shiro配置

## 3.1 身份令牌

身份验证，**即在应用中谁能证明他就是他的本人**。在我们搭建的`RESTful`认证系统当中，身份验证标识当然是根据用户`id`生成的Token了。我们定义`JwtToken`，让它实现`AuthenticationToken`接口：

```java
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
```

`AuthenticationToken`接口定义了两个方法，`getPrincipal()`用于获得主体的标识属性，可以是用户名等；`getCredentials()`方法用于获得证明/凭证，如密码、证书等。

```java
package org.apache.shiro.authc;

public interface AuthenticationToken extends Serializable {
    
    Object getPrincipal();

    Object getCredentials();
}
```

在这里，我们让通过用户`id`生成的Token既当做是标识属性，也当做是证明凭证。另外，`Shiro`有一个默认实现的`UsernamePasswordToken`，它将用户名当做是标识属性，密码当做是凭证。

## 3.2 Realm

前面说到，`Realm`用来提供安全数据源，**即校验用户身份验证是否通过，以及提供该用户相应的角色和权限的数据**。注意，校验用户是否有正确的权限不是有`Realm`来处理，`Realm`是负责提供数据，而交给`SecurityManager`来处理。

我们让自定义的`MyRealm`继承自`AuthorizingRealm`类，并重写`doGetAuthenticationInfo`和`doGetAuthorizationInfo`方法。需要注意的是必须重写`supports`方法，该方法的返回值（True、False）表明是否支持处理传入的`authenticationToken`类型的令牌。

```java
@Component
public class MyRealm extends AuthorizingRealm {

    @Resource
    private UserService userService;

    @Override
    public String getName() {
        return "myRealm";
    }

    @Override
    public boolean supports(AuthenticationToken authenticationToken) {
        // 只支持JwtToken令牌类型
        return authenticationToken instanceof JwtToken;
    }

    /**
     *  默认使用此方法进行用户名正确与否验证，错误抛出异常即可。
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken auth) throws AuthenticationException {
        String token = (String) auth.getCredentials();
        String id = JWTUtil.decode(token);  // 解密Token
        if (id == null) {
            // Token解密失败，抛出异常
            throw new AuthenticationException("Invalid token.");
        }
        // Token解密成功，返回SimpleAuthenticationInfo对象
        return new SimpleAuthenticationInfo(token, token, "myRealm");
    }

    /**
     * 只有当需要检测用户权限的时候才会调用此方法，例如checkRole,checkPermission之类的
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        // 从principals中拿到Token令牌
        String id = JWTUtil.decode(principals.toString());
        User user = userService.getUserById(id);

        if (user != null) {
            SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();
            // 获取当前用户的所有角色，并且通过addRole添加到simpleAuthorizationInfo当中
            // 这样当Shiro内部检查用户是否有某项权限时就会从SimpleAuthorizationInfo中拿取校验
            List<Role> roles = userService.listRoleByUserId(user.getId());
            for (Role role : roles) {
                simpleAuthorizationInfo.addRole(role.getName());
            }
            return simpleAuthorizationInfo;
        }
        return null;
    }
}
```

`AuthorizingRealm `将获取`Subject`相关信息分成了两步：

- `doGetAuthenticationInfo `：获取身份验证相关信息，在该方法内校验传入的令牌是否有效。
- `doGetAuthorizationInfo`：获取授权（角色、权限）信息，在该方法内向`SimpleAuthorizationInfo`存入添加用户的角色或者权限数据。

## 3.3 Filter

另外，我们来需要通过滤器来实现Shiro的认证的流程。我们让定义的过滤器`ShiroFilter`类继承自`BasicHttpAuthenticationFilter`，并且重写主要的认证的方法。

`ShiroFilter`中各个方法调用的逻辑和顺序如下图：

![](https://i.loli.net/2018/11/21/5bf54f2a0ef3e.png)

```java
public class ShiroFilter extends BasicHttpAuthenticationFilter {
    
    // 判断Token头是否为空
    @Override
    protected boolean isLoginAttempt(ServletRequest request, ServletResponse response) {
        HttpServletRequest req = (HttpServletRequest) request;
        return req.getHeader("token") != null;
    }
    
    /**
     * 首先调用的一个方法，在该方法内进行主要的认证逻辑处理，如判断Token头是否为空，解密Token等
     */
    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
        if (isLoginAttempt(request, response)) {
            boolean result = executeLogin(request, response);  // 执行登录
            if (!result) { // 登录失败，返回401错误
                return abort401(request, response);
            }
            return true;
        }
        // Token为空
        return abort401(request, response);
    }
    
    /**
     * 调用Realm执行登录，并返回登录认证的结果
     */
    @Override
    protected boolean executeLogin(ServletRequest request, ServletResponse response) throws AuthenticationException {
        HttpServletRequest req = (HttpServletRequest) request;
        String authorization = req.getHeader("token");

        JwtToken token = new JwtToken(authorization);
        // 当调用Subject对象的login方法时，将会交给我们自己实现的MyRealm来处理登录的认证逻辑
        try {
            Subject subject = getSubject(request, response);
            subject.login(token);
        } catch (Exception e) {
            return false; // 登录失败
        }
        return true;  // 登录成功
    }
    
    /**
     * 返回HTTP 401错误
     */
    private boolean abort401(ServletRequest request, ServletResponse response) {
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
```

在`executeLogin`方法中，我们通过`getSubject()`方法得到`Subject`对象，并调用其`login()`方法。Shiro的内部将会去调用`MyRealm`类进行身份令牌的认证。

## 3.2 集成Spring

创建`ShiroConfig`配置类，

```java
@Configuration
public class ShiroConfig {

    /**
     * 生成DefaultWebSecurityManager bean，并设置我们自定义的Realm
     * @param myRealm Spring将会自动注入MyRealm类，因为我们给它加了@Component注解
     */
    @Bean
    public DefaultWebSecurityManager securityManager(MyRealm myRealm) {
        DefaultWebSecurityManager manager = new DefaultWebSecurityManager();
        manager.setRealm(myRealm);
        return manager;
    }

    /**
     * 注入ShiroFilter，配置过滤的URL规则
     */
    @Bean
    public ShiroFilterFactoryBean shiroFilterFactoryBean(SecurityManager securityManager) {
        ShiroFilterFactoryBean factoryBean = new ShiroFilterFactoryBean();

        // 添加ShiroFilter过滤器且命名为jwt
        Map<String, Filter> filterMap = new HashMap<>();
        filterMap.put("jwt", new ShiroFilter());
        factoryBean.setFilters(filterMap);

        factoryBean.setSecurityManager(securityManager);
        factoryBean.setUnauthorizedUrl("/401");  // 设置认证失败的路径

        Map<String, String> filterRuleMap = new HashMap<>();
        // 所有的请求通过ShiroFilter执行处理
        filterRuleMap.put("/**", "jwt");
        // 排除401路径，ShiroFilter将不做过滤的操作
        filterRuleMap.put("/login", "anon");
        filterRuleMap.put("/401", "anon");
        factoryBean.setFilterChainDefinitionMap(filterRuleMap);

        return factoryBean;
    }
}
```

如果你想使用Shiro的注解功能，还需要额外添加`@Bean`配置：

```java
@Bean
@DependsOn("lifecycleBeanPostProcessor")
public DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator() {
    DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator = new DefaultAdvisorAutoProxyCreator();
    defaultAdvisorAutoProxyCreator.setProxyTargetClass(true);
    return defaultAdvisorAutoProxyCreator;
}

@Bean
public LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
    return new LifecycleBeanPostProcessor();
}

@Bean
public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(DefaultWebSecurityManager securityManager) {
    AuthorizationAttributeSourceAdvisor advisor = new AuthorizationAttributeSourceAdvisor();
    advisor.setSecurityManager(securityManager);
    return advisor;
}
```

OK！到这里Shiro的配置已经完成，下面我们简单地介绍一下使用方式。

# 4. 使用

## 4.1 基本认证

我们定义三个接口，`/login`是不做过滤，用来返回加密后Token；`/users`做了拦截，需要正确令牌才能进行访问：

```java
@RestController
public class AuthController {

    // 生成Token
    @RequestMapping("/login")
    public String login() {
        return JWTUtil.encode("123");
    }

    @RequestMapping("/users")
    public String users() {
        return "ok";
    }
}
```

我们将调用`/login`接口获取的Token拿来访问`/users`接口能正确返回结果：

![](https://i.loli.net/2018/11/21/5bf5551ec787a.png)

如果Token为空或者无效，则会返回401错误：

![](https://i.loli.net/2018/11/21/5bf5559863180.png)

## 4.2 角色&权限控制

定义`/admin`接口，并通过`@RequiresRoles`注解设置只能用户具有`ROLE_ADMIN`角色才能访问，另外来可以通过`@RequiresPermissions`来细分设置具有哪些权限可以访问，但是必须在`MyRealm`中向`simpleAuthorizationInfo`对象中通过`addStringPermission()`方法添加用户的所有权限：

```java
@RestController
public class AuthController {

    @RequestMapping("/admin")
    @RequiresRoles(value = {"ROLE_ADMIN"}, logical = Logical.AND)
    public String admin() {
        return "admin";
    }
}
```

添加数据库数据，可以看到用户A当前的角色为`ROLE_USER`：

```java
// user表数据
+-----+-------+----------+
| id  | name  | password |
+-----+-------+----------+
| 123 | A     | 123      |
| 456 | B     | 456      |
+-----+-------+----------+

// role表数据
+----+------------+---------+
| id | name       | user_id |
+----+------------+---------+
| 1  | ROLE_ADMIN | 456     |
| 2  | ROLE_USER  | 123     |
+----+------------+---------+
```

如果此时我们使用用户A的令牌访问`/admin`接口，虽然可以通过认证，但是却授权失败。因为我们设置了该接口必须具有`ROLE_ADMIN`角色才能访问：

![](https://i.loli.net/2018/11/21/5bf55840de4ec.png)

另外，返回的结果不是我们想要的效果，如果我们想要自定义返回的数据信息，可以通过`@ControllerAdvice`全局捕捉授权失败时程序内抛出的异常：

```java
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
```

现在，返回的错误里显示了授权失败的错误信息了：

![](https://i.loli.net/2018/11/21/5bf559259d886.png)

如果我们修改`role`表，给予用户A`ROLE_ADMIN`的角色：

```
+----+------------+---------+
| id | name       | user_id |
+----+------------+---------+
| 1  | ROLE_ADMIN | 123     |
| 2  | ROLE_USER  | 123     |
+----+------------+---------+
```

那么，使用用户A的令牌将能成功地访问`/admin`接口：

![](https://i.loli.net/2018/11/21/5bf559b0e1dfb.png)


<br><br>

> 参考资料
> 
> [跟我学 Shiro](http://wiki.jikexueyuan.com/project/shiro/)
>
> [Shiro+JWT+Spring Boot Restful简易教程](https://juejin.im/post/59f1b2766fb9a0450e755993#heading-19)