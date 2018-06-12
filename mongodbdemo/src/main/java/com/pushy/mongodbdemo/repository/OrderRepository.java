package com.pushy.mongodbdemo.repository;

import com.pushy.mongodbdemo.pojo.Order;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface OrderRepository extends MongoRepository<Order,String> {

    List<Order> findByCustomer(String c);

    List<Order> findByCustomerLike(String c);

    List<Order> findByCustomerAndType(String c, String t);

}
