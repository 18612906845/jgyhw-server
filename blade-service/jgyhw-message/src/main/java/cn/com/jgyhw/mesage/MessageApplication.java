package cn.com.jgyhw.mesage;

import org.springblade.common.constant.JgyhwConstant;
import org.springblade.core.launch.BladeApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MessageApplication {

	public static void main(String[] args) {
		BladeApplication.run(JgyhwConstant.APPLICATION_MESSAGE_NAME, MessageApplication.class, args);
    }

}
