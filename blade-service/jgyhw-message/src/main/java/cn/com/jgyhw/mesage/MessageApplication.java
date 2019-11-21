package cn.com.jgyhw.mesage;

import org.springblade.common.constant.JgyhwConstant;
import org.springblade.core.launch.BladeApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 消息服务启动类
 *
 * Created by WangLei on 2019/11/20 0021 22:32
 */
@SpringBootApplication
public class MessageApplication {

	public static void main(String[] args) {
		BladeApplication.run(JgyhwConstant.APPLICATION_MESSAGE_NAME, MessageApplication.class, args);
    }

}
