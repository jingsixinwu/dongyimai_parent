package com.offcn.sellergoods.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.offcn.entity.PageResult;
import com.offcn.entity.Result;
import com.offcn.group.Goods;
import com.offcn.pojo.TbGoods;
import com.offcn.pojo.TbItem;
import com.offcn.sellergoods.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import java.util.List;

/**
 * controller
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/goods")
public class GoodsController {

	@Reference
	private GoodsService goodsService;

	@Autowired
	private JmsTemplate jmsTemplate;

	@Autowired
	private Destination queueSolrDestination;

	@Autowired
	private Destination queueSolrDeleteDestination;

	@Autowired
	private Destination topicPageDestination;
	@Autowired
	private Destination topicPageDeleteDestination;

	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findAll")
	public List<TbGoods> findAll(){
		return goodsService.findAll();
	}
	
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findPage")
	public PageResult findPage(int page, int rows){
		return goodsService.findPage(page, rows);
	}
	
	/**
	 * 增加
	 * @param goods
	 * @return
	 */
	@RequestMapping("/add")
	public Result add(@RequestBody Goods goods){
		try {
			goodsService.add(goods);
			return new Result(true, "增加成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "增加失败");
		}
	}
	
	/**
	 * 修改
	 * @param goods
	 * @return
	 */
	@RequestMapping("/update")
	public Result update(@RequestBody Goods goods){
		try {
			goodsService.update(goods);
			return new Result(true, "修改成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "修改失败");
		}
	}	
	
	/**
	 * 获取实体
	 * @param id
	 * @return
	 */
	@RequestMapping("/findOne")
	public Goods findOne(Long id){
		return goodsService.findOne(id);		
	}
	
	/**
	 * 批量删除
	 * @param ids
	 * @return
	 */
	@RequestMapping("/delete")
	public Result delete(final Long [] ids){
		try {
			goodsService.delete(ids);

			jmsTemplate.send(queueSolrDeleteDestination, new MessageCreator() {
				public Message createMessage(Session session) throws JMSException {
					return session.createObjectMessage(ids);
				}
			});

			//发送消息删除商品的详情页
			jmsTemplate.send(topicPageDeleteDestination, new MessageCreator() {
				public Message createMessage(Session session) throws JMSException {
					return session.createObjectMessage(ids);
				}
			});

			return new Result(true, "删除成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "删除失败");
		}
	}
	
		/**
	 * 查询+分页
	 * @param goods
	 * @param page
	 * @param rows
	 * @return
	 */
	@RequestMapping("/search")
	public PageResult search(@RequestBody TbGoods goods, int page, int rows  ){
		return goodsService.findPage(goods, page, rows);		
	}

	/**
	 * 修改商品的状态
	 */
    @RequestMapping("/updateAuditStatus")
	public Result updateAuditStatus(Long[] ids,String status){

		try {
			goodsService.updateAuditStatus(ids,status);
             //按照SPU ID查询 SKU列表(状态为1)
			if(status.equals("1")){//审核通过
				List<TbItem> itemList = goodsService.findItemListByGoodsIdandStatus(ids, status);
				//调用搜索接口实现数据的批量导入
				if(itemList.size()>0){
					final String jsonString = JSON.toJSONString(itemList);
					jmsTemplate.send(queueSolrDestination, new MessageCreator() {
						public Message createMessage(Session session) throws JMSException {

							return session.createTextMessage(jsonString);
						}
					});


				}else{
					System.out.println("没有对应的明细数据sku");
				}
			}

			//静态页面的生成
			for (final Long id : ids) {

				//发送消息生成商品详情页
				jmsTemplate.send(topicPageDestination, new MessageCreator() {
					public Message createMessage(Session session) throws JMSException {
						return session.createTextMessage(id+"");
					}
				});


				//itemPageService.genItemHtml(id);
			}



			return new Result(true,"审核成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(true,"审核失败");
		}


	}

	/**
	 * 生成详情页
	 *//*
	@RequestMapping("/genHtml")
	public void genHtml(Long goodsId){

       itemPageService.genItemHtml(goodsId);

	}*/

}
