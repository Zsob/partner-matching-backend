package com.xyz.yupao.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xyz.yupao.common.BaseResponse;
import com.xyz.yupao.common.ErrorCode;
import com.xyz.yupao.common.ResultUtils;
import com.xyz.yupao.exception.BusinessException;
import com.xyz.yupao.model.domain.Team;
import com.xyz.yupao.model.domain.User;
import com.xyz.yupao.model.dto.TeamQuery;
import com.xyz.yupao.model.request.TeamAddRequest;
import com.xyz.yupao.model.request.TeamJoinRequest;
import com.xyz.yupao.model.request.TeamQuitRequest;
import com.xyz.yupao.model.request.TeamUpdateRequest;
import com.xyz.yupao.model.vo.TeamUserVO;
import com.xyz.yupao.service.TeamService;
import com.xyz.yupao.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/team")
@CrossOrigin(origins = {"http://localhost:3000/"}, allowCredentials = "true")
public class TeamController {
    @Resource
    private UserService userService;
    @Resource
    private TeamService teamService;

    /**
     * 添加队伍接口
     * @param teamAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Long> addTeam(@RequestBody TeamAddRequest teamAddRequest, HttpServletRequest request) {
        // 检验参数
        if (teamAddRequest == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        Team team = new Team();
        // 将请求体中的数据拷贝到 team 对象中
        BeanUtils.copyProperties(teamAddRequest,team);
        // 调用 service 层方法，添加队伍信息
        long teamId=teamService.addTeam(team,loginUser);
        return ResultUtils.success(teamId);
    }

    /**
     * 删除队伍接口
     *
     * @param id
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteTeam(@RequestBody long id) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean result = teamService.removeById(id);
        if (!result) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "删除失败");
        }
        return ResultUtils.success(true);
    }

    /**
     * 更新队伍接口
     *
     * @param teamUpdateRequest
     * @return
     */
    @PostMapping("/update")
    public BaseResponse<Boolean> updateTeam(@RequestBody TeamUpdateRequest teamUpdateRequest, HttpServletRequest request) {
        if (teamUpdateRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        boolean result = teamService.updateTeam(teamUpdateRequest,loginUser);
        if (!result) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "更新失败");
        }
        return ResultUtils.success(true);
    }

    /**
     * 根据 id 获取队伍信息接口
     *
     * @param id
     * @return
     */
    @PostMapping("/get")
    public BaseResponse<Team> getTeamById(@RequestBody long id) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Team team = teamService.getById(id);
        if (team == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        return ResultUtils.success(team);
    }

    /**
     * 查询队伍列表接口。
     * 根据条件查询数据库中符合条件的队伍列表。
     * @param teamQuery
     * @return
     */
    @GetMapping("/list")
    public BaseResponse<List<TeamUserVO>> listTeams(TeamQuery teamQuery,HttpServletRequest request){
        if (teamQuery==null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 判断是否为管理员
        boolean isAdmin = userService.isAdmin(request);
        // 执行查询操作
        List<TeamUserVO> teamList = teamService.listTeams(teamQuery, isAdmin);
        return ResultUtils.success(teamList);
    }

    @GetMapping("/list/page")
    public BaseResponse<Page<Team>> listTeamsByPage(TeamQuery teamQuery){
        if (teamQuery==null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 构造查询条件
        Team team = new Team();
        BeanUtils.copyProperties(teamQuery,team);
        Page<Team> page = new Page<>(teamQuery.getPageNum(), teamQuery.getPageSize());
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>(team);
        // 执行查询操作
        Page<Team> resultPage = teamService.page(page, queryWrapper);
        return ResultUtils.success(resultPage);
    }

    @PostMapping("/join")
    public BaseResponse<Boolean> joinTeam(@RequestBody TeamJoinRequest teamJoinRequest,HttpServletRequest request){
        // 参数校验
        if (teamJoinRequest==null) {
            throw new BusinessException(ErrorCode.NULL_ERROR,"加入队伍数据为空");
        }
        // 获取当前登录用户
        User loginUser = userService.getLoginUser(request);
        if (loginUser==null){
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        // 调用服务层付费处理加入队伍的请求
        boolean result=teamService.joinTeam(teamJoinRequest,loginUser);
        if (!result){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"加入队伍失败");
        }
        return ResultUtils.success(true);
    }

    /**
     * 退出队伍
     * @param teamQuitRequest
     * @param request
     * @return
     */
    @PostMapping("/quit")
    public BaseResponse<Boolean> quitTeam(@RequestBody TeamQuitRequest teamQuitRequest, HttpServletRequest request) {
        // 检验请求参数是否为空
        if (teamQuitRequest == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        // 获取当前用户信息
        User loginUser = userService.getLoginUser(request);
        // 调用业务层执行退出队伍操作
        boolean result = teamService.quitTeam(teamQuitRequest, loginUser);
        return ResultUtils.success(result);
    }
}
