package com.offcn.mapper;

import com.offcn.pojo.TbGoods;
import com.offcn.pojo.TbGoodsExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface TbGoodsMapper {
    long countByExample(TbGoodsExample example);

    int deleteByExample(TbGoodsExample example);

    int deleteByPrimaryKey(Long id);

    int insert(TbGoods record);

    int insertSelective(TbGoods record);

    List<TbGoods> selectByExample(TbGoodsExample example);

    TbGoods selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") TbGoods record, @Param("example") TbGoodsExample example);

    int updateByExample(@Param("record") TbGoods record, @Param("example") TbGoodsExample example);

    int updateByPrimaryKeySelective(TbGoods record);

    int updateByPrimaryKey(TbGoods record);
}