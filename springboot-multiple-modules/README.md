## 1. 创建父工程

创建一个简单的`Maven`项目

![TIM截图20180808105440.png](https://i.loli.net/2018/08/08/5b6a5c0c12af0.png)

删除创建好的项目接口中的`src`目录，因为父工程不需要该目录：

![TIM截图20180808105741.png](https://i.loli.net/2018/08/08/5b6a5c3293f40.png)

修改`pom.xml`的配置文件，添加相关的`spring-boot`依赖：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>site.pushy</groupId>
    <artifactId>springboot-multiple-modules</artifactId>
    <version>1.0-SNAPSHOT</version>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>2.0.3.RELEASE</version>
        <relativePath/> <!-- lookup parent from repository -->
    </parent>

    <properties>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>
        <java.version>1.8</java.version>
        <spring-cloud.version>Finchley.RELEASE</spring-cloud.version>
    </properties>

    <dependencies>
        <!--spring-boot依赖-->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>


</project>
```

## 2. 创建子工程

在IDEA中进入Project Structure，点击加号创建一个新的子工程（New Module）

![TIM截图20180808110218.png](https://i.loli.net/2018/08/08/5b6a5d464d986.png)

这里我们创建6个子工程，这些子工程的含义分别为：

```
bean 存放公共的bean类
common 存放公共类/工具类
dao 存放公共的数据访问层类
service 存放公共的服务层类
service-impl 服务器层接口的实现类
web 存放controller类
```

![TIM截图20180808110745.png](https://i.loli.net/2018/08/08/5b6a628ae026a.png)

创建完子工程之后，我们需要将这些工程引入到父工程的`pom.xml`中：

```xml
...
<modules>
    <module>bean</module>
    <module>common</module>
    <module>dao</module>
    <module>service</module>
    <module>service-impl</module>
    <module>web</module>
</modules>
...
```

接下来就是为各个模块添加对其他模块的依赖。

### 2.1 bean

bean不依赖于任何模块，但是几乎被任何模块所依赖，但是我们还是需要修改bean模块的`pom.xml`文件，来指明该模块的父工程：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <!--指明父工程-->
    <parent>
        <groupId>site.pushy</groupId>
        <artifactId>springboot-multiple-modules</artifactId>
        <version>1.0-SNAPSHOT</version>
        <relativePath>../pom.xml</relativePath>
    </parent>

    <artifactId>bean</artifactId>
    <version>1.0-SNAPSHOT</version>
</project>
```

### 2.2 common

公共类common模块需要依赖bean模块，所有需要在`dependencies`添加对bean模块的依赖：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <!--同时公共类模块也需要指明父工程-->
    <parent>
        <groupId>site.pushy</groupId>
        <artifactId>springboot-multiple-modules</artifactId>
        <version>1.0-SNAPSHOT</version>
        <relativePath>../pom.xml</relativePath>
    </parent>

    <artifactId>common</artifactId>
    <version>1.0-SNAPSHOT</version>

    <dependencies>
        <!--添加对bean模块的依赖-->
        <dependency>
            <groupId>site.pushy</groupId>
            <artifactId>bean</artifactId>
            <version>1.0-SNAPSHOT</version>
        </dependency>
    </dependencies>

    
</project>
```

### 2.3 dao

数据类访问层dao模块，同样需要指明父工程，另外还需要依赖于bean模块和common模块：

```xml
<dependencies>
	<dependency>
		<groupId>site.pushy</groupId>
		<artifactId>bean</artifactId>
		<version>1.0-SNAPSHOT</version>
	</dependency>
	<dependency>
		<groupId>site.pushy</groupId>
		<artifactId>common</artifactId>
		<version>1.0-SNAPSHOT</version>
	</dependency>
</dependencies>
```

### 2.4 service

存放服务层类service模块需要依赖于dao、bean、common模块：

```xml
<dependencies>
	<dependency>
		<groupId>site.pushy</groupId>
		<artifactId>bean</artifactId>
		<version>1.0-SNAPSHOT</version>
	</dependency>
	<dependency>
		<groupId>site.pushy</groupId>
		<artifactId>common</artifactId>
		<version>1.0-SNAPSHOT</version>
	</dependency>
	<dependency>
		<groupId>site.pushy</groupId>
		<artifactId>dao</artifactId>
		<version>0.0.1-SNAPSHOT</version>
	</dependency>
</dependencies>
```

### 2.5 service-impl

存放服务层类的实现类的service-impl模块和service模块一样，但是另外还依赖于service模块：

```xml
<dependencies>
	<dependency>
		<groupId>site.pushy</groupId>
		<artifactId>bean</artifactId>
		<version>1.0-SNAPSHOT</version>
	</dependency>
	<dependency>
		<groupId>site.pushy</groupId>
		<artifactId>common</artifactId>
		<version>1.0-SNAPSHOT</version>
	</dependency>
	<dependency>
		<groupId>site.pushy</groupId>
		<artifactId>dao</artifactId>
		<version>0.0.1-SNAPSHOT</version>
	</dependency>
	<dependency>
		<groupId>site.pushy</groupId>
		<artifactId>service</artifactId>
		<version>0.0.1-SNAPSHOT</version>
	</dependency>
</dependencies>
```

### 2.6 web

web模块依赖于所有的模块：

```xml
<dependencies>
	<dependency>
		<groupId>site.pushy</groupId>
		<artifactId>bean</artifactId>
		<version>1.0-SNAPSHOT</version>
	</dependency>
	<dependency>
		<groupId>site.pushy</groupId>
		<artifactId>common</artifactId>
		<version>1.0-SNAPSHOT</version>
	</dependency>
	<dependency>
		<groupId>site.pushy</groupId>
		<artifactId>dao</artifactId>
		<version>0.0.1-SNAPSHOT</version>
	</dependency>
	<dependency>
		<groupId>site.pushy</groupId>
		<artifactId>service</artifactId>
		<version>0.0.1-SNAPSHOT</version>
	</dependency>
		<dependency>
		<groupId>site.pushy</groupId>
		<artifactId>service-impl</artifactId>
		<version>0.0.1-SNAPSHOT</version>
	</dependency>
</dependencies>
```

## 4. 配置

### 4.1 mybatis逆向工程

首先我们需要通过逆向工程让`mybatis-generator`自动为我们生成mapper类和xml映射文件，所以我们需要用到`generator-sqlmap-custom`工程，首先修改`mybatis-generator-config.xml`配置文件：

```xml
<!-- targetPackage指定javaBean实体类生成的位置，即多模块的bean包名 -->
<javaModelGenerator targetPackage="site.pushy.bean"
                    targetProject=".\src\main\java">
    <property name="enableSubPackages" value="true" />
    <property name="trimStrings" value="true" />
</javaModelGenerator>

<!-- targetPackage指定Dao接口生成的位置，mapper接口，也是多模块dao包名 -->
<javaClientGenerator type="XMLMAPPER"
                     targetPackage="site.pushy.dao"
                     targetProject=".\src\main\java">
    <property name="enableSubPackages" value="false" />
</javaClientGenerator>
```

然后将生成的TbUser/TbUserExample类移动到`site.pushy.bean`（bean模块包）内，TbUserMapper类移动到`site.pushy.dao`（dao模块包）内，`TbUserMapper.xml`文件移动到dao模块的`resource/mapper`文件夹下。


### 4.2 配置mybatis

首先需要在父工程的`pom.xml`中添加mybatis和mysql的依赖：

```xml
<!--druid连接池依赖-->
<dependency>
    <groupId>com.alibaba</groupId>
    <artifactId>druid</artifactId>
    <version>1.1.9</version>
</dependency>
<!--mybatis依赖-->
<dependency>
    <groupId>org.mybatis.spring.boot</groupId>
    <artifactId>mybatis-spring-boot-starter</artifactId>
    <version>1.3.1</version>
</dependency>
<!--mysql-connector 依赖-->
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
    <scope>runtime</scope>
</dependency>
```

然后我们需要修改`WebApplication`类（web模块的Application启动类），添加扫描类`@MapperScan`注解，另外在`@SpringBootApplication`添加`scanBasePackages`参数，修改扫描的包路径，统一扫描`site.pushy`下的组件。

```java
@SpringBootApplication(scanBasePackages = "site.pushy")
@MapperScan("site.pushy.dao")
public class DaoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DaoApplication.class, args);
	}
}

```

同时在dao模块的`resource`目录下的`application.yml`配置文件中指定`mapper.xml`的路径和数据库的链接信息

```
spring:
    datasource:
        name: test
        url: jdbc:mysql://localhost:3306/demo?useSSL=true
        username: root
        password: 123456
        type: com.alibaba.druid.pool.DruidDataSource
        driver-class-name: com.mysql.jdbc.Driver
mybatis:
  mapper-locations: classpath:mapper/*.xml
```

## 5. 测试

### service

我们在`site.pushy.service`包在创建`UserService`接口：

```java
public interface UserService {

    TbUser getUserById(String id);

}
```

然后再`site.pushy.service.impl`包内创建一个`UserService`接口的实现类：

```java
@Service
public class UserServiceImpl implements UserService {

    // 自动装配TbUserMapper组件
    @Resource
    private TbUserMapper userMapper;

    @Override
    public TbUser getUserById(String id) {
        return userMapper.selectByPrimaryKey(id);
    }
}
```

### controller

我们在`site.pushy.web`包下创建一个`UserController`：

```java
@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @RequestMapping("/users/{id}")
    public String getUserById(@PathVariable String id) {
        return RespEntity.success(userService.getUserById(id));
    }
}
```

访问`localhost:8080/users/1`就可以得到返回的JSON数据：

![TIM截图20180808160109.png](https://i.loli.net/2018/08/08/5b6aa353cb4f4.png)

## 6. 打包

首先数据在父工程的`pom.xml`文件中修改打包的方式为`pom`：

```xml
<groupId>site.pushy</groupId>
<artifactId>springboot-multiple-modules</artifactId>
<version>1.0-SNAPSHOT</version>
<!--指定为pom-->
<packaging>pom</packaging>
```

然后点开侧边栏的Maven Projects，可以看到`springboot-multiple-modules`包为根工程（父工程），

![TIM截图20180808160343.png](https://i.loli.net/2018/08/08/5b6aa3e98653d.png)