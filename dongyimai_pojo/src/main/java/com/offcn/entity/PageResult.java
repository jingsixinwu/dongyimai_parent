package com.offcn.entity;

import java.io.Serializable;
import java.util.List;

/**
 * 分页数据的包装类
 */
public class PageResult implements Serializable {

    private long total;//总记录数
    private List rows;//当前页结果

    public PageResult(long total, List rows) {
        this.total = total;
        this.rows = rows;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public List getRows() {
        return rows;
    }

    public void setRows(List rows) {
        this.rows = rows;
    }
}
