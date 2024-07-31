package com.xyz.yupao.service;

import com.xyz.yupao.model.domain.User;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

@SpringBootTest
@Slf4j
class UserServiceTest {
    @Resource
    private UserService userService;

    @Test
    public void testAdduser(){
        User user = new User();
        user.setUsername("dogYupi");
        user.setUserAccount("123");
        user.setAvatarUrl("https://profile-avatar.csdnimg.cn/07f521183eec40f287e751859b7f2b71_weixin_55549435.jpg!1");
        user.setGender(0);
        user.setUserPassword("123");
        user.setEmail("123@qq.com");
        user.setPhone("123456");
        boolean result = userService.save(user);
        System.out.println(user.getId());
        Assertions.assertTrue(result);
    }

    @Test
    void userRegister() {
        String userAccount="yupi";
        String userPassword="";
        String checkPassword="12345678";
        long result = userService.userRegister(userAccount, userPassword, checkPassword,"0");
        Assertions.assertEquals(-1,result);
        userAccount="yu";
        userPassword="12345678";
        result = userService.userRegister(userAccount, userPassword, checkPassword,null);
        userAccount="yu pi";
        result = userService.userRegister(userAccount, userPassword, checkPassword,"");
        Assertions.assertEquals(-1,result);
        checkPassword="123456789";
        result = userService.userRegister(userAccount, userPassword, checkPassword,"0");
        Assertions.assertEquals(-1,result);
        userAccount="dogYupi";
        userPassword="12345678";
        checkPassword="12345678";
        result = userService.userRegister(userAccount, userPassword, checkPassword,"");
        Assertions.assertEquals(-1,result);
        userAccount="yupi";
        result = userService.userRegister(userAccount, userPassword, checkPassword,"");
        Assertions.assertTrue(result>0);

    }

    @Test
    void testUserRegister1() {
        String userAccount="zhangsan";
        String userPassword="12345678";
        String checkPassword="12345678";
        long result = userService.userRegister(userAccount, userPassword, checkPassword,"0");
    }

    @Test
    void testSearchUsersByTags(){
        List<String> tagNameList = Arrays.asList("乒乓球");
        List<User> users = userService.searchUsersByTags(tagNameList);
        Assertions.assertNotNull(users);
    }
}