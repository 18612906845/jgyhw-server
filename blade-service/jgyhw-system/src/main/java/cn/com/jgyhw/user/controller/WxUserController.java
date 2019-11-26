package cn.com.jgyhw.user.controller;

import cn.com.jgyhw.user.entity.WxUser;
import cn.com.jgyhw.user.service.IWxUserService;
import cn.com.jgyhw.user.vo.WxUserReturnMoneyScaleVo;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiOperationSupport;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springblade.core.tool.api.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 微信用户控制器
 *
 * Created by WangLei on 2019/11/21 0021 22:02
 */
@Slf4j
@RestController
@RequestMapping("/wxUser")
@Api(value = "微信用户", tags = "微信用户")
public class WxUserController {

	@Autowired
	private IWxUserService wxUserService;

	/**
	 * 根据公众号标识获取微信用户
	 *
	 * @param openIdGzh 公众号标识
	 * @return
	 */
	@GetMapping("/findWxUserByOpenIdGzh")
	@ApiOperationSupport(order = 1)
	@ApiOperation(value = "根据公众号标识获取微信用户", notes = "")
	public R<WxUser> findWxUserByOpenIdGzh(@ApiParam(value = "公众号标识", required = true) String openIdGzh){
		if(StringUtils.isBlank(openIdGzh)){
			return R.data(null);
		}
		WxUser wu = wxUserService.getOne(Wrappers.<WxUser>lambdaQuery().eq(WxUser::getOpenIdGzh, openIdGzh));
		return R.data(wu);
	}

	/**
	 * 根据用户标识获取微信用户
	 *
	 * @param wxUserId 用户标识
	 * @return
	 */
	@GetMapping("/findWxUserById")
	@ApiOperationSupport(order = 2)
	@ApiOperation(value = "根据用户标识获取微信用户", notes = "")
	public R<WxUser> findWxUserById(@ApiParam(value = "用户标识", required = true) Long wxUserId){
		if(wxUserId == null){
			return R.data(null);
		}
		WxUser wu = wxUserService.getById(wxUserId);
		return R.data(wu);
	}

	/**
	 * 根据微信用户标识获取返现/提成/收益比例、推荐人租户信息
	 *
	 * @param wxUserId 用户标识
	 * @return
	 */
	@GetMapping("/findWxUserReturnMoneyScaleVoById")
	@ApiOperationSupport(order = 3)
	@ApiOperation(value = "根据微信用户标识获取返现/提成/收益比例、推荐人租户信息", notes = "")
	public R<WxUserReturnMoneyScaleVo> findWxUserReturnMoneyScaleVoById(@ApiParam(value = "用户标识", required = true) Long wxUserId){
		return R.data(wxUserService.findWxUserReturnMoneyScaleVoById(wxUserId));
	}

}
