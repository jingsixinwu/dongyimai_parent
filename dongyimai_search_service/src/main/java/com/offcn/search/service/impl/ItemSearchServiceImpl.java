package com.offcn.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.promeg.pinyinhelper.Pinyin;
import com.offcn.pojo.TbItem;
import com.offcn.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Service
public class ItemSearchServiceImpl implements ItemSearchService {

    @Autowired
    private SolrTemplate solrTemplate;
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 从缓存中查询品牌和规格列表
     * @param
     * @return
     */
    private Map  searchBrandAndSpec(String category){
        //创建一个Map存储查询数据
        Map map=new HashMap();
        //通过分类的名称查询出模板的id
        Long typeId=(Long)redisTemplate.boundHashOps("itemCat").get(category);

        //判断模板id是否为空
        if(typeId!=null){
            //根据模板id取出品牌列表
            List brandList=(List)redisTemplate.boundHashOps("brandList").get(typeId);
            map.put("brandList",brandList);

            //根据模板id取出规格列表 包括有规格选项

            List specList=(List)redisTemplate.boundHashOps("specList").get(typeId);
            map.put("specList",specList);

        }
         return map;

    }



    @Override
    public Map<String, Object> search(Map searchMap) {
        //用来存储 查询的结果
        Map<String, Object> map=new HashMap<String, Object>();
        //1.查询列表
        map.putAll(searchList(searchMap));
        //2. 根据关联自查询商品分类
        List categoryList = searchCategoryList(searchMap);
        map.put("categoryList",categoryList);

        if(categoryList.size()>0){
            map.putAll(searchBrandAndSpec((String)categoryList.get(0)));

        }
        return map;
    }
    //根据关键字查询，对查询的结果进行高亮显示
    private Map searchList(Map searchMap){
         Map map=new HashMap();
         //1.创建一个可以支持高亮查询查询器对象
        SimpleHighlightQuery query = new SimpleHighlightQuery();
        //2. 设定需要高亮处理字段
        HighlightOptions highlightOptions = new HighlightOptions();
        highlightOptions.addField("item_title");
        //3.设置高亮的前缀和后缀
        highlightOptions.setSimplePrefix("<em style='color:red'>");
        highlightOptions.setSimplePostfix("</em>");
        //4.关联高亮选项到高亮查询器对象中
        query.setHighlightOptions(highlightOptions);

        //处理关键字

        if(searchMap.get("keywords")!=null){
            int index = searchMap.get("keywords").toString().indexOf(" ");
            if(index>0){
                searchMap.put("keywords",searchMap.get("keywords").toString().replaceAll(" ",""));
            }

        }

        //5.设置查询条件 根据关键字查询
        //创建一个查询条件对象
        //5.1按照搜索关键字来查询
        Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
        //5.2按照分类查询
        if(!"".equals(searchMap.get("category"))){
            Criteria criteria_ca =new Criteria("item_category").is(searchMap.get("category"));
            SimpleFilterQuery filterQuery = new SimpleFilterQuery(criteria_ca);
            query.addFilterQuery(filterQuery);

        }
        //5.3根据品牌查询
        if(!"".equals(searchMap.get("brand"))){

            Criteria criteria_br = new Criteria("item_brand").is(searchMap.get("brand"));
            SimpleFilterQuery filterQuery_br = new SimpleFilterQuery(criteria_br);
            query.addFilterQuery(filterQuery_br);

        }
        //5.4添加规格查询

        if(searchMap.get("spec")!=null){

            Map<String,String> specMap=(Map)searchMap.get("spec");//{"网络":"移动4G","机身内存":"32G"}
            for (String key : specMap.keySet()) {
                Criteria criteria_spec = new Criteria("item_spec_"+ Pinyin.toPinyin(key,"").toLowerCase()).is(specMap.get(key));

                SimpleFilterQuery filterQuery_spec = new SimpleFilterQuery(criteria_spec);
                query.addFilterQuery(filterQuery_spec);
            }
        }

        //5.5添加价格查询
        if(!"".equals(searchMap.get("price"))){
            String[] price = ((String) searchMap.get("price")).split("-");
            if(!price[0].equals("0")){//如果区间起点不等于0
                Criteria filterCriteria=new Criteria("item_price").greaterThanEqual(price[0]);
                FilterQuery filterQuery=new SimpleFilterQuery(filterCriteria);
                query.addFilterQuery(filterQuery);
            }
            if(!price[1].equals("*")){//如果区间终点不等于*
                Criteria filterCriteria=new  Criteria("item_price").lessThanEqual(price[1]);
                FilterQuery filterQuery=new SimpleFilterQuery(filterCriteria);
                query.addFilterQuery(filterQuery);
            }
        }

        //5.6 根据分页查询
        //处理当前页面
        Integer pageNo=(Integer)searchMap.get("pageNo");
        if(pageNo==null){
            pageNo=1;//默认第一页
        }

        //处理每页显示的记录条数
        Integer pageSize=(Integer)searchMap.get("pageSize");
        if(pageSize==null){
            pageSize=20;//默认显示20条数据
        }

        query.setOffset((pageNo-1)*pageSize);//设置查询的起始记录
        query.setRows(pageSize);//查询多少条记录
        //5.7 按照进行排序输出
        String sortValue=(String)searchMap.get("sort");//ASC  DESC
        String sortField=(String)searchMap.get("sortField");
        //判断排序是升序还是降序:
        if(sortValue!=null&&!sortValue.equals("")){
            if(sortValue.equals("ASC")){
                Sort sort = new Sort(Sort.Direction.ASC,"item_"+sortField);
                query.addSort(sort);

            }
            if(sortValue.equals("DESC")){
                Sort sort = new Sort(Sort.Direction.DESC,"item_"+sortField);
                query.addSort(sort);

            }





        }





        //关联查询条件对象到高亮查询器对象中
        query.addCriteria(criteria);

        //6.发出带高亮查询数据的查询请求
        HighlightPage<TbItem> page= solrTemplate.queryForHighlightPage(query, TbItem.class);
        //7.获取高亮集合的入口
        List<HighlightEntry<TbItem>> highlighted = page.getHighlighted();
        //8.遍历高亮集合
        for (HighlightEntry<TbItem> highlightEntry : highlighted) {
              //获取数据基本对象
            TbItem tbItem = highlightEntry.getEntity();
            if(highlightEntry.getHighlights().size()>0&&highlightEntry.getHighlights().get(0).getSnipplets().size()>0) {
                List<HighlightEntry.Highlight> highlightList = highlightEntry.getHighlights();
                //高亮结果集合
                List<String> snipplets = highlightList.get(0).getSnipplets();
                //获取第一个高亮字段对应的高亮结果，设置到商品标题
                tbItem.setTitle(snipplets.get(0));

            }


        }
        //把带高亮数据集合存放map
        map.put("rows",page.getContent());
        //返回总页面数据和总记录条数
        map.put("totalPages",page.getTotalPages());//总页数
        map.put("total",page.getTotalElements());//总记录数



        return map;
   }


    /**
     * 查询分类列表
     */
    private List searchCategoryList(Map searchMap){
       List<String> list=new ArrayList<String>();

       Query query=new SimpleQuery();
       //按照关键字查询
       Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
       //将查询条件对象添加到 查询器对象上
       query.addCriteria(criteria);

       //设置分组选项
        GroupOptions groupOptions = new GroupOptions().addGroupByField("item_category");
       //将分组选项添加到查询对象上
        query.setGroupOptions(groupOptions);

        //查询索引库得到分组页面
        GroupPage<TbItem> page = solrTemplate.queryForGroupPage(query, TbItem.class);
        //根据列得到分组结果集
        GroupResult<TbItem> groupResult = page.getGroupResult("item_category");
        //得到分组结果入口页面
        Page<GroupEntry<TbItem>> groupEntries = groupResult.getGroupEntries();
        //得到分组入口集合
        List<GroupEntry<TbItem>> groupEntryList = groupEntries.getContent();
        for (GroupEntry<TbItem> tbItemGroupEntry : groupEntryList) {
            list.add(tbItemGroupEntry.getGroupValue());
        }
        
        return list;

    }

    @Override
    public void importList(List<TbItem> list) {
        for (TbItem tbItem : list) {
            System.out.println(tbItem.getTitle());
            //将sku中规格数据json转换成Map 对象
            Map<String,String> specMap = JSON.parseObject(tbItem.getSpec(), Map.class);

            Map map=new HashMap();
            for (String key : specMap.keySet()) {
                  map.put("item_spec_"+Pinyin.toPinyin(key,"").toLowerCase(),specMap.get(key));
            }

            tbItem.setSpecMap(map);//给带动态域注解的字段赋值

        }

        solrTemplate.saveBeans(list);
        solrTemplate.commit();
    }

    @Override
    public void deleteByGoodsIds(Long[] goodsIds) {
        System.out.println("删除索引库相关信息");
        SimpleQuery query = new SimpleQuery();
        Criteria criteria = new Criteria("item_goodsid").in(goodsIds);
        query.addCriteria(criteria);
        solrTemplate.delete(query);
        solrTemplate.commit();
    }
}
