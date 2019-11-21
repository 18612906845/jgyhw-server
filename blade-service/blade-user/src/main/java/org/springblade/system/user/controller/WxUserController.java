package org.springblade.system.user.controller;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiOperationSupport;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springblade.core.tool.api.R;
import org.springblade.system.user.entity.WxUser;
import org.springblade.system.user.service.IWxUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 微信用户控制器
 *
 * Created by WangLei on 2019/11/21 0021 22:02
 */
@Slf4j
@RefreshScope
@RestController
@RequestMapping("/wxUser")
@Api(value = "微信用户", tags = "微信用户")
public class WxUserController {

	@Autowired
	private IWxUserService wxUserService;

	/**
	 * 根据公众号标识获取开放平台唯一标识
	 *
	 * @param openIdGzh 公众号标识
	 * @return
	 */
	@GetMapping("/findUnionIdByOpenIdGzh")
	@ApiOperationSupport(order = 1)
	@ApiOperation(value = "根据公众号标识获取开放平台唯一标识", notes = "")
	public R<String> findUnionIdByOpenIdGzh(@ApiParam(value = "公众号标识", required = true) String openIdGzh){
		if(StringUtils.isBlank(openIdGzh)){
			return R.data(null);
		}
		WxUser wu = wxUserService.getOne(Wrappers.<WxUser>lambdaQuery().eq(WxUser::getOpenIdGzh, openIdGzh));
		if(wu == null){
			return R.data(null);
		}else{
			return R.data(wu.getUnionId());
		}
	}
}
