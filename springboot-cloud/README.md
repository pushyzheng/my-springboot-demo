## 1. 实现注册中心服务器

### 1.1 依赖和配置

在[pom.xml](https://github.com/PushyZqin/my-springboot-demo/blob/master/springboot-cloud/eureka-server/pom.xml)中添加 `eureka-server` 的相关依赖：

```xml
<dependency>
	<groupId>org.springframework.cloud</groupId>
	<artifactId>spring-cloud-starter-netflix-eureka-server</artifactId>
</dependency>
```

然后在`application.yml`中添加配置：

```
server:
  port: 8081  # 服务监听的端口

eureka:
  instance:
    hostname: localhost
  client:
    # fetchRegistry、registerWithEureka 表示是个eureka server.
    registerWithEureka: false
    fetchRegistry: false
    serviceUrl:
      defaultZone: http://${eureka.instance.hostname}:${server.port}/eureka/
```

### 1.2 开启注册中心功能

在应用的入口文件中添加`@EnableEurekaServer`注解来开启注册中心服务器的功能：

```java
@SpringBootApplication
@EnableEurekaServer
public class EurekaServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(EurekaServerApplication.class, args);
	}
}
```


于是就可以在`localhost:8081`访问到注册中心的可视化界面了：

![TIM截图20180620213233.png](https://i.loli.net/2018/06/20/5b2a5780d79fd.png)


## 2. 服务提供者

### 2.1 依赖和配置

作为一个服务器的提供者，相对于注册中心来说为客户端，所以需要添在[pom.xml](https://github.com/PushyZqin/my-springboot-demo/blob/master/springboot-cloud/client/pom.xml)加`eureka`客户端的依赖：

```
<dependency>
	<groupId>org.springframework.cloud</groupId>
	<artifactId>spring-cloud-starter-eureka</artifactId>
	<version>1.4.4.RELEASE</version>
</dependency>
```

同时需要在`yml`配置文件中配置`eureka`服务器的地址：

```
eureka:
  # 客户端配置
  client:
    serviceUrl:
      defaultZone: http://localhost:8081/eureka/
server:
  port: 8762
spring:
  application:
    name: service-hi  # 该服务的名称
```

### 2.2 开启客户端和服务接口

注册一个路径为`/hi`的服务接口，并且通过`@EnableEurekaClient`注解实现客户端功能：

```java
@SpringBootApplication
@EnableEurekaClient // 表明自己是一个客户端
@RestController
public class ClientApplication {

	public static void main(String[] args) {
		SpringApplication.run(ClientApplication.class, args);
	}

	/**
	 * 注册一个服务接口
	 * @param name 接口传入的参数
	 * @return
	 */
	@RequestMapping("hi")
	public String home(String name) {
		return "Hello" + name;
	}
	
}
```

## 3. 服务消费者（rest+ribbon）

### 3.1 依赖和配置

在[pom.xml](https://github.com/PushyZqin/my-springboot-demo/blob/master/springboot-cloud/service-ribbon/pom.xml)中添加`ribbon`和`eureka`的依赖：

```xml
<dependency>
	<groupId>org.springframework.cloud</groupId>
	<artifactId>spring-cloud-starter-ribbon</artifactId>
	<version>1.4.4.RELEASE</version>
</dependency>
```

在配置文件添加相关的配置：

```
eureka:
  client:
    serviceUrl:
      defaultZone: http://localhost:8761/eureka/
server:
  port: 8083
spring:
  application:
    name: service-ribbon
```


### 3.2 调用接口

定义一个`HelloService`类，在该类中调用了`service-hi`应用中的方法：

```java
@Service
public class HelloService {

    @Autowired
    private RestTemplate restTemplate;

    /**
     * 在该方法中通过REST调用service-hi应用中/hi接口
     */
    public String hiService(String name) {
        return restTemplate
            .getForObject("http://SERVICE-HI/hi?name=" + name, String.class);
    }

}
```

## 4. 服务消费者（feign）

### 4.1 依赖和配置

在[pom.xml](https://github.com/PushyZqin/my-springboot-demo/blob/master/springboot-cloud/service-feign/pom.xml)文件中添加`feign`和`eureka`依赖：

```
<dependency>
	<groupId>org.springframework.cloud</groupId>
	<artifactId>spring-cloud-starter-eureka</artifactId>
	<version>1.4.4.RELEASE</version>
</dependency>
<dependency>
	<groupId>org.springframework.cloud</groupId>
	<artifactId>spring-cloud-starter-feign</artifactId>
	<version>1.4.4.RELEASE</version>
</dependency>
```

在`yml`中添加相关的配置：

```
eureka:
  client:
    serviceUrl:
      defaultZone: http://localhost:8761/eureka/
server:
  port: 8084
spring:
  application:
    name: service-feign
```

### 4.2 调用服务

通过`@ FeignClient（“服务名”）`，来指定调用哪个服务：

```java
@FeignClient(value = "service-hi")
public interface SchedualServiceHi {

    /**
     * 定义一个feign接口，通过@ FeignClient（“服务名”），来指定调用哪个服务。这里调用了service-hi服务的“/hi”接口
     * @param name
     * @return
     */
    @RequestMapping(value = "hi", method = RequestMethod.GET)
    String sayHiFromClientOne(@RequestParam(value = "name") String name);
}
```
