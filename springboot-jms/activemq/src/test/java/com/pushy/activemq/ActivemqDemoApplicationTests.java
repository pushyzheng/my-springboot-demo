package com.pushy.activemq;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ActivemqDemoApplicationTests {

	@Resource
	private JmsTemplate jmsTemplate;

	@Test
	public void contextLoads() {
		jmsTemplate.convertAndSend("topic", "Hello World");
		jmsTemplate.convertAndSend("queue", "Hello World");
	}

}
