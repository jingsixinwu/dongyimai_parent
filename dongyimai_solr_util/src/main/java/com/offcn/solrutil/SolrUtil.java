package com.offcn.solrutil;

import com.alibaba.fastjson.JSON;
import com.github.promeg.pinyinhelper.Pinyin;
import com.offcn.mapper.TbItemMapper;
import com.offcn.pojo.TbItem;
import com.offcn.pojo.TbItemExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class SolrUtil {
    @Autowired
    private TbItemMapper tbItemMapper;
    @Autowired
    private SolrTemplate solrTemplate;


    /**
     * 导入所有审核成功的商品的信息到索引库
     */

    public void importItemData(){
      //创建一个查询的条件
        TbItemExample example = new TbItemExample();
        TbItemExample.Criteria criteria = example.createCriteria();
        //查询已经审核成功的商品信息  上架的商品
        criteria.andStatusEqualTo("1");
        List<TbItem> itemList = tbItemMapper.selectByExample(example);
        System.out.println("========商品的列表：======");
        for (TbItem tbItem : itemList) {
            System.out.println(tbItem.getTitle());
            //首先取出规格数据，并且进行转换 将json字符串转换成Map集合
            Map<String,String> specMap = JSON.parseObject(tbItem.getSpec(), Map.class);
            //创建一个Map来存储转换后的结果

            Map<String,String> mapPinyin=new HashMap<String, String>();
            for (String key : specMap.keySet()) {
                mapPinyin.put(Pinyin.toPinyin(key,"").toLowerCase(),specMap.get(key));
            }

            //将转换后的值存入  item中
            tbItem.setSpecMap(mapPinyin);

        }

        solrTemplate.saveBeans(itemList);
        solrTemplate.commit();
        System.out.println("=======导入结束======");


      }

    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("classpath*:spring/applicationContext*.xml");

        SolrUtil solrUtil = (SolrUtil) context.getBean("solrUtil");
        solrUtil.importItemData();

    }
}
