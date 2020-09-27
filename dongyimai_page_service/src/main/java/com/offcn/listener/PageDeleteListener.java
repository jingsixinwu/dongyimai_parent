package com.offcn.listener;

import com.offcn.page.service.ItemPageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import java.util.Arrays;

@Component
public class PageDeleteListener implements MessageListener {
    @Autowired
    private ItemPageService itemPageService;

    @Override
    public void onMessage(Message message) {
        ObjectMessage objectMessage=(ObjectMessage)message;

        try {
           Long[] ids= (Long[])objectMessage.getObject();
           System.out.println("ItemDeleteListener监听接收到消息..."+ Arrays.toString(ids));
            boolean b = itemPageService.deleteItemHtml(ids);

            System.out.println("删除的结果为："+b);

        } catch (JMSException e) {
            e.printStackTrace();
        }


    }
}
