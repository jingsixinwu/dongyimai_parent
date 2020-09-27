package com.offcn.listener;

import com.alibaba.fastjson.JSON;
import com.offcn.pojo.TbItem;
import com.offcn.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import javax.xml.soap.Text;
import java.util.List;
import java.util.Map;

@Component
public class ItemSearchListener implements MessageListener {
    @Autowired
    private ItemSearchService itemSearchService;



    @Override
    public void onMessage(Message message) {
        System.out.println("监听接收到消息...");
        try {
            TextMessage textMessage =(TextMessage)message;

            String text = textMessage.getText();
            List<TbItem> list = JSON.parseArray(text, TbItem.class);
            for(TbItem item:list){
                System.out.println(item.getId()+" "+item.getTitle());
                Map specMap= JSON.parseObject(item.getSpec());//将spec字段中的json字符串转换为map
                item.setSpecMap(specMap);//给带注解的字段赋值
            }

            itemSearchService.importList(list);//导入
            System.out.println("成功导入到索引库");

        } catch (JMSException e) {
            e.printStackTrace();
        }


    }
}
