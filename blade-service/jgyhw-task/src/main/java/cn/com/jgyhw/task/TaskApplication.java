package cn.com.jgyhw.task;

import org.springblade.common.constant.JgyhwConstant;
import org.springblade.core.launch.BladeApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 任务服务启动类
 *
 * Created by WangLei on 2019/11/22 0022 23:09
 */
@SpringBootApplication
public class TaskApplication {

	public static void main(String[] args) {
		BladeApplication.run(JgyhwConstant.APPLICATION_TASK_NAME, TaskApplication.class, args);
	}
}
