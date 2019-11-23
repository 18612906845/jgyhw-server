package cn.com.jgyhw.order;

import org.springblade.common.constant.JgyhwConstant;
import org.springblade.core.launch.BladeApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 订单服务启动类
 *
 * Created by WangLei on 2019/11/23 0023 21:39
 */
@SpringBootApplication
@EnableFeignClients(basePackages = {"org.springblade.system.user", "cn.com.jgyhw.goods"})
public class OrderApplication {

	public static void main(String[] args) {
		BladeApplication.run(JgyhwConstant.APPLICATION_ORDER_NAME, OrderApplication.class, args);
	}
}
