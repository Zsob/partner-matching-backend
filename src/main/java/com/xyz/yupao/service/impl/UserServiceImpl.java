package com.xyz.yupao.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xyz.yupao.common.ErrorCode;
import com.xyz.yupao.exception.BusinessException;
import com.xyz.yupao.model.domain.User;
import com.xyz.yupao.service.UserService;
import com.xyz.yupao.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.xyz.yupao.constant.UserConstant.ADMIN_ROLE;
import static com.xyz.yupao.constant.UserConstant.USER_LOGIN_STATE;

/**
 * @author 74703
 * @description 针对表【user(用户表)】的数据库操作Service实现
 * @createDate 2024-06-08 17:14:42
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService {
    /**
     * 盐值，混淆密码
     */
    private static final String SALT = "yupi";

    @Resource
    private UserMapper userMapper;
    @Resource
    private RedisTemplate<String,Object> redisTemplate;

    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword, String planetCode) {
        //对数据进行校验
        //1.校验非空
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "输入数据为空");
        }
        //2.账户长度不小于4位
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户账户过短");
        }
        //3.密码不小于8位
        if (userPassword.length() < 8 || checkPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户密码过短");
        }
        //4.账户不能包含特殊字符
        Pattern compile = Pattern.compile(".*[[ _`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]|\\n|\\r|\\t].*");
        Matcher matcher = compile.matcher(userAccount);
        if (matcher.matches()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账户不能包含特殊字符");
        }
        //5.密码与确认密码相同
        if (!userPassword.equals(checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码与确认密码相同");
        }
        //6.账户不能重复
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("userAccount", userAccount);
        Long count = userMapper.selectCount(userQueryWrapper);
        if (count > 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账户重复");
        }


        //密码加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());

        //插入数据
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(encryptPassword);
        //保存星球编号
        if (!StringUtils.isAnyBlank(planetCode)) {
            user.setPlanetCode(planetCode);
        }
        boolean saveResult = this.save(user);
        if (!saveResult) {
            return -1;
        }
        return user.getId();
    }

    @Override
    public User userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        //对数据进行校验
        //1.校验非空
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        //2.账户长度不小于4位
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户账户过短");
        }
        //3.密码不小于8位
        if (userPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户密码过短");
        }
        //4.账户不能包含特殊字符
        Pattern compile = Pattern.compile(".*[[ _`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]|\\n|\\r|\\t].*");
        Matcher matcher = compile.matcher(userAccount);
        if (matcher.matches()) {
            return null;
        }
        //密码加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());

        //查询用户是否存在
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("userAccount", userAccount);
        userQueryWrapper.eq("userPassword", encryptPassword);
        User user = userMapper.selectOne(userQueryWrapper);
        //用户不存在
        if (user == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账户或密码错误");
        }

        //用户数据脱敏
        User safetyUser = getSafetyUser(user);

        //记录用户登录状态
        request.getSession().setAttribute(USER_LOGIN_STATE, safetyUser);
        return safetyUser;
    }

    @Override
    public int userLogout(HttpServletRequest request) {
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        return 1;
    }

    @Override
    public User getSafetyUser(User user) {
        if (user == null) {
            return null;
        }
        User safetyUser = new User();
        safetyUser.setId(user.getId());
        safetyUser.setUsername(user.getUsername());
        safetyUser.setUserAccount(user.getUserAccount());
        safetyUser.setAvatarUrl(user.getAvatarUrl());
        safetyUser.setGender(user.getGender());
        safetyUser.setEmail(user.getEmail());
        safetyUser.setUserStatus(user.getUserStatus());
        safetyUser.setPhone(user.getPhone());
        safetyUser.setCreateTime(user.getCreateTime());
        safetyUser.setUserRole(user.getUserRole());
        safetyUser.setUserStatus(user.getUserStatus());
        safetyUser.setPlanetCode(user.getPlanetCode());
        safetyUser.setProfile(user.getProfile());
        safetyUser.setTags(user.getTags());
        return safetyUser;
    }

    @Override
    public List<User> searchUsersByTags(List<String> tagNameList) {
        //return searchUsersByTagsThroughSQL(tagNameList);
        return searchUsersByTagsThroughMemory(tagNameList);
    }

    @Override
    public boolean isAdmin(HttpServletRequest request) {
        //仅管理员可查询
        User user = (User) request.getSession().getAttribute(USER_LOGIN_STATE);
        if (null == user || user.getUserRole() != ADMIN_ROLE) {
            return false;
        }
        return true;
    }

    @Override
    public boolean isAdmin(User loginUser) {
        return loginUser.getUserRole() == ADMIN_ROLE;
    }

    @Override
    public User getLoginUser(HttpServletRequest request) {
        if (request == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User currentUser=(User) userObj;
        if (currentUser==null){
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        Long id = currentUser.getId();
        User newCurrentUser = this.getById(id);
        if (newCurrentUser==null){
            throw new BusinessException(ErrorCode.NULL_ERROR,"无法获取当前用户信息");
        }
        return this.getSafetyUser(newCurrentUser);
    }

    @Override
    public int updateUser(User user, User loginUser) {
        long userId = user.getId();
        if (userId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 校验权限
        if (!isAdmin(loginUser) && userId!=loginUser.getId()){
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        User oldUser = this.getById(userId);
        if (oldUser==null){
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        // 触发更新
        return this.baseMapper.updateById(user);
    }

    @Override
    public Page<User> getRecommendUsers(long pageNum, long pageSize, HttpServletRequest request) {
        String redisKey= null;
        try {
            // 获取当前登录用户
            User loginUser = this.getLoginUser(request);
            // 格式化Redis的key，以用户ID作为唯一标识
            redisKey = String.format("yupao:user:recommend:%s",loginUser.getId());
        } catch (Exception e) {
            // 当前用户未登录
            redisKey = "yupao:user:recommend:0";
        }
        // 尝试从Redis中获取缓存的用户分页数据
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
        Page<User> userPage = (Page<User>) valueOperations.get(redisKey);
        if (userPage!=null) {
            // 如果存在，则直接返回
            return userPage;
        }
        // 如果不存在，则查询数据库
        userPage = this.page(new Page<>(pageNum, pageSize), null);
        // 将数据库中查到的信息放入Redis，并设置过期时间
        try {
            valueOperations.set(redisKey,userPage,180, TimeUnit.MINUTES);
        } catch (Exception e) {
            log.error("redis set key error",e);
        }
        // 返回得到的用户列表
        return userPage;
    }


    /**
     * 通过内存筛选满足标签的用户
     *
     * @param tagNameList
     * @return
     */
    private List<User> searchUsersByTagsThroughMemory(List<String> tagNameList) {
        //long start = System.currentTimeMillis();
        //1. 检查标签列表是否为空
        if (CollectionUtils.isEmpty(tagNameList)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //2. 查询所有用户
        List<User> userList = userMapper.selectList(null);
        //3. 在内存中过滤不符合要求的用户，利用Gson将标签Json解析为集合
        Gson gson = new Gson();
        List<User> resultList = userList.stream()
                .filter(user -> {
                    String tagsStr = user.getTags();
                    if (StringUtils.isBlank(tagsStr)) {
                        return false;
                    }
                    //将Json字符串解析为String标签集合
                    Set<String> tmpTagNameSet = gson.fromJson(tagsStr, new TypeToken<Set<String>>() {
                    }.getType());
                    // Java8特性：利用 Optional 判空，确保 tmpTagNameSet 不是空值，避免了空指针异常
                    tmpTagNameSet = Optional.ofNullable(tmpTagNameSet).orElse(new HashSet<>());
                    //4. 如果用户标签集合不包含所有目标标签，则过滤用户
                    for (String tagName : tagNameList) {
                        if (!tmpTagNameSet.contains(tagName)) {
                            return false;
                        }
                    }
                    return true;
                })
                //5. 将符合条件的用户转化为safetyUser对象
                .map(this::getSafetyUser)
                //6. 收集符合条件的用户信息
                .collect(Collectors.toList());
        //long end = System.currentTimeMillis();
        //log.info("内存查询耗时：" + (end - start));
        return resultList;
    }

    /**
     * 通过 SQL 语句筛选满足标签的用户
     *
     * @param tagNameList
     * @return
     */
    @Deprecated
    private List<User> searchUsersByTagsThroughSQL(List<String> tagNameList) {
        //long start = System.currentTimeMillis();
        //1. 检查标签是否为空
        if (CollectionUtils.isEmpty(tagNameList)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //2. 初始化查询包装器
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        //3. 使用标签列表的每一个标签进行模糊查询
        for (String tagName : tagNameList) {
            queryWrapper.like("tags", tagName);
        }
        //4. 从数据库中更具查询条件返回数据列表
        List<User> userList = userMapper.selectList(queryWrapper);
        //long end = System.currentTimeMillis();
        //log.info("SQL查询耗时：" + (end - start));
        //5. 利用流式处理返回安全对象信息
        return userList.stream().map(this::getSafetyUser).collect(Collectors.toList());
    }


}




