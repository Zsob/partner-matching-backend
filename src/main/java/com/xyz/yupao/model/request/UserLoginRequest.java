package com.xyz.yupao.model.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户注册请求体
 */
@Data
public class UserLoginRequest implements Serializable {
    private static final long serialVersionUID = 7367252591485854174L;
    private String userAccount;
    private String userPassword;
}
