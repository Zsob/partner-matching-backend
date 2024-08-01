package com.xyz.yupao.common;

import lombok.Data;

import java.io.Serializable;

/**
 * 分页请求对象，用于表示分页查询的请求参数。
 */
@Data
public class PageRequest implements Serializable {

    private static final long serialVersionUID = -616737696578132666L;
    /**
     * 页面大小
     */
    protected int pageSize=10;
    /**
     * 当前页码
     */
    protected int pageNum=1;
}
