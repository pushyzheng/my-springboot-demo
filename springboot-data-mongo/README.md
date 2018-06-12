# Spring Data Mongodb

## 1. 所需依赖和配置

首先在`pom.xml`导入相关的依赖

```xml
<dependency>
	<groupId>org.springframework.boot</groupId>
	<artifactId>spring-boot-starter-data-mongodb</artifactId>
</dependency>
```

在`config`包在创建`MongoConfig`配置类，通过`EnableMongoRepositories`的注解启动`Spring Data`的自动化`JPA Repository`生成的功能，并通过`basePackages`指定存放`Repository`的包路径：

```java
@Configuration
@EnableMongoRepositories(basePackages = "com.pushy.mongodbdemo.repository")
public class MongoConfig extends AbstractMongoConfiguration {

    @Override
    public MongoClient mongoClient() {  // 创建一个mongo客户端
        return new MongoClient();
    }

    @Override
    protected String getDatabaseName() {  // 指定数据库的名称
        return "OrdersDB";
    }

}
```

在这里，虽然没有直接声明`MongoTemplate Bean`，但是它会被隐式地创建。

**如果需要通过应用连接需要认证的MongoDB服务器**，则可以通过下面进行配置：

```java
@Autowired
private Environment env;

@Override
public MongoClient mongoClient() {
    MongoCredential mongoCredential = MongoCredential.createMongoCRCredential(
        env.getProperty("mongo.username"),  // 用户们
            "OrdersDB", // 数据库
        env.getProperty("mongo.password").toCharArray()); // 密码

    return new MongoClient(
            new ServerAddress("localhost",37017), // host和port
            Arrays.asList(mongoCredential)
    );
}
```

## 2. 创建模型

如下，创建了一个`Item`的`JavaBean`，并通过`@Document`注解将Java类型映射为一个文档：

```java
@Document // 指定这是一个文档（表名为实体类）
public class Order {

    @Id   // 指定文档的ID
    private String id;

    @Field("client") // 覆盖默认的域名
    private String customer;

    private String type;

    private Collection<Item> items = new LinkedHashSet<>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    ...
}
```

并且，这里还通过`@Field`注解了一个属性，当文档持久化的时候，`customer`属性将会映射为名为`client`的域。

另外，`items`属性还是一个`Item`类的集合对象。但是`Item`类除了是一个`JavaBean`外，并没有添加任何的注解。也就是说并不会将`Item`类单独持久化为文档，它只会作为`Order`类的内嵌元素。

```java
public class Item {

    private Long id;
    private Order order;
    private String product;
    private double price;
    private int quantiry;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    ...
}
```

## 3. 编写Repositiry

### 3.1 自动生成方法

在`repositiry`，定义一个接口，并继承自`MongoRepository`：

```
public interface OrderRepository 
         extends MongoRepository<Order,String> {}
```

`MongoRepository`接口有两个参数，第一个是带有`@Document`注
解的对象类型，也就是该`Repository`要处理的类型。第二个参数是带
有`@Id`注解的属性类型。这样`OrderRepository`就拥有了对`Order`文档基本的CRUD操作的方法了。

```
@RunWith(SpringRunner.class)
@SpringBootTest
public class MongodbdemoApplicationTests {

	@Resource
	private OrderRepository orderRepository;

	@Test
	public void contextLoads() {
		Order order = new Order();
		order.setId(UUID.randomUUID().toString());
		order.setType("1");
		order.setCustomer("Pushy");
		
		orderRepository.save(order);
		orderRepository.deleteById("1");
		orderRepository.count();
		orderRepository.findAll();
	}
}
```

`orderRepository`接口还具备了一下的增删改查的方法：

```
long count(); 返回指定Repository类型的文档数量

void delete(Iterable<? extends T); 删除与指定对象关联的所有文档

void delete(T); 删除与指定对象关联的文档

void delete(ID); 根据ID删除某一个文档

void deleteAll(); 删除指定Repository类型的所有文档

boolean exists(Object); 如果存在与指定对象相关联的文档，则返回true

boolean exists(ID); 如果存在指定ID的文档，则返回true <br>

List<T> findAll(); 返回指定Repository类型的所有文档 <br>

List<T> findAll(Iterable<ID>); 返回指定文档ID对应的所有文档为指定的Repository类型，返回分页且排序的文档列表

List<T> findAll(Sort);为指定的Repository类型，返回排序后的所有文档列表 <br>

T findOne(ID); 为指定的ID返回单个文档 <br>

Save( terable<s>) ; 保存指定Iterable中的所有文档 <br>

save ( < S > ); 为给定的对象保存一条文档 <br>
```


### 3.2 自定义查询方法


我们可以直接在`OrderRepository`中添加自定义的方法：

```java
public interface OrderRepository extends MongoRepository<Order,String> {

    // 根据customer属性查找
    List<Order> findByCustomer(String c);

    // 根据customer相似值查找
    List<Order> findByCustomerLike(String c);

    // 根据customer和type并行查找
    List<Order> findByCustomerAndType(String c, String t);

}
```

于是就可以使用`findByCustomer()`方法进行查询了：

```
	@Test
	public void contextLoads() {
		System.out.println(orderRepository.findByCustomer("Pushy"));
	}
```

## 4. 使用MongoTemplate

### 4.1 基本使用

- 插入操作

```java
Order order = new Order();
order.setId(UUID.randomUUID().toString())
mongoTemplate.save(order)
```


- 查询操作

```java
String orderId = "f87d5598-5332-4356-9721-930c728794a5";
Query query = new Query(Criteria.where("_id").is(orderId));
// 根据customer字段查询
// Query query = new Query(Criteria.where("customer").is("Jacking"));
Query query = new Query(Criteria.where("_id").is(orderId));
Order order = mongoTemplate.findOne(query, Order.class);
```

- 更新操作

先通过`Query`对象查询出需要修改的对象，然后通过`update("key","new value")`方法更新目标字段的值：

```java
Query query = new Query(Criteria.where("_id").is(orderId));
Update update = new Update()
update.set("customer","Pushy")  // 更新customer字段的值为Pushy
update.set("type","good") // 更新type字段的值为good

mongoTemplate.updateFirst(query,update,Order.class);
```

- 删除操作

```java
Query query = new Query(Criteria.where("_id").is(orderId));
mongoTemplate.remove(query,Order.class);
```


### 4.2 复杂对象的操作

我们将上面的文档对象修改如下的复杂的形式：

#### Order

```
@Document
public class Order {
    @Id
    private String id;
    @Field
    private Items items;
    @Field
    private List<Comment> comments;
    ...
}
```

#### Items

```java
public class Items {
    private List<ItemField> freshItemList;
    private List<ItemField> noFreshItemList;
    ...
}
```

#### ItemField

```
public class ItemField {
    private String id;
    private String product;
    private double price;
    ...
}
```

#### Comment

```java
public class Comment {
    private String id;
    private String content;
    ...
}
```

在插入一条数据后的格式如下：

```json
{
    "_id" : "f87d5598-5332-4356-9721-930c728794a5",
    "type" : "good",
    "items" : {
        "freshItemList" : [ 
            {
                "_id" : "dacd3af3-f488-41c5-aed6-fc77bf352982",
                "price" : 400
            }, 
            {
                "_id" : "10706630-24b9-4e31-a449-096f21eb08e3",
                "price" : 900.0
            }
        ],
        "noFreshItemList" : [ 
            {
                "_id" : "29b75bec-1bdc-41f1-9e21-6b15e17bee2c",
                "price" : 200.0
            }
        ]
    },
    "comments" : [ 
        {
            "_id" : "08762bfb-35d7-499b-87f5-397fae27ebb9",
            "content" : "modified the comment"
        }, 
        {
            "_id" : "66562bfb-35d7-499b-87f5-397fae27e6b9",
            "content" : "modified the comment"
        }
    ],
    "_class" : "com.pushy.mongodbdemo.pojo.Order"
}
```


#### 向freshItemList数组中添加一个对象

```java
String id = "f87d5598-5332-4356-9721-930c728794a5";
// 查找出需要更新的记录
Query query = Query.query(Criteria.where("_id").is(id));
// 创建一个ItemField对象
ItemField item = new ItemField();
item.setId(UUID.randomUUID().toString());
item.setPrice(300);
// 进行更新的操作
Update update = new Update();
// 向数组中插入对象通过addToSet方法进行插入
update.addToSet("items.$.freshItemList", item);

mongoTemplate.upsert(query, update, Order.class);
```

#### 更新评论内容

`comments.$.content`表达式则代表更新`comments`数组中的某个对象的`content`字段：

```
String orderId = "f87d5598-5332-4356-9721-930c728794a5";
String commentId = "08762bfb-35d7-499b-87f5-397fae27ebb9";

Query query = Query.query(Criteria.where("_id").is(orderId)
        .and("comments._id").is(commentId));
Update update = new Update();
update.set("comments.$.content", "modified comment");
mongoTemplate.upsert(query, update, Order.class);
```

#### 更新freshItemList某个对象的价格

```
String orderId = "f87d5598-5332-4356-9721-930c728794a5";
String itemId = "dacd3af3-f488-41c5-aed6-fc77bf352982";

// 查找出需要更新的freshItemList中的对象
Query query = Query.query(Criteria.where("_id").is("orderId")
        .and("items.freshItemList._id").is("itemId"));
Update update = new Update();
// 更新价格
update.set("items.freshItemList.$.price", 400);
mongoTemplate.upsert(query, update, Order.class);
```