package cn.com.jgyhw.common.controller;

import lombok.extern.slf4j.Slf4j;
import org.springblade.core.tool.api.R;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 公共控制器
 *
 * Created by WangLei on 2019/11/30 0030 01:06
 */
@Slf4j
@RefreshScope
@RestController
@RequestMapping("/common")
public class CommonController {

	@Value("${jgyhw.system.newNotice}")
	private String newNotice;

	/**
	 * 查询最新公告
	 *
	 * @return
	 */
	@GetMapping("/findNewNotice")
	public R<String> findNewNotice(){
		return R.data(newNotice);
	}
}
