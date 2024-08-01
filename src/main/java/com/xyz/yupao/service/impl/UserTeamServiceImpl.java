package com.xyz.yupao.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xyz.yupao.model.domain.UserTeam;
import com.xyz.yupao.service.UserTeamService;
import com.xyz.yupao.mapper.UserTeamMapper;
import org.springframework.stereotype.Service;

/**
* @author 74703
* @description 针对表【user_team(用户队伍关系)】的数据库操作Service实现
* @createDate 2024-08-01 14:51:17
*/
@Service
public class UserTeamServiceImpl extends ServiceImpl<UserTeamMapper, UserTeam>
    implements UserTeamService{

}




