package com.xyz.yupao.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xyz.yupao.model.domain.User;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
* @author 74703
* @description 针对表【user(用户表)】的数据库操作Service
* @createDate 2024-06-08 17:14:42
*/
public interface UserService extends IService<User> {


    /**
     * 用户注册
     * @param userAccount 用户账号
     * @param userPassword 用户密码
     * @param checkPassword 校验密码
     * @return 新用户id
     */

    long userRegister(String userAccount, String userPassword, String checkPassword, String planetCode);

    /**
     * 用户登录
     * @param userAccount
     * @param userPassword
     * @param request
     * @return
     */
    User userLogin(String userAccount, String userPassword, HttpServletRequest request);

    /**
     * 用户注销
     * @param request
     * @return
     */
    int userLogout(HttpServletRequest request);

    /**
     * 数据脱敏
     * @param user
     * @return
     */
    User getSafetyUser(User user);

    /**
     * 根据标签查找用户
     * @param tagNameList
     * @return
     */
    List<User> searchUsersByTags(List<String> tagNameList);

    /**
     * 判断是否为管理员
     * @param request
     * @return
     */
    boolean isAdmin(HttpServletRequest request);

    /**
     * 是否为管理员
     * @param loginUser
     * @return
     */
    boolean isAdmin(User loginUser);

    /**
     * 获取当前用户
     * @param request
     * @return
     */
    User getLoginUser(HttpServletRequest request);

    /**
     * 更新用户信息
     * @param user 需要更新的用户数据
     * @param loginUser 当前用户信息
     * @return
     */
    int updateUser(User user,User loginUser);

    /**
     * 获取推荐用户
     * @param pageNum
     * @param pageSize
     * @param request
     * @return
     */
    Page<User> getRecommendUsers(long pageNum, long pageSize, HttpServletRequest request);

    /**
     * 获取与当前登录用户相似的用户列表
     * @param num
     * @param loginUser
     * @return
     */
    List<User> matchUsers(long num, User loginUser);
}
