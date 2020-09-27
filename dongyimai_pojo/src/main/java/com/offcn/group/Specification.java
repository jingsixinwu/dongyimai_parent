package com.offcn.group;

import com.offcn.pojo.TbSpecification;
import com.offcn.pojo.TbSpecificationOption;

import java.io.Serializable;
import java.util.List;

/**
 * 规格组合实体类
 */
public class Specification implements Serializable {
    //规格
    private TbSpecification specification;
    //规格选项
    private List<TbSpecificationOption> specificationOptionList;

    public TbSpecification getSpecification() {
        return specification;
    }

    public void setSpecification(TbSpecification specification) {
        this.specification = specification;
    }

    public List<TbSpecificationOption> getSpecificationOptionList() {
        return specificationOptionList;
    }

    public void setSpecificationOptionList(List<TbSpecificationOption> specificationOptionList) {
        this.specificationOptionList = specificationOptionList;
    }
}
