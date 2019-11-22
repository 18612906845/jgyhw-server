package cn.com.jgyhw.token;

import org.springblade.common.constant.JgyhwConstant;
import org.springblade.core.launch.BladeApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 令牌服务启动类
 *
 * Created by WangLei on 2019/11/22 0022 23:09
 */
@SpringBootApplication
public class TokenApplication {

	public static void main(String[] args) {
		BladeApplication.run(JgyhwConstant.APPLICATION_TOKEN_NAME, TokenApplication.class, args);
	}
}
