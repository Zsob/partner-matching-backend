package com.xyz.yupao.service;

import com.xyz.yupao.model.domain.Team;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xyz.yupao.model.domain.User;
import com.xyz.yupao.model.dto.TeamQuery;
import com.xyz.yupao.model.request.TeamAddRequest;
import com.xyz.yupao.model.request.TeamJoinRequest;
import com.xyz.yupao.model.request.TeamQuitRequest;
import com.xyz.yupao.model.request.TeamUpdateRequest;
import com.xyz.yupao.model.vo.TeamUserVO;

import java.util.List;

/**
* @description 针对表【team(队伍)】的数据库操作Service
* @createDate 2024-08-01 14:49:59
*/
public interface TeamService extends IService<Team> {

    /**
     * 添加队伍
     * @param team
     * @param loginUser
     * @return
     */
    long addTeam(Team team, User loginUser);

    /**
     * 查询队伍
     * @param teamQuery
     * @param isAdmin
     * @return
     */
    List<TeamUserVO> listTeams(TeamQuery teamQuery,boolean isAdmin);

    /**
     * 更新队伍
     * @param teamUpdateRequest
     * @param loginUser
     * @return
     */
    boolean updateTeam(TeamUpdateRequest teamUpdateRequest, User loginUser);

    /**
     * 加入队伍
     * @param teamJoinRequest
     * @param loginUser
     * @return
     */
    boolean joinTeam(TeamJoinRequest teamJoinRequest, User loginUser);

    /**
     * 退出队伍
     * @param teamQuitRequest
     * @param loginUser
     * @return
     */
    boolean quitTeam(TeamQuitRequest teamQuitRequest, User loginUser);

    /**
     * 解散队伍
     * @param id
     * @param loginUser
     * @return
     */
    boolean deleteTeam(long id, User loginUser);
}
