package cn.com.jgyhw.task.controller;

import cn.com.jgyhw.task.service.IWxTokenService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiOperationSupport;
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
@Api(value = "微信令牌", tags = "微信令牌")
public class WxTokenController {

	@Autowired
	private IWxTokenService wxTokenService;

	/**
	 * 更新微信公众号ServiceApiToken
	 *
	 * @return
	 */
	@GetMapping("/updateWxGzhServiceApiToken")
	@ApiOperationSupport(order = 1)
	@ApiOperation(value = "更新微信公众号ServiceApiToken", notes = "")
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
	@ApiOperationSupport(order = 2)
	@ApiOperation(value = "更新微信小程序ServiceApiToken", notes = "")
	public R updateWxXcxServiceApiToken(){
		wxTokenService.timingUpdateWxXcxServiceApiToken();
		return R.status(true);
	}
}
