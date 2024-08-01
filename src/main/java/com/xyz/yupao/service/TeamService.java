package com.xyz.yupao.service;

import com.xyz.yupao.model.domain.Team;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xyz.yupao.model.domain.User;

/**
* @author 74703
* @description 针对表【team(队伍)】的数据库操作Service
* @createDate 2024-08-01 14:49:59
*/
public interface TeamService extends IService<Team> {

    long addTeam(Team team, User loginUser);
}
