package cn.com.jgyhw.user.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 用户服务配置类
 *
 * @author wanglei@citms.cn
 * @date 2019/11/26 14:34
 */
@Configuration
@MapperScan({"cn.com.jgyhw.user.mapper.**"})
public class UserConfiguration implements WebMvcConfigurer {

}
