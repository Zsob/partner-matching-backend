package com.xyz.yupao.model.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 队伍加入请求对象。
 * 用于封装用户加入队伍时所需的数据
 */
@Data
public class TeamJoinRequest implements Serializable {


    private static final long serialVersionUID = -1157419961937321451L;
    /**
     * 队伍id
     */
    private Long teamId;

    /**
     * 密码
     */
    private String password;
}
