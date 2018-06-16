package com.pushy.activemq.compoment;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
public class ActiveMqServer {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @JmsListener(destination = "topic")
    public void receiveTopic(String message) {
        logger.info("listening....");
        System.out.println(message);
    }

    @JmsListener(destination = "queue")
    public void receiveQueue(String message) {
        logger.info("listening....");
        System.out.println(message);
    }
}


