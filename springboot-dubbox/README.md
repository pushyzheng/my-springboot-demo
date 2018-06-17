# Dubbox & Zookeeper 部署及其简单Demo

## 1. 安装Zookeeper

首先在[Apache ZooKeeper Releases](http://mirrors.shu.edu.cn/apache/zookeeper/)里下载到压缩包文件，通过FileZilla上传到服务器上。然后解压分别解压这两个文件，并将解压后的文件夹移动到`/usr`的目录下：

```
$ tar -zxvf zookeeper-3.4.10.tar.gz
...
$ mv zookeeper-3.4.10 /usr
```

然后在`/usr/zookeeper-3.4.10`目录下创建`data`目录，

```
$ cd /usr/zookeeper-3.4.10
$ mkdir data
```

接下来进入到`conf`目录，重命名`zoo_sample.cfg`文件为`zoo.cfg`，打开该文件，并修改`/dataDir`一行为：

```
dataDir=/usr/zookeeper-3.4.10/data
```

保存之后进入到`/zookeeper-3.4.10/bin`文件夹下，运行`zookeeper`：

```
$ ./zkServer.sh start
ZooKeeper JMX enabled by default
Using config: /home/pushy/Downloads/zookeeper-3.4.9/bin/../conf/zoo.cfg
Starting zookeeper ... STARTED
$ ./zkServer.sh status
ZooKeeper JMX enabled by default
Using config: /home/pushy/Downloads/zookeeper-3.4.9/bin/../conf/zoo.cfg
Mode: standalone
```

如果出现以上的提示信息即代表启动成功。


## 2. 部署dobbo-admin

在`Github`将[dangdangdotcom/dubbox](https://github.com/dangdangdotcom/dubbox)克隆或者下载到本地，将`dubbo-admin`项目中的`pom.xml`的依赖文件通过[dubbo-admin-2.5.4在tomcat中部署失败的问题](https://blog.csdn.net/tjeagle/article/details/50021151)的方式修改，另外必须修改`com.alibaba`的版本号：

```
<dependency>
	<groupId>com.alibaba</groupId>
	<artifactId>dubbo</artifactId>
	<version>2.5.3</version>
	<exclusions>
		<exclusion>
			<groupId>org.springframework</groupId>
			<artifactId>spring</artifactId>
		</exclusion>
	</exclusions>
</dependency>
```

然后再项目的根目录进行打包：

```
// 该命令跳过测试环节，直接进行打包的操作
$ mvn package -Dmaven.skip.test=true
```

就会在`tartget`目录下生成`dubbo-admin-2.8.4.war`文件，然后我们将该文件上传到服务器上的`tomcat/webapp`下，然后启动`tomcat`：

```
$ ./start.sh
```

此时`dubbo-admin-2.8.4.war`会自动解压，并该该目录下生成`dubbo-admin-2.8.4`的文件夹，然后我们将`/webapps/ROOT`原来的所有文件删除，并将`dubbo-admin-2.8.4`的内容全部启动到`ROOT`文件夹下，这样就搭建好了`dobbo-admin`后台了。

这时，打开`http://IP:8080/`地址，会进行简单的HTTP认证，账号密码均为`root`，登录之后则会出现下面的后台界面：

![TIM截图20180617213400.png](https://i.loli.net/2018/06/17/5b26635a04b05.png)


## 3. 简单Demo

### 3.1 服务提供者

#### 依赖和配置

首先在`pom.xml`中添加相关的依赖，这里使用到了一个[spring-boot-starter-dubbo](https://github.com/teaey/spring-boot-starter-dubbo)，可以使用`spring-boot`的方式开发dubbo程序

```xml
<properties>
    <dubbo-spring-boot>1.0.0</dubbo-spring-boot>
</properties>
<dependency>
    <groupId>io.dubbo.springboot</groupId>
    <artifactId>spring-boot-starter-dubbo</artifactId>
    <version>${dubbo-spring-boot}</version>
</dependency>
```

然后在`application.properties`添加相关的`dubbo`配置：

```
spring.dubbo.application.name=provider
spring.dubbo.registry.address=zookeeper://192.168.145.128:2181
spring.dubbo.protocol.name=dubbo
spring.dubbo.protocol.port=20880
// service的包扫描
spring.dubbo.scan=org.spring.springboot.dubbo
```

#### 创建服务

同样我们先创建简单的一个`interface`：

```
public interface CityDubboService {

    City findCityByName(String cityName);
}
```

并有实现该接口的实现类，这里唯一需要注意的是，`@service`注解是来自`com.alibaba.dubbo.config.annotation`的，并不是之前的`org.springframework.stereotype`的`@Service`注解：

```
import com.alibaba.dubbo.config.annotation.Service;

@Service(version = "1.0.0")
public class CityDubboServiceImpl implements CityDubboService {

    public City findCityByName(String cityName) {
        return new City(1L,2L,cityName,"是我的故乡");
    }
}
```

然后启动该项目，准备给服务的调用者提供服务。


### 3.2 调用服务者

#### 依赖和配置

消费者和服务端有者相同的文件，但是需要在配置文件中将端口设置为不同于服务提供者的端口号：

```
## Dubbo 服务消费者配置
spring.dubbo.application.name=consumer
spring.dubbo.registry.address=zookeeper://192.168.145.128:2181
## 扫描调用服务组件的包
spring.dubbo.scan=org.spring.springboot
## 避免和 server 工程端口冲突
server.port=8081
```

#### 调用服务

创建一个`CityController`，使用`@Reference`注解进行自动转配`cityDubboService`接口，这样就可以调用服务提供者提供的`cityDubboService`接口的服务了：

```
@RestController
public class CityController {

    @Reference(version = "1.0.0")
    CityDubboService cityDubboService;

    @RequestMapping("/city")
    public String printCity() {
        String cityName="北京";
        City city = cityDubboService.findCityByName(cityName);
        return city.toString();
    }

}
```

启动消费者应用，同时可以在`dobbuo-admin`后台看到应用、服务的详细信息：

![TIM截图20180617220633.png](https://i.loli.net/2018/06/17/5b266b1b36cfe.png)

![TIM截图20180617220643.png](https://i.loli.net/2018/06/17/5b266b1b43b6d.png)