package cn.com.jgyhw;

import org.springblade.common.constant.JgyhwConstant;
import org.springblade.core.launch.BladeApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 系统服务启动类
 *
 * @author wanglei@citms.cn
 * @date 2019/11/26 14:29
 */
@SpringCloudApplication
@EnableFeignClients(basePackages = {"cn.com.jgyhw"})
public class SystemApplication {

	public static void main(String[] args) {
		BladeApplication.run(JgyhwConstant.APPLICATION_SYSTEM_NAME, SystemApplication.class, args);
	}
}
