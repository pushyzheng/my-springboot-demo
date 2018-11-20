## 1. 所需依赖和配置

首先在`pom.xml`导入相关的依赖：

```xml
<dependency>
	<groupId>org.springframework.boot</groupId>
	<artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
<dependency>
	<groupId>mysql</groupId>
	<artifactId>mysql-connector-java</artifactId>
	<version>8.0.11</version>
</dependency>
``` 

然后在`application.properties`中配置数据源：

```
spring.datasource.url=jdbc:mysql://127.0.0.1/springboot
spring.datasource.username=root
spring.datasource.password=123456
spring.datasource.driver-class-name=com.mysql.jdbc.Driver

spring.jpa.properties.hibernate.hbm2ddl.auto=update
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL5InnoDBDialect
spring.jpa.show-sql= true  // 配置是否在控制台显示SQL语句
```

## 2. 准备实体类

创建一个简单的`JavaBean`的`User`实体类，并带上`@Entity`注解，表明这是一个实体类，并且用`@Id`和`Column`注解来映射数据库的主键和其他字段：

```java
package site.pushy.demo.entity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class User {

    @Id
    private String id;

    @Column(nullable = false, unique = true)
    private String userName;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private int age;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    ...
}

```

## 3. 实现自动化JPA Repository

### 3.1 基本CURD

在`repository`包下创建`UserRepository`接口，并且继承自`JpaRepository`，`JpaRepository`包含两个多态参数类型，第一个为实体类类型，第二个为`id`的数据类型：

```
public interface UserRepository extends JpaRepository<User,String> {
}
```

这样我们就可以使用`UserRepository`接口进行基本的CURD的操作了：

```java
@RunWith(SpringRunner.class)
@SpringBootTest
public class DemoApplicationTests {

	@Resource
	private UserRepository userRepository;

	@Test
	public void contextLoads() {
		User user = new User(
				UUID.randomUUID().toString(),
				"Pushy",
				"123",
				19
		);
        userRepository.save(user);
		User user1 = userRepository.findByUserName("Pushy");
		System.out.println(user.getAge());
	}

}
```


### 3.2 额外添加

`Spring Data`定义了一组小型的领域特定语言（domainspecific
language ，DSL），在这里，持久化的细节都是通过
`Repository`方法的签名来描述的。

例如我们想通过`userName`来进行查询实例，则可以在`UserRepository`额外添加该抽象方法

```java
public interface UserRepository extends JpaRepository<User,String> {

    User findByAge(int age);
}
```

该命名方式是通过，查询动词（除了`find`还可以使用`get`、`read`、、`count`） + 主题（`User`，大多数情况可以省略）+ 断言（`ByAge`）组成的。

在断言中，还可以使用这些方法：

```
IsAfter、After、IsGreaterThan、GreaterThan
IsGreaterThanEqual、GreaterThanEqual
IsBefore、Before、IsLessThan、LessThan
IsLessThanEqual、LessThanEqual
IsBetween、Between
IsNull、Null
IsNotNull、NotNull
IsIn、In
IsNotIn、NotIn
IsStartingWith、StartingWith、StartsWith
IsEndingWith、EndingWith、EndsWith
IsContaining、Containing、Contains
IsLike、Like
IsNotLike、NotLike
IsTrue、True
IsFalse、False
Is、Equals
IsNot、Not
```

### 3.3 自定义SQL

查

```java
@Query("select u from User u where u.name = ?1")
User getUserByName(String name);
```

改：如涉及到删除和修改在需要加上`@Modifying`和事务支持`@Transactional`：

```java
@Modifying
@Query("update User u set u.name = ?2 where u.id = ?1")
@Transactional
int updateUserNameById(String id, String name);
```

删：同样需要添加`@Modifying`和事务支持`@Transactional`的注解：

```java
@Modifying
@Transactional
@Query("delete from User where id = ?1")
void deleteByUserId(String id);
```

### 3.4 分页

```java
int offset = 1, count = 10;
Sort sort = new Sort(Sort.Direction.DESC, "id");
// Pageable 是spring封装的分页实现类，使用的时候需要传入页数、每页条数和排序规则
Pageable pageable = new PageRequest(offset, count, sort);
Page<Post> page = postRepository.findAll(pageable);
List<Post> posts = page
		.get()   // Stream class
		.collect(Collectors.toList());
System.out.println(posts);
```

## 4. 连表关联

首先在`@Entity`类中添加关联注解：

```java
@Entity
public class User {
    
    @Id
    private String id;
    
    @Column(nullable = false, unique = true)
    private String name;
    
    // 一对多关联
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = {CascadeType.ALL})
    private List<Post> posts;
    
}
```

```java
@Entity
public class Post {
    
    @Id
    private String id;
    
    @Column(nullable = false, unique = true)
    private String title;

    @Column(nullable = false, unique = true)
    private String content;
    
    // 多对一关联
    @ManyToOne(optional = false)
    private User user;
    
}
```

然后定义结果集接口，接收连表查询的结果集：

```java
public interface PostSummary {

    User getUser();

    String getId();

    String getTitle();

    String getContent();

}
```

通过`@Query`自定义SQL查询，Spring将会为该结果集创建代理对象：

```java
@Repository
public interface PostRepository extends JpaRepository<Post, String> {

    @Query("select p.id as id, p.title as title, u as user" +
            " from Post p left join User u on p.user = u.id where u.id = ?1")
    List<PostSummary> getPostByUser(String userId);

}
```

测试：

```java
@Test
public void leftJoinQuery() {
	List<PostSummary> posts = postRepository.getPostByUser("f9251e38-7f92-469a-8c04-7c8d2f9a7edc");
	for (PostSummary summary : posts) {
		System.out.println("post title： " + summary.getTitle());
		System.out.println("user name：" + summary.getUser().getName());
	}
}
```