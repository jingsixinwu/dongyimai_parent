package com.offcn.mapper;

import com.offcn.pojo.TbProvinces;
import com.offcn.pojo.TbProvincesExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface TbProvincesMapper {
    long countByExample(TbProvincesExample example);

    int deleteByExample(TbProvincesExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(TbProvinces record);

    int insertSelective(TbProvinces record);

    List<TbProvinces> selectByExample(TbProvincesExample example);

    TbProvinces selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") TbProvinces record, @Param("example") TbProvincesExample example);

    int updateByExample(@Param("record") TbProvinces record, @Param("example") TbProvincesExample example);

    int updateByPrimaryKeySelective(TbProvinces record);

    int updateByPrimaryKey(TbProvinces record);
}