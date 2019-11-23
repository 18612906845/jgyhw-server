package cn.com.jgyhw.mesage;

import org.springblade.common.constant.JgyhwConstant;
import org.springblade.core.launch.BladeApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 消息服务启动类
 *
 * Created by WangLei on 2019/11/20 0021 22:32
 */
@SpringBootApplication
@EnableFeignClients(basePackages = {"org.springblade.system.user", "cn.com.jgyhw.goods"})
public class MessageApplication {

	public static void main(String[] args) {
		BladeApplication.run(JgyhwConstant.APPLICATION_MESSAGE_NAME, MessageApplication.class, args);
    }

}
