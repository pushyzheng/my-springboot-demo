package com.pushy.client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
