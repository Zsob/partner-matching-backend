package com.xyz.yupao.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xyz.yupao.model.domain.Tag;
import com.xyz.yupao.service.TagService;
import com.xyz.yupao.mapper.TagMapper;
import org.springframework.stereotype.Service;

/**
* @author 74703
* @description 针对表【tag(标签)】的数据库操作Service实现
* @createDate 2024-07-24 17:38:55
*/
@Service
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag>
    implements TagService{

}




