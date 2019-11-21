package cm.com.jgyhw.goods.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 商品服务配置类
 *
 * Created by WangLei on 2019/11/21 0021 22:36
 */
@Configuration
@MapperScan({"cn.com.jgyhw.goods.mapper.**"})
public class GoodsConfiguration implements WebMvcConfigurer {

}
