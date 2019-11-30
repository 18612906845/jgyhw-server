package cn.com.jgyhw.goods;

import org.springblade.common.constant.JgyhwConstant;
import org.springblade.core.launch.BladeApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 商品服务启动类
 *
 * Created by WangLei on 2019/11/21 0021 22:32
 */
@SpringBootApplication
@EnableFeignClients(basePackages = {"cn.com.jgyhw"})
public class GoodsApplication {

	public static void main(String[] args) {
		BladeApplication.run(JgyhwConstant.APPLICATION_GOODS_NAME, GoodsApplication.class, args);
	}
}
