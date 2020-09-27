package com.offcn.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.jms.*;

@RestController
@RequestMapping("/sms")
public class TestSmsController {
    @Autowired
    private JmsTemplate jmsTemplate;
    @Autowired
    private Destination smsDestination;

    /**
     * 发送消息，到消费  发送短信
     */
     @RequestMapping("/sendMsg")
    public String sendMsg(final String mobile,final String param){
       jmsTemplate.send(smsDestination, new MessageCreator() {
           @Override
           public Message createMessage(Session session) throws JMSException {
               MapMessage mapMessage = session.createMapMessage();
               mapMessage.setString("mobile",mobile);
               mapMessage.setString("param",param);


               return mapMessage;
           }
       });


        return "send ok";

    }



}
