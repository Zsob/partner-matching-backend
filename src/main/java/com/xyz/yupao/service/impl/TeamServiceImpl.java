package com.xyz.yupao.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xyz.yupao.common.ErrorCode;
import com.xyz.yupao.exception.BusinessException;
import com.xyz.yupao.model.domain.Team;
import com.xyz.yupao.model.domain.User;
import com.xyz.yupao.model.domain.UserTeam;
import com.xyz.yupao.model.dto.TeamQuery;
import com.xyz.yupao.model.enums.TeamStatusEnum;
import com.xyz.yupao.model.request.TeamAddRequest;
import com.xyz.yupao.model.request.TeamJoinRequest;
import com.xyz.yupao.model.request.TeamUpdateRequest;
import com.xyz.yupao.model.vo.TeamUserVO;
import com.xyz.yupao.model.vo.UserVO;
import com.xyz.yupao.service.TeamService;
import com.xyz.yupao.mapper.TeamMapper;
import com.xyz.yupao.service.UserService;
import com.xyz.yupao.service.UserTeamService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * @author 74703
 * @description 针对表【team(队伍)】的数据库操作Service实现
 * @createDate 2024-08-01 14:49:59
 */
@Service
public class TeamServiceImpl extends ServiceImpl<TeamMapper, Team>
        implements TeamService {
    @Resource
    private UserService userService;
    @Resource
    private UserTeamService userTeamService;

    @Override
    public long addTeam(Team team, User loginUser) {
        //1. 请求参数是否为空
        if (team == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //2. 是否登录，未登录不允许创建
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        //3. 校验信息
        //  a.队伍人数 > 1 且 <= 20
        Integer maxNum = team.getMaxNum();
        if (maxNum < 1 || maxNum > 20) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍人数不符合要求");
        }
        //  b.队伍标题 <= 20
        String name = team.getName();
        if (StringUtils.isBlank(name) || name.length() > 20) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍标题不符合要求");
        }
        //  c.描述 <= 512
        String description = team.getDescription();
        if (description.length() > 512) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍描述过长");
        }
        //  d.验证队伍状态是否有效
        int status = Optional.ofNullable(team.getStatus()).orElse(TeamStatusEnum.PUBLIC.getValue());
        TeamStatusEnum statusEnum = TeamStatusEnum.getEnumByValue(status);
        if (statusEnum == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍状态不符合要求");
        }
        //  e.如果 status 是加密状态，一定要有密码，且密码 <= 32位
        String password = team.getPassword();
        if (TeamStatusEnum.SECRET.equals(statusEnum)) {
            if (StringUtils.isBlank(password) || password.length() > 32) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码设置不正确");
            }
        }
        //  f.超时时间 > 当前时间
        Date expireTime = team.getExpireTime();
        if (expireTime != null && new Date().after(expireTime)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "超时时间设置错误");
        }
        //  g.校验用户最多创建 5 个队伍
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", loginUser.getId());
        long hasTeamNum = this.count(queryWrapper);
        if (hasTeamNum >= 5) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户最多创建5个队伍");
        }
        //4. 插入队伍信息到队伍表
        team.setId(null);
        team.setUserId(loginUser.getId());
        boolean result = this.save(team);
        long teamId = team.getId();
        if (!result) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "创建队伍失败");
        }
        //5. 插入用户 => 队伍关系到关系表
        UserTeam userTeam = new UserTeam();
        userTeam.setUserId(loginUser.getId());
        userTeam.setTeamId(teamId);
        userTeam.setJoinTime(new Date());
        result = userTeamService.save(userTeam);
        if (!result) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "创建队伍用户关系失败");
        }
        return teamId;
    }

    @Override
    public List<TeamUserVO> listTeams(TeamQuery teamQuery, boolean isAdmin) {
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>();
        //组合查询条件
        if (teamQuery != null) {
            Long id = teamQuery.getId();
            if (id != null && id > 0) {
                queryWrapper.eq("id", id);
            }
            // 根据多个 ID 查询
            List<Long> idList = teamQuery.getIdList();
            if (CollectionUtils.isNotEmpty(idList)) {
                queryWrapper.in("id", idList);
            }
            // 根据搜索文本模糊查询队伍名称
            String searchText = teamQuery.getSearchText();
            if (StringUtils.isNotBlank(searchText)) {
                queryWrapper.and(qw -> qw.like("name", searchText).or().like("description", searchText));
            }
            // 根据队伍名称模糊搜索
            String name = teamQuery.getName();
            if (StringUtils.isNotBlank(name)) {
                queryWrapper.like("name", name);
            }
            // 根据描述模糊搜索
            String description = teamQuery.getDescription();
            if (StringUtils.isNotBlank(description)) {
                queryWrapper.like("description", description);
            }
            // 根据最多人数精确搜索
            Integer maxNum = teamQuery.getMaxNum();
            if (maxNum != null && maxNum >= 0) {
                queryWrapper.eq("maxNum", maxNum);
            }
            // 根据用户 ID 精确搜索
            Long userId = teamQuery.getUserId();
            if (userId != null && userId > 0) {
                queryWrapper.eq("userId", userId);
            }
            // 根据队伍状态查询，使用枚举类型确保数据的有效性
            Integer status = teamQuery.getStatus();
            TeamStatusEnum statusEnum = TeamStatusEnum.getEnumByValue(status);
            // 默认为公开状态
            if (statusEnum == null) {
                statusEnum = TeamStatusEnum.PUBLIC;
            }
            if (!isAdmin && TeamStatusEnum.PRIVATE.equals(statusEnum)) {
                throw new BusinessException(ErrorCode.NO_AUTH);
            }
            queryWrapper.eq("status", statusEnum.getValue());
        }
        // 排除过期队伍
        // where expireTime > CURRENT_DATE or expireTime is NULL
        queryWrapper.and(qw -> qw.gt("expireTime", new Date()).or().isNull("expireTime"));
        // 执行查询并获取队伍列表
        List<Team> teamList = this.list(queryWrapper);
        // 查询队伍数据为空
        if (CollectionUtils.isEmpty(teamList)) {
            return new ArrayList<>();
        }
        // 关联查询创建人的用户信息并构建返回列表
        ArrayList<TeamUserVO> teamUserVOList = new ArrayList<>();
        for (Team team : teamList) {
            Long userId = team.getUserId();
            if (userId == null) {
                continue;
            }
            User user = userService.getById(userId);
            TeamUserVO teamUserVO = new TeamUserVO();
            BeanUtils.copyProperties(team, teamUserVO);
            if (user != null) {
                UserVO userVO = new UserVO();
                BeanUtils.copyProperties(user,userVO);
                teamUserVO.setCreateUser(userVO);
            }
            teamUserVOList.add(teamUserVO);
        }
        return teamUserVOList;
    }

    @Override
    public boolean updateTeam(TeamUpdateRequest teamUpdateRequest, User loginUser) {
        // 检查队伍对象是否为空
        if (teamUpdateRequest==null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 获取修改队伍的id并验证
        Long id = teamUpdateRequest.getId();
        if (id==null || id<=0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"队伍不存在");
        }
        // 获取旧的队伍信息
        Team oldTeam = this.getById(id);
        if (oldTeam==null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"更新队伍不存在");
        }
        // 只有管理员或队伍的创建人可以修改信息
        if (oldTeam.getUserId()!= loginUser.getId() && !userService.isAdmin(loginUser)){
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        // 如果队伍状态为加密且原来不是加密状态，检验是否设置了密码
        TeamStatusEnum newStatusEnum = TeamStatusEnum.getEnumByValue(teamUpdateRequest.getStatus());
        TeamStatusEnum oldStatusEnum = TeamStatusEnum.getEnumByValue(oldTeam.getStatus());
        if (TeamStatusEnum.SECRET.equals(newStatusEnum) && !TeamStatusEnum.SECRET.equals(oldStatusEnum)){
            if (StringUtils.isBlank(teamUpdateRequest.getPassword())){
                throw new BusinessException(ErrorCode.PARAMS_ERROR,"加密队伍必须设置密码");
            }
        }
        // 创建新的队伍对象，并复制属性，进行更新
        Team team = new Team();
        BeanUtils.copyProperties(teamUpdateRequest,team);
        // 执行更新操作，返回更新的结果
        return this.updateById(team);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean joinTeam(TeamJoinRequest teamJoinRequest, User loginUser) {
        // 确认请求完整性
        if (teamJoinRequest==null){
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        // 验证队伍ID有效性
        Long teamId = teamJoinRequest.getTeamId();
        if (teamId==null || teamId<=0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 获取队伍信息，验证是否存在
        Team team = this.getById(teamId);
        if (team==null){
            throw new BusinessException(ErrorCode.NULL_ERROR,"队伍不存在");
        }
        // 检查队伍是否过期
        Date expireTime = team.getExpireTime();
        if (expireTime!=null && expireTime.before(new Date())){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"队伍已过期");
        }
        // 检查队伍加入权限
        Integer status = team.getStatus();
        TeamStatusEnum statusEnum = TeamStatusEnum.getEnumByValue(status);
        if (TeamStatusEnum.PRIVATE.equals(statusEnum)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"禁止加入私有队伍");
        }
        // 对于加密队伍，验证密码是否正确
        String password = teamJoinRequest.getPassword();
        if (TeamStatusEnum.SECRET.equals(statusEnum) && (StringUtils.isBlank(password) || !password.equals(team.getPassword()))){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"密码错误");
        }
        // 检查用户是否超过队伍数量限制(5个)
        long userId = loginUser.getId();
        QueryWrapper<UserTeam> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId",userId);
        long hasJoinTeam = userTeamService.count(queryWrapper);
        if (hasJoinTeam>=5){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"最多创建和加入5个队伍");
        }
        // 检查是否重复加入
        queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("userId",userId);
        queryWrapper.eq("teamId",teamId);
        long hasUserJoinTeam = userTeamService.count(queryWrapper);
        if (hasUserJoinTeam>0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户已加入该队伍");
        }
        // 检查队伍是否满员
        queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("teamId", teamId);
        long teamHasJoinNum = userTeamService.count(queryWrapper);
        if (teamHasJoinNum>=team.getMaxNum()){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"队伍已满");
        }
        // 执行加入操作
        UserTeam userTeam = new UserTeam();
        userTeam.setUserId(userId);
        userTeam.setTeamId(teamId);
        userTeam.setJoinTime(new Date());
        return userTeamService.save(userTeam);
    }
}




