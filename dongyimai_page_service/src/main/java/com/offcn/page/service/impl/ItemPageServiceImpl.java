package com.offcn.page.service.impl;


import com.offcn.mapper.TbGoodsDescMapper;
import com.offcn.mapper.TbGoodsMapper;
import com.offcn.mapper.TbItemCatMapper;
import com.offcn.mapper.TbItemMapper;
import com.offcn.page.service.ItemPageService;
import com.offcn.pojo.TbGoods;
import com.offcn.pojo.TbGoodsDesc;
import com.offcn.pojo.TbItem;
import com.offcn.pojo.TbItemExample;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfig;

import java.io.File;
import java.io.FileWriter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ItemPageServiceImpl implements ItemPageService {

    @Value("${pagedir}")
    private String pagedir;

    @Autowired
    private FreeMarkerConfig freemarkerConfig;

    @Autowired
    private TbGoodsMapper goodsMapper;

    @Autowired
    private TbGoodsDescMapper goodsDescMapper;

    @Autowired
    private TbItemCatMapper itemCatMapper;

    @Autowired
    private TbItemMapper itemMapper;


    /**
     * 生成商品详情页
     * @param goodsId
     * @return
     */
    @Override
    public boolean genItemHtml(Long goodsId) {

        try {
            //获得配置对象
            Configuration configuration = freemarkerConfig.getConfiguration();
            //加载一个模板
            Template template = configuration.getTemplate("item.ftl");
            Map dataModel=new HashMap<String,Object>();
            //1.加载商品表数据
            TbGoods goods = goodsMapper.selectByPrimaryKey(goodsId);
            dataModel.put("goods", goods);
            //2.加载商品扩展表数据
            TbGoodsDesc goodsDesc = goodsDescMapper.selectByPrimaryKey(goodsId);
            dataModel.put("goodsDesc", goodsDesc);
            //3.加载商品分类数据
            //3.1 查找一级分类
            String itemCat1=itemCatMapper.selectByPrimaryKey(goods.getCategory1Id()).getName();

            //3.2 查找二级分类
            String itemCat2=itemCatMapper.selectByPrimaryKey(goods.getCategory2Id()).getName();
            //3.3 查找三级分类
            String itemCat3=itemCatMapper.selectByPrimaryKey(goods.getCategory3Id()).getName();

            dataModel.put("itemCat1",itemCat1);
            dataModel.put("itemCat2",itemCat2);
            dataModel.put("itemCat3",itemCat3);

            //4. 添加SKU的信息
            TbItemExample itemExample = new TbItemExample();
            TbItemExample.Criteria criteria = itemExample.createCriteria();
            criteria.andStatusEqualTo("1");//上架的状态 有效
            criteria.andGoodsIdEqualTo(goodsId);//添加spu的id
            itemExample.setOrderByClause("is_default desc");////按照状态降序，保证第一个为默

            List<TbItem> itemList = itemMapper.selectByExample(itemExample);

            dataModel.put("itemList",itemList);


            //F:\\item\\149187842867986.html
            FileWriter out = new FileWriter(pagedir+goodsId+".html");
            template.process(dataModel,out);
            out.close();

            return true;


        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean deleteItemHtml(Long[] goodsIds) {
        try {
            for (Long goodsId : goodsIds) {

                new File(pagedir+goodsId+".html").delete();
            }
            return true;
        } catch (Exception e) {
             e.printStackTrace();
             return false;
        }
    }
}
