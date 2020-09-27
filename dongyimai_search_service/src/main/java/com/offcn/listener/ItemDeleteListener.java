package com.offcn.listener;

import com.alibaba.fastjson.JSON;
import com.offcn.pojo.TbItem;
import com.offcn.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.*;
import java.util.List;
import java.util.Map;
@Component
public class ItemDeleteListener implements MessageListener {

    @Autowired
   private ItemSearchService itemSearchService;
    @Override
    public void onMessage(Message message) {
        System.out.println("监听接收到消息...");
        try {
            ObjectMessage objectMessage =(ObjectMessage)message;

            Long[] ids= (Long[])objectMessage.getObject();

            itemSearchService.deleteByGoodsIds(ids);
            System.out.println("删除索引库中相关数据成功");

        } catch (JMSException e) {
            e.printStackTrace();
        }


    }
}
