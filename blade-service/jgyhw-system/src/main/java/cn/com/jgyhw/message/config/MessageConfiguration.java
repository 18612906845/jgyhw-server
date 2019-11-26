package cn.com.jgyhw.message.config;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 消息服务配置类
 *
 * Created by WangLei on 2019/11/20 0021 22:32
 */
@Configuration
@MapperScan({"cn.com.jgyhw.message.mapper.**"})
public class MessageConfiguration implements WebMvcConfigurer {

}
