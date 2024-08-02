package com.xyz.yupao.model.dto;

import com.xyz.yupao.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 队伍查询对象，用于表示队伍的查询条件。
 */
@Data
@EqualsAndHashCode(callSuper = true)  //自动生成equals()和hashCode()方法
public class TeamQuery extends PageRequest {
    /**
     * id
     */
    private Long id;

    /**
     * 队伍ID列表。
     */
    private List<Long> idList;

    /**
     * 搜索关键字(可同时对队伍名称和描述搜索)
     */
    private String searchText;

    /**
     * 队伍名称
     */
    private String name;

    /**
     * 描述
     */
    private String description;

    /**
     * 最大人数
     */
    private Integer maxNum;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 0 - 公开，1 - 私有，2 - 加密
     */
    private Integer status;


}
