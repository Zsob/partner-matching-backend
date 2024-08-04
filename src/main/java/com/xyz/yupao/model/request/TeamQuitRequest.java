package com.xyz.yupao.model.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 处理用户退出队伍的请求。
 */
@Data
public class TeamQuitRequest implements Serializable {
    private static final long serialVersionUID = -8835702238979877250L;

    /**
     * 队伍id
     */
    private Long teamId;
}
