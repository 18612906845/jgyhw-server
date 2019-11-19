package cn.com.jgyhw.mesage.config;
import org.mybatis.spring.annotation.MapperScan;
import org.springblade.core.secure.registry.SecureRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 配置类
 */
@Configuration
@MapperScan({"cn.com.jgyhw.message.mapper.**"})
public class MessageConfiguration implements WebMvcConfigurer {
    @Bean
    public SecureRegistry secureRegistry() {
        SecureRegistry secureRegistry = new SecureRegistry();
        secureRegistry.excludePathPatterns("/wxGzhMessage/**");
        return secureRegistry;
    }
}
