package com.pushy.mongodbdemo.config;


import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import javax.annotation.Resource;
import java.util.Arrays;

@Configuration
@EnableMongoRepositories(basePackages = "com.pushy.mongodbdemo.repository")
public class MongoConfig extends AbstractMongoConfiguration {

    @Autowired
    private Environment env;

    @Override
    public MongoClient mongoClient() {  // 创建一个mongo客户端
//        MongoCredential mongoCredential = MongoCredential.createMongoCRCredential(
//            env.getProperty("mongo.username"),
//                "OrdersDB",
//            env.getProperty("mongo.password").toCharArray());
//
//        return new MongoClient(
//                new ServerAddress("localhost",37017),
//                Arrays.asList(mongoCredential)
//        );
        return new MongoClient();
    }

    @Override
    protected String getDatabaseName() {  // 指定数据库的名称
        return "OrdersDB";
    }

}
