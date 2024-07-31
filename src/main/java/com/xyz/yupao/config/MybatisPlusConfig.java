package com.xyz.yupao.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *  MyBatis Plus配置类，用于配置数据库相关的中间件功能，如分页。
 */
@Configuration
@MapperScan("com.xyz.yupao.mapper") // 适当调整以匹配你的Mapper组件的包路径
public class MybatisPlusConfig {

    /**
     * 配置MyBatis Plus的分页插件。
     * @return 返回配置了分页功能的MybatisPlusInterceptor。
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        // 添加针对MySQL数据库的分页拦截器，自动处理分页逻辑
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }
}
