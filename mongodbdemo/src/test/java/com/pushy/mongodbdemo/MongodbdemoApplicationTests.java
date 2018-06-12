package com.pushy.mongodbdemo;

import com.pushy.mongodbdemo.pojo.Order;
import com.pushy.mongodbdemo.repository.OrderRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.UUID;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MongodbdemoApplicationTests {

	@Resource
	private OrderRepository orderRepository;
	@Resource
	private MongoTemplate mongoTemplate;

	@Test
	public void contextLoads() {
		Order order = new Order();
		order.setId(UUID.randomUUID().toString());
		order.setType("1");
		order.setCustomer("Pushy");
//		orderRepository.save(order);
//		orderRepository.deleteById("1");
//		orderRepository.count();
//		orderRepository.findAll();
		System.out.println(orderRepository.findByCustomer("Pushy"));
	}

}
