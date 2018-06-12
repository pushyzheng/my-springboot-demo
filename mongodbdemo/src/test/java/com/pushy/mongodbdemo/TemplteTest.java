package com.pushy.mongodbdemo;


import com.mongodb.WriteResult;
import com.pushy.mongodbdemo.pojo.Comment;
import com.pushy.mongodbdemo.pojo.ItemField;
import com.pushy.mongodbdemo.pojo.Items;
import com.pushy.mongodbdemo.pojo.Order;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.test.context.junit4.SpringRunner;
import org.bson.Document;
import javax.annotation.Resource;
import java.util.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TemplteTest {


    @Resource
    private MongoTemplate mongoTemplate;

    @Test
    public void insertTest() {

        Items items = new Items();

        ItemField field1 = new ItemField();
        field1.setId(UUID.randomUUID().toString());
        field1.setPrice(100);

        ItemField field2 = new ItemField();
        field2.setId(UUID.randomUUID().toString());
        field2.setPrice(200);

        List<ItemField> freshItemList = Collections.singletonList(field1);
        List<ItemField> noFreshItemList = Collections.singletonList(field2);

        items.setFreshItemList(freshItemList);
        items.setNoFreshItemList(noFreshItemList);

        Comment comment = new Comment();
        comment.setId(UUID.randomUUID().toString());
        comment.setContent("This order is good");
        comment.setUsreId("123");
        comment.setRegistrationTime(new Date());
        List<Comment> commentList = Collections.singletonList(comment);


        Order order = new Order();
        order.setId(UUID.randomUUID().toString());
        order.setType("good");

        order.setComments(commentList);
        order.setItems(items);

        mongoTemplate.save(order);

    }

    @Test
    public void selectTest() {

        // 基本查询方式
        Query query = new Query(Criteria.where("_id").is("f87d5598-5332-4356-9721-930c728794a5"));
        Order order = mongoTemplate.findOne(query, Order.class);
        order.getComments().forEach(a -> System.out.println(a.getContent()));

        // 使用Document的查询方式
        Document document = new Document();
        document.put("_id", "f87d5598-5332-4356-9721-930c728794a5");
        document.put("comments._id", "08762bfb-35d7-499b-87f5-397fae27ebb9");
        Document fields = new Document();
        fields.put("comments", true);
        Query query1 = new BasicQuery(document, fields);
        Order order1 = mongoTemplate.findOne(query1, Order.class);
        order1.getComments().forEach(a -> System.out.println(a.getContent()));


    }


    @Test
    public void updateCommentTest() {
        Query query = Query.query(Criteria.where("_id")
                .is("f87d5598-5332-4356-9721-930c728794a5")  // _id的值
                .and("comments._id")
                .is("08762bfb-35d7-499b-87f5-397fae27ebb9"));  // order._id.comments._id 的值
        Update update = new Update();
        update.set("comments.$.content", "modified the comment");
        mongoTemplate.upsert(query, update, Order.class);
    }

    @Test
    public void updateFreshItemPriceTest() {
        Query query = Query.query(Criteria.where("_id")
                .is("f87d5598-5332-4356-9721-930c728794a5")
                .and("items.freshItemList._id").is("dacd3af3-f488-41c5-aed6-fc77bf352982"));
        Update update = new Update();
        update.set("items.freshItemList.$.price", 400);
        mongoTemplate.upsert(query, update, Order.class);
    }

    @Test
    public void insertFreshItemPriceTest() {
        Query query = Query.query(Criteria.where("_id")
                .is("f87d5598-5332-4356-9721-930c728794a5")
                .and("items.freshItemList._id").is("dacd3af3-f488-41c5-aed6-fc77bf352982"));

        ItemField field1 = new ItemField();
        field1.setId(UUID.randomUUID().toString());
        field1.setPrice(900);
        field1.setProduct("白菜");

        Update update = new Update();
        update.addToSet("items.$.freshItemList", field1);

        mongoTemplate.upsert(query, update, Order.class);

    }

}
