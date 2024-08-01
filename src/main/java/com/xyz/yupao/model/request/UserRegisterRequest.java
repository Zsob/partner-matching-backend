package com.xyz.yupao.model.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户注册请求体
 */
@Data
public class UserRegisterRequest implements Serializable {
    private static final long serialVersionUID = -7350633747225472238L;
    private String userAccount;
    private String userPassword;
    private String checkPassword;
    private String planetCode;
}
