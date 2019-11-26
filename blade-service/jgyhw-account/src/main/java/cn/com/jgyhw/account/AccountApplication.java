package cn.com.jgyhw.account;

import org.springblade.common.constant.JgyhwConstant;
import org.springblade.core.launch.BladeApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 流水账本服务启动类
 *
 * Created by WangLei on 2019/11/23 0023 23:52
 */
@SpringBootApplication
@EnableFeignClients(basePackages = {"cn.com.jgyhw.user"})
public class AccountApplication {

	public static void main(String[] args) {
		BladeApplication.run(JgyhwConstant.APPLICATION_ACCOUNT_NAME, AccountApplication.class, args);
	}
}
