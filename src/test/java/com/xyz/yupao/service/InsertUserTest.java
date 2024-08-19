package com.xyz.yupao.service;

import com.xyz.yupao.mapper.UserMapper;
import com.xyz.yupao.model.domain.User;
import com.xyz.yupao.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.StopWatch;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

@SpringBootTest
public class InsertUserTest {
    static List<String> AvatarList = Arrays.asList(
            "https://pic1.zhimg.com/80/v2-ec3cb33d627617314932405c49b00754_720w.jpg",
            "https://pic1.zhimg.com/v2-ec3cb33d627617314932405c49b00754_b.jpg",
            "https://pic3.zhimg.com/80/v2-dba556b82805c26184944ef816b1fb76_720w.jpg",
            "https://pic3.zhimg.com/v2-dba556b82805c26184944ef816b1fb76_r.jpg",
            "https://pic3.zhimg.com/v2-dba556b82805c26184944ef816b1fb76_b.jpg",
            "https://pic4.zhimg.com/80/v2-e0a37d9359f11922993a591a0a485177_720w.jpg",
            "https://pic4.zhimg.com/v2-e0a37d9359f11922993a591a0a485177_r.jpg",
            "https://pic4.zhimg.com/v2-e0a37d9359f11922993a591a0a485177_b.jpg",
            "https://pic4.zhimg.com/80/v2-d98ba30c38bb30a3968c807c9fac9203_720w.jpg",
            "https://pic4.zhimg.com/v2-d98ba30c38bb30a3968c807c9fac9203_r.jpg",
            "https://pic4.zhimg.com/v2-d98ba30c38bb30a3968c807c9fac9203_b.jpg",
            "https://pic1.zhimg.com/80/v2-2bc4d2fd0e3ec4d945394028c8ce8980_720w.jpg",
            "https://pic1.zhimg.com/v2-2bc4d2fd0e3ec4d945394028c8ce8980_r.jpg",
            "https://pic1.zhimg.com/v2-2bc4d2fd0e3ec4d945394028c8ce8980_b.jpg",
            "https://pic3.zhimg.com/80/v2-d0306e70488f6f225c87442d98797682_720w.jpg",
            "https://pic3.zhimg.com/v2-d0306e70488f6f225c87442d98797682_r.jpg",
            "https://pic3.zhimg.com/v2-d0306e70488f6f225c87442d98797682_b.jpg",
            "https://pic2.zhimg.com/80/v2-8eaf3047d484eefce83956cca2c55429_720w.jpg",
            "https://pic2.zhimg.com/v2-8eaf3047d484eefce83956cca2c55429_b.jpg",
            "https://pic2.zhimg.com/80/v2-35b20bd3d0667f0a4ab2a81ff14d9289_720w.jpg",
            "https://pic2.zhimg.com/v2-35b20bd3d0667f0a4ab2a81ff14d9289_b.jpg",
            "https://pic3.zhimg.com/80/v2-e54cd5b1379ea09779d3863799143222_720w.jpg",
            "https://pic3.zhimg.com/v2-e54cd5b1379ea09779d3863799143222_r.jpg",
            "https://pic3.zhimg.com/v2-e54cd5b1379ea09779d3863799143222_b.jpg",
            "https://pic3.zhimg.com/80/v2-58a7f2b0e8779a3a30a4eb4bfd5d24d2_720w.jpg",
            "https://pic3.zhimg.com/v2-58a7f2b0e8779a3a30a4eb4bfd5d24d2_b.jpg",
            "https://pic3.zhimg.com/80/v2-c60835e0217d17b10016fe3ca0a85a22_720w.jpg",
            "https://pic3.zhimg.com/v2-c60835e0217d17b10016fe3ca0a85a22_r.jpg",
            "https://pic3.zhimg.com/v2-c60835e0217d17b10016fe3ca0a85a22_b.jpg",
            "https://pic3.zhimg.com/80/v2-6c66eeac778597275ac50b10ff20f0da_720w.jpg",
            "https://pic3.zhimg.com/v2-6c66eeac778597275ac50b10ff20f0da_r.jpg",
            "https://pic3.zhimg.com/v2-6c66eeac778597275ac50b10ff20f0da_b.jpg",
            "https://pic4.zhimg.com/80/v2-e81448887e1a5aaa5d7a23c59254b1ff_720w.jpg",
            "https://pic4.zhimg.com/v2-e81448887e1a5aaa5d7a23c59254b1ff_r.jpg",
            "https://pic4.zhimg.com/v2-e81448887e1a5aaa5d7a23c59254b1ff_b.jpg",
            "https://pic2.zhimg.com/80/v2-bed439ff53b5cc854445e58c85e37c45_720w.jpg",
            "https://pic2.zhimg.com/v2-bed439ff53b5cc854445e58c85e37c45_r.jpg",
            "https://pic2.zhimg.com/v2-bed439ff53b5cc854445e58c85e37c45_b.jpg",
            "https://pic2.zhimg.com/80/v2-b17d0a226c18cce567612589d5f1fcc9_720w.jpg",
            "https://pic2.zhimg.com/v2-b17d0a226c18cce567612589d5f1fcc9_b.jpg",
            "https://pic2.zhimg.com/80/v2-0783cd2406b2d8fd4d595a2ff35463e9_720w.jpg",
            "https://pic2.zhimg.com/v2-0783cd2406b2d8fd4d595a2ff35463e9_r.jpg",
            "https://pic2.zhimg.com/v2-0783cd2406b2d8fd4d595a2ff35463e9_b.jpg",
            "https://pic2.zhimg.com/80/v2-30675d7656fcb8e2cc959cedd8b8db19_720w.jpg",
            "https://pic2.zhimg.com/v2-30675d7656fcb8e2cc959cedd8b8db19_r.jpg",
            "https://pic2.zhimg.com/v2-30675d7656fcb8e2cc959cedd8b8db19_b.jpg",
            "https://pic2.zhimg.com/80/v2-8a4187aa69c586954db7fb1ee79dc345_720w.jpg",
            "https://pic2.zhimg.com/v2-8a4187aa69c586954db7fb1ee79dc345_r.jpg",
            "https://pic2.zhimg.com/v2-8a4187aa69c586954db7fb1ee79dc345_b.jpg",
            "https://pic2.zhimg.com/80/v2-74720ab28fef03812230b44c689b1075_720w.jpg",
            "https://pic2.zhimg.com/v2-74720ab28fef03812230b44c689b1075_r.jpg",
            "https://pic2.zhimg.com/v2-74720ab28fef03812230b44c689b1075_b.jpg",
            "https://pic2.zhimg.com/80/v2-02f9c90b9f573e814ce413d8446098b5_720w.jpg",
            "https://pic2.zhimg.com/v2-02f9c90b9f573e814ce413d8446098b5_r.jpg",
            "https://pic2.zhimg.com/v2-02f9c90b9f573e814ce413d8446098b5_b.jpg",
            "https://pic4.zhimg.com/80/v2-0dbc479a4fa0f130a1622e36b9f43057_720w.jpg",
            "https://pic4.zhimg.com/v2-0dbc479a4fa0f130a1622e36b9f43057_r.jpg",
            "https://pic4.zhimg.com/v2-0dbc479a4fa0f130a1622e36b9f43057_b.jpg",
            "https://pic2.zhimg.com/80/v2-88e534dacda86fd9f46e9af91cd6b9cd_720w.jpg",
            "https://pic2.zhimg.com/v2-88e534dacda86fd9f46e9af91cd6b9cd_r.jpg",
            "https://pic2.zhimg.com/v2-88e534dacda86fd9f46e9af91cd6b9cd_b.jpg",
            "https://pic1.zhimg.com/80/v2-eaa8ad47d2133c60ffcb150449e75050_720w.jpg",
            "https://pic1.zhimg.com/v2-eaa8ad47d2133c60ffcb150449e75050_r.jpg",
            "https://pic1.zhimg.com/v2-eaa8ad47d2133c60ffcb150449e75050_b.jpg",
            "https://pic2.zhimg.com/80/v2-ec924aa1d54c3d9c70020f3538b4c909_720w.jpg",
            "https://pic2.zhimg.com/v2-ec924aa1d54c3d9c70020f3538b4c909_r.jpg",
            "https://pic2.zhimg.com/v2-ec924aa1d54c3d9c70020f3538b4c909_b.jpg",
            "https://pic1.zhimg.com/80/v2-4e7f93275a2f7100bebdd93ae1e4188c_720w.jpg",
            "https://pic1.zhimg.com/v2-4e7f93275a2f7100bebdd93ae1e4188c_r.jpg",
            "https://pic1.zhimg.com/v2-4e7f93275a2f7100bebdd93ae1e4188c_b.jpg",
            "https://pic2.zhimg.com/80/v2-2fe100236cfafb9eb49862fc88dc6555_720w.jpg",
            "https://pic2.zhimg.com/v2-2fe100236cfafb9eb49862fc88dc6555_r.jpg",
            "https://pic2.zhimg.com/v2-2fe100236cfafb9eb49862fc88dc6555_b.jpg",
            "https://pic2.zhimg.com/80/v2-b8558d474c2787339b9f0f84d08485a9_720w.jpg",
            "https://pic2.zhimg.com/v2-b8558d474c2787339b9f0f84d08485a9_r.jpg",
            "https://pic2.zhimg.com/v2-b8558d474c2787339b9f0f84d08485a9_b.jpg",
            "https://pic2.zhimg.com/80/v2-4ad61f2b08f6597e8fe52c85c5fc1459_720w.jpg",
            "https://pic2.zhimg.com/v2-4ad61f2b08f6597e8fe52c85c5fc1459_r.jpg",
            "https://pic2.zhimg.com/v2-4ad61f2b08f6597e8fe52c85c5fc1459_b.jpg",
            "https://pic1.zhimg.com/80/v2-fd46e99776da5960ce378cb73da2ec08_720w.jpg",
            "https://pic1.zhimg.com/v2-fd46e99776da5960ce378cb73da2ec08_r.jpg",
            "https://pic1.zhimg.com/v2-fd46e99776da5960ce378cb73da2ec08_b.jpg",
            "https://pic3.zhimg.com/80/v2-831534cc60bb87d1ad15740b75ca08ce_720w.jpg",
            "https://pic3.zhimg.com/v2-831534cc60bb87d1ad15740b75ca08ce_r.jpg",
            "https://pic3.zhimg.com/v2-831534cc60bb87d1ad15740b75ca08ce_b.jpg",
            "https://pic4.zhimg.com/80/v2-de55719e7490e7820fee0e1cb284d23b_720w.jpg",
            "https://pic4.zhimg.com/v2-de55719e7490e7820fee0e1cb284d23b_r.jpg",
            "https://pic4.zhimg.com/v2-de55719e7490e7820fee0e1cb284d23b_b.jpg",
            "https://pic2.zhimg.com/80/v2-f24cf7eb84a5a0d6e82d951e29638a51_720w.jpg",
            "https://pic2.zhimg.com/v2-f24cf7eb84a5a0d6e82d951e29638a51_r.jpg",
            "https://pic2.zhimg.com/v2-f24cf7eb84a5a0d6e82d951e29638a51_b.jpg",
            "https://pic2.zhimg.com/80/v2-e8131228d463dd5a356f383bdf5d790d_720w.jpg",
            "https://pic2.zhimg.com/v2-e8131228d463dd5a356f383bdf5d790d_r.jpg",
            "https://pic2.zhimg.com/v2-e8131228d463dd5a356f383bdf5d790d_b.jpg",
            "https://pic1.zhimg.com/80/v2-e4e7a4c53f472eb44f2da9925c5b5068_720w.jpg",
            "https://pic1.zhimg.com/v2-e4e7a4c53f472eb44f2da9925c5b5068_b.jpg",
            "https://pic3.zhimg.com/80/v2-3f8e3fa55ea6422761f182db3af5ec52_720w.jpg",
            "https://pic3.zhimg.com/v2-3f8e3fa55ea6422761f182db3af5ec52_r.jpg",
            "https://pic3.zhimg.com/v2-3f8e3fa55ea6422761f182db3af5ec52_b.jpg",
            "https://pic4.zhimg.com/80/v2-d6ecd30919248e10b6bc07a22ad87b4b_720w.jpg",
            "https://pic4.zhimg.com/v2-d6ecd30919248e10b6bc07a22ad87b4b_r.jpg",
            "https://pic4.zhimg.com/v2-d6ecd30919248e10b6bc07a22ad87b4b_b.jpg",
            "https://pic4.zhimg.com/80/v2-03710dd24aeb41d46acaeb4c070fc7fb_720w.jpg",
            "https://pic4.zhimg.com/v2-03710dd24aeb41d46acaeb4c070fc7fb_r.jpg",
            "https://pic4.zhimg.com/v2-03710dd24aeb41d46acaeb4c070fc7fb_b.jpg",
            "https://pic3.zhimg.com/80/v2-609361dbebe6e9b2dd928a22656235b2_720w.jpg",
            "https://pic3.zhimg.com/v2-609361dbebe6e9b2dd928a22656235b2_r.jpg",
            "https://pic3.zhimg.com/v2-609361dbebe6e9b2dd928a22656235b2_b.jpg",
            "https://pic3.zhimg.com/80/v2-32ff350ea2e641d0775911e3b950238e_720w.jpg",
            "https://pic3.zhimg.com/v2-32ff350ea2e641d0775911e3b950238e_r.jpg",
            "https://pic3.zhimg.com/v2-32ff350ea2e641d0775911e3b950238e_b.jpg",
            "https://pic4.zhimg.com/80/v2-0b5910edd9b149ef33c3b9447b9e15ef_720w.jpg",
            "https://pic4.zhimg.com/v2-0b5910edd9b149ef33c3b9447b9e15ef_r.jpg",
            "https://pic4.zhimg.com/v2-0b5910edd9b149ef33c3b9447b9e15ef_b.jpg",
            "https://pic1.zhimg.com/80/v2-490796a9728cf9aa0e59eeca44bd92f8_720w.jpg",
            "https://pic1.zhimg.com/v2-490796a9728cf9aa0e59eeca44bd92f8_r.jpg"
    );

    @Resource
    private UserMapper userMapper;
    @Resource
    private UserService userService;

    /**
     * 执行用户数据的循环插入操作。
     * 创建了?个用户实例，并逐一插入数据库中。
     * 批量插入执行时间（毫秒）：5000条-64928  10000条-142429
     */
    @Test
    public void doInsertUser() {
        int size = AvatarList.size();
        Random random = new Random();
        int randomNumber;
        StopWatch stopWatch = new StopWatch();
        stopWatch.start(); // 开始计时
        final int INSERT_NUM = 10000; // 定义插入的用户数量
        for (int i = 0; i < INSERT_NUM; i++) {
            User user = new User();
            // 配置用户信息
            randomNumber = random.nextInt(size);
            user.setUsername("小黑子" + randomNumber + "号");
            user.setUserAccount("xiaoheizi" + randomNumber);
            user.setAvatarUrl(AvatarList.get(randomNumber));
            user.setProfile("你干嘛");
            user.setGender(0);
            user.setUserPassword("12345678");
            user.setPhone("12345678901");
            user.setEmail("ikun" + randomNumber + "@qq.com");
            user.setUserStatus(0);
            user.setUserRole(0);
            user.setPlanetCode("123");
            user.setTags("[]");
            userMapper.insert(user); // 插入用户
        }
        stopWatch.stop(); // 停止计时
        // 输出插入操作所需的时间
        System.out.println("循环插入执行时间（毫秒）：" + stopWatch.getLastTaskTimeMillis());
    }

    /**
     * 执行用户数据的批量插入操作，使用批处理以提高性能。
     * 创建了5000个用户实例并将它们添加到列表中，最后通过批处理方法插入数据库。
     * 这种方法减少了数据库交互次数，从而显著提高了性能。
     * 批量插入执行时间（毫秒）：2059
     */
    @Test
    public void doBatchInsertUser() {
        int size = AvatarList.size();
        Random random = new Random();
        int randomNumber;
        StopWatch stopWatch = new StopWatch();
        stopWatch.start(); // 开始计时
        final int INSERT_NUM = 5000; // 定义插入的用户数量
        List<User> userList = new ArrayList<>();
        for (int i = 0; i < INSERT_NUM; i++) {
            User user = new User();
            // 配置用户信息
            randomNumber = random.nextInt(size);
            user.setUsername("小黑子" + randomNumber + "号");
            user.setUserAccount("xiaoheizi" + randomNumber);
            user.setAvatarUrl(AvatarList.get(randomNumber));
            user.setProfile("你干嘛");
            user.setGender(0);
            user.setUserPassword("12345678");
            user.setPhone("12345678901");
            user.setEmail("ikun" + randomNumber + "@qq.com");
            user.setUserStatus(0);
            user.setUserRole(0);
            user.setPlanetCode("123");
            user.setTags("[]");
            userList.add(user);
        }
        userService.saveBatch(userList, 100);// 使用批处理每次提交100个用户
        stopWatch.stop(); // 停止计时
        // 输出插入操作所需的时间
        System.out.println("批量插入执行时间（毫秒）：" + stopWatch.getLastTaskTimeMillis());
    }

    /**
     * 使用并发方法批量插入用户数据。
     * 通过分割任务并利用CompletableFuture与线程池，实现高效的并行数据插入。
     * 此方法尤其适用于处理大量数据插入操作，能显著提高性能。
     * batchSize=5000：并发批量插入执行时间（毫秒）：2603
     * batchSize=500：并发批量插入执行时间（毫秒）：2381
     * 100000-9682ms-9.6秒
     */
    // 线程池的设置
    private ExecutorService executorService = new ThreadPoolExecutor(
            100, // corePoolSize: 核心线程数。线程池中始终保持活跃的线程数量，即使它们处于空闲状态。
            1000, // maximumPoolSize: 最大线程数。线程池中允许的最大线程数量。
            10000, // keepAliveTime: 当线程数超过核心线程数时，这是非核心线程空闲前的最大存活时间。
            TimeUnit.MINUTES, // 时间单位。上面的 keepAliveTime 的单位。
            new ArrayBlockingQueue<>(10000) // 工作队列。存放待执行任务的阻塞队列，具有先进先出等特性的阵列支持的有界队列。
    );

    @Test
    public void doConcurrencyInsertUser() {
        int size = AvatarList.size();
        Random random = new Random();
        int randomNumber;
        StopWatch stopWatch = new StopWatch();
        final int INSERT_NUM=100000; // 总插入数据量
        final int batchSize=1000; // 每批次处理的数据量
        stopWatch.start(); // 开始计时
        List<CompletableFuture<Void>> futureList=new ArrayList<>();
        for (int i = 0; i < Math.ceil((double) INSERT_NUM/batchSize); i++) {
            List<User> userList = new ArrayList<>();
            // 创建每批次的用户数据
            for (int j = 0; j < batchSize; j++) {
                User user = new User();
                // 配置用户信息
                randomNumber = random.nextInt(size);
                user.setUsername("小黑子" + randomNumber + "号");
                user.setUserAccount("xiaoheizi" + randomNumber);
                user.setAvatarUrl(AvatarList.get(randomNumber));
                user.setProfile("你干嘛");
                user.setGender(0);
                user.setUserPassword("12345678");
                user.setPhone("12345678901");
                user.setEmail("ikun" + randomNumber + "@qq.com");
                user.setUserStatus(0);
                user.setUserRole(0);
                user.setPlanetCode("123");
                user.setTags("[]");
                userList.add(user);
            }
            // 异步执行数据库插入操作
            CompletableFuture<Void> future=CompletableFuture.runAsync(()->{
                System.out.println("ThreadName:"+Thread.currentThread().getName());
                userService.saveBatch(userList,batchSize);
            },executorService);
            futureList.add(future);
        }
        // 等待所有异步任务完成
        CompletableFuture.allOf(futureList.toArray(new CompletableFuture[0])).join();
        stopWatch.stop(); // 停止计时
        // 输出插入操作所需的时间
        System.out.println("并发批量插入执行时间（毫秒）：" + stopWatch.getLastTaskTimeMillis());
    }

}
