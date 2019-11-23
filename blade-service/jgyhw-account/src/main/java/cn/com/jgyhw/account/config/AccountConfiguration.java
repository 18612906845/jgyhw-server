package cn.com.jgyhw.account.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 流水账目配置类
 *
 * Created by WangLei on 2019/11/24 0024 02:03
 */
@Configuration
@MapperScan({"cn.com.jgyhw.account.mapper.**"})
public class AccountConfiguration implements WebMvcConfigurer {

}
