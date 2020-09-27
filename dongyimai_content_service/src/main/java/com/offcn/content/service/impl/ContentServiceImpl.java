package com.offcn.content.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.offcn.content.service.ContentService;
import com.offcn.entity.PageResult;
import com.offcn.mapper.TbContentMapper;
import com.offcn.pojo.TbContent;
import com.offcn.pojo.TbContentExample;
import com.offcn.pojo.TbContentExample.Criteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class ContentServiceImpl implements ContentService {

	@Autowired
	private TbContentMapper contentMapper;

	@Autowired
	private RedisTemplate redisTemplate;

	
	/**
	 * 查询全部
	 */
	@Override
	public List<TbContent> findAll() {
		return contentMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbContent> page=   (Page<TbContent>) contentMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(TbContent content) {
		contentMapper.insert(content);
		//清空缓存数据
		redisTemplate.boundHashOps("content").delete(content.getCategoryId());
	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(TbContent content){
		//查询修改前的分类Id
		//先清理缓存
		//查的是老的分类
		Long categoryId = contentMapper.selectByPrimaryKey(content.getId()).getCategoryId();
		System.out.println("categoryIdold:"+categoryId+"，清理缓存");

		redisTemplate.boundHashOps("content").delete(categoryId);

		//修改数据
		contentMapper.updateByPrimaryKey(content);
		//判断有没有修改分类
		if(categoryId.longValue()!=content.getCategoryId().longValue()){
			//清理新的分类对应的广告内容
			System.out.println("categoryIdnew:"+content.getCategoryId()+"，清理缓存");
			redisTemplate.boundHashOps("content").delete(content.getCategoryId());


		}





	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public TbContent findOne(Long id){
		return contentMapper.selectByPrimaryKey(id);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			Long categoryId = contentMapper.selectByPrimaryKey(id).getCategoryId();
			redisTemplate.boundHashOps("content").delete(categoryId);
			contentMapper.deleteByPrimaryKey(id);
		}		
	}
	
	
		@Override
	public PageResult findPage(TbContent content, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbContentExample example=new TbContentExample();
		Criteria criteria = example.createCriteria();
		
		if(content!=null){			
						if(content.getTitle()!=null && content.getTitle().length()>0){
				criteria.andTitleLike("%"+content.getTitle()+"%");
			}			if(content.getUrl()!=null && content.getUrl().length()>0){
				criteria.andUrlLike("%"+content.getUrl()+"%");
			}			if(content.getPic()!=null && content.getPic().length()>0){
				criteria.andPicLike("%"+content.getPic()+"%");
			}			if(content.getStatus()!=null && content.getStatus().length()>0){
				criteria.andStatusLike("%"+content.getStatus()+"%");
			}	
		}
		
		Page<TbContent> page= (Page<TbContent>)contentMapper.selectByExample(example);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 根据分类id查询广告
	 * @param categoryId
	 * @return
	 */
	@Override
	public List<TbContent> findByCategoryId(Long categoryId) {

		//先从缓存中从查
		List<TbContent> contentList = (List<TbContent>)redisTemplate.boundHashOps("content").get(categoryId);
        //判断缓存中是否有值
		if(contentList==null){
			//创建一个查询的条件
			TbContentExample example = new TbContentExample();
			Criteria criteria = example.createCriteria();
			criteria.andCategoryIdEqualTo(categoryId);
			//查询的是有效状态的   1
			criteria.andStatusEqualTo("1");
			//查询的同时按照sort 进行排序
			example.setOrderByClause("sort_order");
			contentList =contentMapper.selectByExample(example);
			//存入缓存
			redisTemplate.boundHashOps("content").put(categoryId,contentList);

		}else{
			System.out.println("从缓存中读取数据");
		}


        return contentList;
	}
}
