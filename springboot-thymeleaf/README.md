## 1. 依赖和配置

添加maven依赖：

```xml
<dependency>
	<groupId>org.springframework.boot</groupId>
	<artifactId>spring-boot-starter-thymeleaf</artifactId>
</dependency>
```

在`application.properties`中禁用`thymeleaf`缓存

```
spring.thymeleaf.cache: false
```

## 2. 简单示例

创建一个Controller，这里不能使用`@RestController`注解修改该类，返回的`hello`值是`resources/templates`目录下`hello.html`的文件名，即模板视图名：

```java
@Controller
public class HelloController {

    @RequestMapping("")
    public String hello(Model model, @RequestParam(value = "name", required = false, defaultValue = "World") String name) {
        model.addAttribute("name", name);
        return "hello";
    }

}
```

创建`hello.html`：

```html
<!DOCTYPE html>
<!--声明Thymeleaf命名空间-->
<html lang="en" xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="UTF-8">
    <title>Title</title>
</head>
<body>
    <p th:text="'Hello, ' + ${name} + '!'" ></p>
</body>
</html>
```

## th标签语法

- `th:text`：文本替换

```
<span th:text="${use.name}"></span>
```

- `th:value`：属性赋值

```
<input th:value="${user.name}" id="name" />
```

- `th:utext`：支持HTML文本替换

```
<p th:utext="${htmlcontent}">conten</p>
```

- `th:href`：链接地址赋值：

```
<a th:href="@{/login}" th:unless=${session.user != null}>Login</a>
```

## 标准表达式语法

#### 变量

通过`${...}`方式获取变量的值：

```
// model.addAttribute("user", user);

<span th:text="${user.username}"></span>
```

或者通过`*{..}`：

```
<span th:text="*{user.username}"></span>
```

#### URL

通过`@{...}`方式处理URL的视图：

```html
<a th:href="@{/login}">登录</a>
```

#### 字符串拼接

注意，第二种方式的限制较多，只能包含变量表达式`${...}`，不能包含其他常量、条件表达式等。

```html
<p th:text="'Hello, ' + ${name} + '!'" ></p>
<!--简单形式-->
<p th:text="| Hello ${name} |"></p>
```

#### 循环

循环变量是`user`在循环体中可以通过表达式访问

```
<li th:each="user : ${userList}">
    <span th:text="${user.username}"></span>
</li>
```

#### 条件取值

- If/Unless

<a>标签只有在`th:if`中条件成立时才显示，`th:unless`于`th:if`恰好相反，只有表达式中的条件不成立，才会显示其内容

```
<a th:href="@{/login}" th:if="${session.currentUser  == null }">
    登录
</a>

<a th:href="@{/login}" th:unless="${session.currentUser  != null }">
    登录
</a>
```

- Switch

```
<span th:switch="${session.role}">
    <p th:case="'admin'">管理员</p>
    <p th:case="'user'">普通用户</p>
</span>
```

## Thymeleaf内嵌变量

Thymeleaf还提供了一系列Utility对象（内置于Context中），可以通过#直接访问

```
dates ： java.util.Date的功能方法类。
calendars : 类似#dates，面向java.util.Calendar
numbers : 格式化数字的功能方法类
strings : 字符串对象的功能类
objects: 对objects的功能类操作。
bools: 对布尔值求值的功能方法。
arrays：对数组的功能类方法。
lists: 对lists功能类方法
sets
maps
```

- 判断数组是否为空：

```
<div th:unless="${#lists.isEmpty(userList)}">
    userList不为空
</div>
```

- request对象获取当前路径：

```
<span th:text="${#httpServletRequest.getRequestURL()}"></span>
```

## 表单提交

前端

```
<form action="/login" method="post">
    <input type="text" placeholder="用户名" name="username" id="username" /> <br>
    <input type="password" placeholder="密码" name="password" id="password "/> <br>
    <button type="submit">登录</button>
</form>
```

后端controller中通过注入User对象，Spring将会自动将前端的值转换成User对象的：

```java
@PostMapping("/login")
public String login(User user) {
    request.getSession().setAttribute("currentUser", user);
    request.getSession().setAttribute("role", "admin");
    return "index";
}
```

参考资料：

[spring boot(四)：thymeleaf使用详解 - 纯洁的微笑 - 博客园](https://www.cnblogs.com/ityouknow/p/5833560.html)

[Spring Boot (三)：Thymeleaf 的使用](https://blog.csdn.net/qq_32923745/article/details/78257686)