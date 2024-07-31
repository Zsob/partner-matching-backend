package com.xyz.yupao.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2WebMvc;


/**
 * @ClassName: yupao-backend
 * @Description: 自定义 Knife4j 接口文档的配置
 * 接口文档访问:http://localhost:8080/api/doc.html
 */
@Configuration
@EnableSwagger2WebMvc
@Profile({"dev", "test"})   //版本控制访问,限定配置仅在部分环境开启
public class Knife4jConfig {

    @Bean(value = "defaultApi2")
    public Docket defaultApi2() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                // 这里一定要标注你控制器的位置
                .apis(RequestHandlerSelectors.basePackage("com.xyz.yupao.controller"))
                .paths(PathSelectors.any())
                .build();
    }

    /**
     * api 信息
     * @return
     */
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("鱼泡伙伴匹配系统")
                .description("鱼泡伙伴匹配系统-接口文档")
                .termsOfServiceUrl("https://github.com/kongshier")
                .contact(new Contact("shier", "https://blog.csdn.net/qq_56098191?spm=1000.2115.3001.5343","2927527234@qq.com"))
                .version("1.0")
                .build();
    }
}