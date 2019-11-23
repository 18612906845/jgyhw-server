package cn.com.jgyhw.order.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 订单服务配置类
 *
 * Created by WangLei on 2019/11/24 0024 01:55
 */
@Configuration
@MapperScan({"cn.com.jgyhw.order.mapper.**"})
public class OrderConfiguration implements WebMvcConfigurer {
}
