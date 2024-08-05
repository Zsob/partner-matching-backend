package com.xyz.yupao.common;

import lombok.Data;

import java.io.Serializable;

/**
 * 删除请求对象。
 */
@Data
public class DeleteRequest implements Serializable {

    private static final long serialVersionUID = 477332225920762412L;

    private long id;
}
