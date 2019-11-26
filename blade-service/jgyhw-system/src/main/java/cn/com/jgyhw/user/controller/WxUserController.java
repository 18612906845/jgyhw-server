package cn.com.jgyhw.user.controller;

import cn.com.jgyhw.user.entity.WxUser;
import cn.com.jgyhw.user.service.IWxUserService;
import cn.com.jgyhw.user.vo.WxUserReturnMoneyScaleVo;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiOperationSupport;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springblade.core.tool.api.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

	@Value("${jgyhw.user.returnMoneyWxUserIdDefault}")
	private Long returnMoneyWxUserIdDefault;

	@Value("${jgyhw.user.returnMoneyTenantIdDefault}")
	private String returnMoneyTenantIdDefault;

	@Value("${jgyhw.user.returnMoneyShareDefault}")
	private Integer systemReturnMoneyShareDefault;

	@Value("${jgyhw.user.returnMoneyShareTcDefault}")
	private Integer systemReturnMoneyShareTcDefault;

	@Value("${jgyhw.user.returnMoneyShareSyDefault}")
	private Integer systemReturnMoneyShareSyDefault;

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
		WxUserReturnMoneyScaleVo wurmsVo = new WxUserReturnMoneyScaleVo();
		// 设置订单所属用户为系统缺省值
		wurmsVo.setWxUserId(returnMoneyWxUserIdDefault);
		// 设置当前用户返现比例为系统缺省值
		wurmsVo.setReturnScale(systemReturnMoneyShareDefault);
		// 设置推荐人提成比例为系统缺省值
		wurmsVo.setParentReturnScaleTc(systemReturnMoneyShareTcDefault);
		if(wxUserId == null){
			log.info("根据微信用户标识获取返现/提成/收益比例、推荐人租户信息，参数用户标识为空，结果：" + JSON.toJSONString(wurmsVo));
			return R.data(wurmsVo);
		}
		// 获取该用户信息
		WxUser wu = wxUserService.getById(wxUserId);
		if(wu == null){
			log.info("根据微信用户标识获取返现/提成/收益比例、推荐人租户信息，参数未查询到用户信息，最终结果：" + JSON.toJSONString(wurmsVo));
			return R.data(wurmsVo);
		}
		// 设置订单所属用户
		wurmsVo.setWxUserId(wu.getId());
		// 没有推荐人
		if(wu.getParentWxUserId() == null){
			// 应该是管理员自己，购物百分百返现，无提成
			wurmsVo.setReturnScale((wu.getReturnMoneyShare() == null || wu.getReturnMoneyShare() <= 0) ? 100 : wu.getReturnMoneyShare());
			wurmsVo.setParentReturnScaleTc(0);
			log.info("根据微信用户标识获取返现/提成/收益比例、推荐人租户信息，无推荐人信息，最终结果：" + JSON.toJSONString(wurmsVo));
			return R.data(wurmsVo);
		}
		// 有推荐人
		WxUser pwu = wxUserService.getById(wu.getParentWxUserId());
		if(pwu == null){// 推荐人用户信息不存在
			// 修改推荐人字段为管理员
			wu.setParentWxUserId(returnMoneyWxUserIdDefault);
			wxUserService.updateById(wu);
			wurmsVo.setParentWxUserId(returnMoneyWxUserIdDefault);
			wurmsVo.setParentWxUserNikeName("系统管理员");
			// 查询管理员信息
			WxUser rootWxUser = wxUserService.getById(returnMoneyWxUserIdDefault);
			if(rootWxUser != null){
				// 用户返现比例 = 管理员设置的租户返现比例
				if(rootWxUser.getTenantReturnMoneyShare() != null && rootWxUser.getTenantReturnMoneyShare() > 0){
					wurmsVo.setReturnScale(rootWxUser.getTenantReturnMoneyShare());
				}
				// 推荐人提成比例 = 推荐人收益 = 100%
				if(rootWxUser.getTenantReturnMoneyShareTc() != null && rootWxUser.getTenantReturnMoneyShareTc() > 0){
					wurmsVo.setParentReturnScaleTc(100);
				}
				// 订单所属租户 = 推荐人自己的租户ID
				wurmsVo.setTenantId(rootWxUser.getTenantId());
				wurmsVo.setParentWxUserIdIsTenant(true);
			}
		}else{// 推荐人用户信息存在
			wurmsVo.setParentWxUserId(pwu.getId());
			wurmsVo.setParentWxUserNikeName(pwu.getNickName());
			if(StringUtils.isNotBlank(pwu.getTenantId())){// 推荐人是租户
				// 用户返现比例 = 推荐人设置的默认返现比例
				if(pwu.getTenantReturnMoneyShare() != null && pwu.getTenantReturnMoneyShare() > 0){
					wurmsVo.setReturnScale(pwu.getTenantReturnMoneyShare());
				}
				// 用户返现比例 = 用户信息里的返现比例
				if(wu.getReturnMoneyShare() != null && wu.getReturnMoneyShare() > 0){
					wurmsVo.setReturnScale(wu.getReturnMoneyShare());
				}
				// 推荐人提成 = 推荐人收益 = 系统收益缺省值
				wurmsVo.setParentReturnScaleTc(systemReturnMoneyShareSyDefault);
				// 推荐人是管理员 = 推荐人收益 = 100%
				if(pwu.getId().equals(returnMoneyWxUserIdDefault)){
					wurmsVo.setParentReturnScaleTc(100);
				}
				// 订单所属租户 = 推荐人自己的租户ID
				wurmsVo.setTenantId(pwu.getTenantId());
				wurmsVo.setParentWxUserIdIsTenant(true);
			}else{// 推荐人不是租户
				// 用户返现比例 = 用户自身返现比例
				if(wu.getReturnMoneyShare() != null && wu.getReturnMoneyShare() > 0){
					wurmsVo.setReturnScale(wu.getReturnMoneyShare());
				}
				// 推荐人提成比例 = 根据推荐人的推荐人的情况设置
				if(pwu.getParentWxUserId() != null && wxUserService.getById(pwu.getParentWxUserId()) != null){// 推荐人的推荐人存在
					WxUser ppWu = wxUserService.getById(pwu.getParentWxUserId());
					if(StringUtils.isNotBlank(ppWu.getTenantId())){// 推荐人的推荐人是租户
						// 推荐人提成 = 推荐人的推荐人设置的租户提成
						if(ppWu.getTenantReturnMoneyShareTc() != null && ppWu.getTenantReturnMoneyShareTc() > 0){
							wurmsVo.setParentReturnScaleTc(ppWu.getTenantReturnMoneyShareTc());
						}
						// 推荐人提成 = 推荐人自身提成比例
						if(pwu.getReturnMoneyShareTc() != null && pwu.getReturnMoneyShareTc() > 0){
							wurmsVo.setParentReturnScaleTc(pwu.getReturnMoneyShareTc());
						}
					}else{// 推荐人的推荐人不是租户
						// 推荐人提成 = 系统缺省提成比例
						wurmsVo.setParentReturnScaleTc(systemReturnMoneyShareTcDefault);
						// 推荐人提成 = 推荐人自身提成比例
						if(pwu.getReturnMoneyShareTc() != null && pwu.getReturnMoneyShareTc() > 0){
							wurmsVo.setParentReturnScaleTc(pwu.getReturnMoneyShareTc());
						}
					}
				}else{// 推荐人的推荐人不存在
					// 推荐人提成比例 = 系统提成缺省值
					wurmsVo.setParentReturnScaleTc(systemReturnMoneyShareTcDefault);
					// 推荐人提成比例 = 推荐人自身提成比例
					if(pwu.getReturnMoneyShareTc() != null && pwu.getReturnMoneyShareTc() > 0){
						wurmsVo.setParentReturnScaleTc(pwu.getReturnMoneyShareTc());
					}
				}

			}
		}
		log.info("根据微信用户标识获取返现/提成/收益比例、推荐人租户信息，最终结果：" + JSON.toJSONString(wurmsVo));
		return R.data(wurmsVo);
	}

}
