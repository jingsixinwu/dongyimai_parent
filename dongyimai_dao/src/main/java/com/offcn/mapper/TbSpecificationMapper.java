package com.offcn.mapper;

import com.offcn.pojo.TbSpecification;
import com.offcn.pojo.TbSpecificationExample;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

public interface TbSpecificationMapper {
    long countByExample(TbSpecificationExample example);

    int deleteByExample(TbSpecificationExample example);

    int deleteByPrimaryKey(Long id);

    int insert(TbSpecification record);

    int insertSelective(TbSpecification record);

    List<TbSpecification> selectByExample(TbSpecificationExample example);

    TbSpecification selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") TbSpecification record, @Param("example") TbSpecificationExample example);

    int updateByExample(@Param("record") TbSpecification record, @Param("example") TbSpecificationExample example);

    int updateByPrimaryKeySelective(TbSpecification record);

    int updateByPrimaryKey(TbSpecification record);

    /**
     * 查询规格下来列表
     * @return
     */
    List<Map> selectOptionList();
}