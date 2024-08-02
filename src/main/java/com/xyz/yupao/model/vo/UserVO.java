package com.xyz.yupao.model.vo;


import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户包装类(脱敏)
 */
@Data
public class UserVO implements Serializable {

    private static final long serialVersionUID = -149323950570031411L;
    /**
     * 用户ID
     */
    private long id;

    /**
     * 用户昵称
     */
    private String username;

    /**
     * 账号
     */
    private String userAccount;

    /**
     * 用户头像
     */
    private String avatarUrl;

    /**
     * 性别
     */
    private Integer gender;

    /**
     * 个人简介
     */
    private String profile;

    /**
     * 密码
     */
    private String userPassword;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 星球编号
     */
    private String planetCode;

    /**
     * 标签 JSON 列表
     */
    private String tags;

    /**
     * 状态 0-正常
     */
    private Integer userStatus;

    /**
     * 电话
     */
    private String phone;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 用户角色 0-普通用户 1-管理员
     */
    private Integer userRole;
}
