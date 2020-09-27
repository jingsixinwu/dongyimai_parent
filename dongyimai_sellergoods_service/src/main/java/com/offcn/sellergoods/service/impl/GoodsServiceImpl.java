package com.offcn.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.offcn.entity.PageResult;
import com.offcn.group.Goods;
import com.offcn.mapper.*;
import com.offcn.pojo.*;
import com.offcn.pojo.TbGoodsExample.Criteria;
import com.offcn.sellergoods.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
@Transactional
public class GoodsServiceImpl implements GoodsService {

	@Autowired
	private TbGoodsMapper goodsMapper;
	@Autowired
	private TbGoodsDescMapper goodsDescMapper;
	@Autowired
	private TbBrandMapper brandMapper;
	@Autowired
	private TbItemCatMapper itemCatMapper;
	@Autowired
	private TbSellerMapper sellerMapper;
	@Autowired
	private TbItemMapper itemMapper;
	
	/**
	 * 查询全部
	 */
	@Override
	public List<TbGoods> findAll() {
		return goodsMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbGoods> page=   (Page<TbGoods>) goodsMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(Goods goods) {
		goods.getGoods().setAuditStatus("0");//设置待审核状态
		goodsMapper.insert(goods.getGoods()); //获得刚刚插入生成的id 值

		//int x=1/0;

		goods.getGoodsDesc().setGoodsId(goods.getGoods().getId());
		//插入 扩展类型
		goodsDescMapper.insert(goods.getGoodsDesc());
        saveItemList(goods);


	}

	private void saveItemList(Goods goods){
		//判断是否启用规格
		if("1".equals(goods.getGoods().getIsEnableSpec())){//启用
			for(TbItem item :goods.getItemList()){
				//标题
				String title= goods.getGoods().getGoodsName();
				Map<String,Object> specMap = JSON.parseObject(item.getSpec());
				for(String key:specMap.keySet()){
					title+=" "+ specMap.get(key);
				}
				item.setTitle(title);
				setItemValus(goods,item);
				itemMapper.insert(item);
			}
		}else{//不启用规格
			TbItem item=new TbItem();
			item.setTitle(goods.getGoods().getGoodsName());//商品SPU+规格描述串作为SKU名称
			item.setPrice( goods.getGoods().getPrice() );//价格
			item.setStatus("1");//状态
			item.setIsDefault("1");//是否默认
			item.setNum(99999);//库存数量
			item.setSpec("{}");
			setItemValus(goods,item);
			itemMapper.insert(item);
		}



	}
    private void setItemValus(Goods goods,TbItem item){

		item.setGoodsId(goods.getGoods().getId());//商品SPU编号
		item.setSellerId(goods.getGoods().getSellerId());//商家编号
		item.setCategoryid(goods.getGoods().getCategory3Id());//商品分类编号（3级）
		item.setCreateTime(new Date());//创建日期
		item.setUpdateTime(new Date());//修改日期
		//品牌名称
		TbBrand brand = brandMapper.selectByPrimaryKey(goods.getGoods().getBrandId());
		item.setBrand(brand.getName());
		//分类名称
		TbItemCat itemCat = itemCatMapper.selectByPrimaryKey(goods.getGoods().getCategory3Id());
		item.setCategory(itemCat.getName());
		//商家名称
		TbSeller seller = sellerMapper.selectByPrimaryKey(goods.getGoods().getSellerId());
		item.setSeller(seller.getNickName());
		//图片地址（取spu的第一个图片）
		List<Map> imageList = JSON.parseArray(goods.getGoodsDesc().getItemImages(), Map.class) ;
		if(imageList.size()>0){
			item.setImage ( (String)imageList.get(0).get("url"));
		}

	}
	
	/**
	 * 修改
	 */
	@Override
	public void update(Goods goods){
		goods.getGoods().setAuditStatus("0");//设置待审核状态
		goodsMapper.updateByPrimaryKey(goods.getGoods()); //获得刚刚插入生成的id 值

		//插入 扩展类型
		goodsDescMapper.updateByPrimaryKey(goods.getGoodsDesc());
		saveItemList(goods);
        //删除原有的SKU列表数据
		TbItemExample tbItemExample = new TbItemExample();
		TbItemExample.Criteria criteria = tbItemExample.createCriteria();
		criteria.andGoodsIdEqualTo(goods.getGoods().getId());
		itemMapper.deleteByExample(tbItemExample);

		//添加新的SKU列表信息
		saveItemList(goods);

	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public Goods findOne(Long id){
		Goods  goods=new Goods();
		//根据id查询spu goods表中的信息
		TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
        goods.setGoods(tbGoods);
        //查询扩展属性  goodDesc
		TbGoodsDesc tbGoodsDesc = goodsDescMapper.selectByPrimaryKey(id);
        goods.setGoodsDesc(tbGoodsDesc);

        //查询sku,根据goodsId
		TbItemExample tbItemExample = new TbItemExample();
		TbItemExample.Criteria criteria = tbItemExample.createCriteria();
		criteria.andGoodsIdEqualTo(id);

		List<TbItem> tbItems = itemMapper.selectByExample(tbItemExample);

		goods.setItemList(tbItems);

		return goods;





	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			//修改为物理删除
			//查询
			TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
			tbGoods.setIsDelete("1");
			//同步到数据库
			goodsMapper.updateByPrimaryKey(tbGoods);

			//goodsMapper.deleteByPrimaryKey(id);
		}

		//修改商品sdu状态为禁用
		List<TbItem> listitem=findItemListByGoodsIdandStatus(ids,"1");
		for (TbItem tbItem : listitem) {
			tbItem.setStatus("0");
			itemMapper.updateByPrimaryKey(tbItem);
		}

	}
	
	
		@Override
	public PageResult findPage(TbGoods goods, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbGoodsExample example=new TbGoodsExample();
		Criteria criteria = example.createCriteria();
		criteria.andIsDeleteIsNull();
		if(goods!=null){			
						if(goods.getSellerId()!=null && goods.getSellerId().length()>0){
				/*criteria.andSellerIdLike("%"+goods.getSellerId()+"%");*/
				criteria.andSellerIdEqualTo(goods.getSellerId());
			}			if(goods.getGoodsName()!=null && goods.getGoodsName().length()>0){
				criteria.andGoodsNameLike("%"+goods.getGoodsName()+"%");
			}			if(goods.getAuditStatus()!=null && goods.getAuditStatus().length()>0){
				criteria.andAuditStatusLike("%"+goods.getAuditStatus()+"%");
			}			if(goods.getIsMarketable()!=null && goods.getIsMarketable().length()>0){
				criteria.andIsMarketableLike("%"+goods.getIsMarketable()+"%");
			}			if(goods.getCaption()!=null && goods.getCaption().length()>0){
				criteria.andCaptionLike("%"+goods.getCaption()+"%");
			}			if(goods.getSmallPic()!=null && goods.getSmallPic().length()>0){
				criteria.andSmallPicLike("%"+goods.getSmallPic()+"%");
			}			if(goods.getIsEnableSpec()!=null && goods.getIsEnableSpec().length()>0){
				criteria.andIsEnableSpecLike("%"+goods.getIsEnableSpec()+"%");
			}			if(goods.getIsDelete()!=null && goods.getIsDelete().length()>0){
				//criteria.andIsDeleteLike("%"+goods.getIsDelete()+"%");

			}	
		}
		
		Page<TbGoods> page= (Page<TbGoods>)goodsMapper.selectByExample(example);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 修改商品的审核状态
	 * @param ids
	 * @param status
	 */
	@Override
	public void updateAuditStatus(Long[] ids, String status) {
        for(Long id:ids){
        	//根据id查询goods
			TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
			//修改审核的状态
			tbGoods.setAuditStatus(status);
            //同步到数据库
			goodsMapper.updateByPrimaryKey(tbGoods);

			//修改
			//查询SPU对应所有SKU
			TbItemExample tbItemExample = new TbItemExample();
			TbItemExample.Criteria criteria = tbItemExample.createCriteria();
			criteria.andGoodsIdEqualTo(id);
			List<TbItem> itemsList = itemMapper.selectByExample(tbItemExample);
			for (TbItem tbItem : itemsList) {
				//修改为商家状态
				tbItem.setStatus("1");

				itemMapper.updateByPrimaryKey(tbItem);
			}



		}
	}

	/**
	 * 根据商品的id和状态信息查询item
	 * @param goodsIds
	 * @param status
	 * @return
	 */
	@Override
	public List<TbItem> findItemListByGoodsIdandStatus(Long[] goodsIds, String status) {
		//创建一个查询的条件
		TbItemExample example = new TbItemExample();
		TbItemExample.Criteria criteria = example.createCriteria();
		criteria.andGoodsIdIn(Arrays.asList(goodsIds));
		criteria.andStatusEqualTo(status);
		return itemMapper.selectByExample(example);
	}
}
