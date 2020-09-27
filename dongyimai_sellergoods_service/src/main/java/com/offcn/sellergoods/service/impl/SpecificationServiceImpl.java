package com.offcn.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.offcn.entity.PageResult;
import com.offcn.group.Specification;
import com.offcn.mapper.TbSpecificationMapper;
import com.offcn.mapper.TbSpecificationOptionMapper;
import com.offcn.pojo.TbSpecification;
import com.offcn.pojo.TbSpecificationExample;
import com.offcn.pojo.TbSpecificationExample.Criteria;
import com.offcn.pojo.TbSpecificationOption;
import com.offcn.pojo.TbSpecificationOptionExample;
import com.offcn.sellergoods.service.SpecificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
@Transactional
public class SpecificationServiceImpl implements SpecificationService {

	@Autowired
	private TbSpecificationMapper specificationMapper;
	@Autowired
	private TbSpecificationOptionMapper specificationOptionMapper;
	
	/**
	 * 查询全部
	 */
	@Override
	public List<TbSpecification> findAll() {
		return specificationMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbSpecification> page=   (Page<TbSpecification>) specificationMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(Specification specification) {
		specificationMapper.insert(specification.getSpecification());	//插入规格
		//遍历规格选项
		for (TbSpecificationOption tbSpecificationOption : specification.getSpecificationOptionList()) {
			//传入id值
			tbSpecificationOption.setSpecId(specification.getSpecification().getId());
			//插入数据
			specificationOptionMapper.insert(tbSpecificationOption);

		}

	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(Specification specification){
		//根据id修改 规格
		specificationMapper.updateByPrimaryKey(specification.getSpecification());
		//根据规格的id删除 所有的规格选项
		TbSpecificationOptionExample example = new TbSpecificationOptionExample();
		TbSpecificationOptionExample.Criteria criteria = example.createCriteria();
		criteria.andSpecIdEqualTo(specification.getSpecification().getId());

		specificationOptionMapper.deleteByExample(example);
		//新增规格选项
		//遍历规格选项
		for (TbSpecificationOption tbSpecificationOption : specification.getSpecificationOptionList()) {
			//传入id值
			tbSpecificationOption.setSpecId(specification.getSpecification().getId());
			//插入数据
			specificationOptionMapper.insert(tbSpecificationOption);

		}
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public Specification findOne(Long id){
		//查询规格
		TbSpecification tbSpecification = specificationMapper.selectByPrimaryKey(id);
		//根据规格的id查询规格选项
		//1.创建一个查询的条件
		TbSpecificationOptionExample example = new TbSpecificationOptionExample();
		TbSpecificationOptionExample.Criteria criteria = example.createCriteria();
		//2.添加查询条件
		criteria.andSpecIdEqualTo(tbSpecification.getId());

		List<TbSpecificationOption> specificationOptionList = specificationOptionMapper.selectByExample(example);
        //构建规格组合实体类
		Specification specification = new Specification();
		specification.setSpecification(tbSpecification);
		specification.setSpecificationOptionList(specificationOptionList);
		return specification;

	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			//删除规格
			specificationMapper.deleteByPrimaryKey(id);
			//删除规格关联的规格选项
			//1.创建删除条件
			TbSpecificationOptionExample example = new TbSpecificationOptionExample();
			TbSpecificationOptionExample.Criteria criteria = example.createCriteria();
			criteria.andSpecIdEqualTo(id);
			//2.删除
			specificationOptionMapper.deleteByExample(example);

		}		
	}
	
	
		@Override
	public PageResult findPage(TbSpecification specification, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbSpecificationExample example=new TbSpecificationExample();
		Criteria criteria = example.createCriteria();
		
		if(specification!=null){			
						if(specification.getSpecName()!=null && specification.getSpecName().length()>0){
				criteria.andSpecNameLike("%"+specification.getSpecName()+"%");
			}	
		}
		
		Page<TbSpecification> page= (Page<TbSpecification>)specificationMapper.selectByExample(example);
		return new PageResult(page.getTotal(), page.getResult());
	}

	@Override
	public List<Map> selectOptionList() {
		return specificationMapper.selectOptionList();
	}
}
