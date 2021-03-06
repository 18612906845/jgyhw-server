package cn.com.jgyhw.token.controller;

import cn.com.jgyhw.token.service.IWxTokenService;
import lombok.extern.slf4j.Slf4j;
import org.springblade.core.tool.api.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 微信令牌控制器
 *
 * Created by WangLei on 2019/11/22 0022 23:02
 */
@Slf4j
@RefreshScope
@RestController
@RequestMapping("/wxToken")
public class WxTokenController {

	@Autowired
	private IWxTokenService wxTokenService;

	/**
	 * 更新微信公众号ServiceApiToken
	 *
	 * @return
	 */
	@GetMapping("/updateWxGzhServiceApiToken")
	public R updateWxGzhServiceApiToken(){
		wxTokenService.timingUpdateWxGzhServiceApiToken();
		return R.status(true);
	}

	/**
	 * 更新微信小程序ServiceApiToken
	 *
	 * @return
	 */
	@GetMapping("/updateWxXcxServiceApiToken")
	public R updateWxXcxServiceApiToken(){
		wxTokenService.timingUpdateWxXcxServiceApiToken();
		return R.status(true);
	}
}
