package org.springblade.system.user.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 微信用户返现/提成/收益比例、推荐人租户信息Vo对象
 *
 * Created by WangLei on 2019/11/23 0011 20:07
 */
@Data
@ApiModel(value = "微信用户返现/提成/收益比例、推荐人租户信息Vo对象", description = "微信用户返现/提成/收益比例、推荐人租户信息Vo对象")
public class WxUserReturnMoneyScaleVo {

	/**
	 * 订单所属租户
	 */
	@ApiModelProperty(value = "订单所属租户")
	private String tenantId;

	/**
	 * 订单所属用户
	 */
	@ApiModelProperty(value = "订单所属用户")
	private Long wxUserId;

	/**
	 * 当前用户返现比例
	 */
	@ApiModelProperty(value = "当前用户返现比例")
    private int returnScale;

	/**
	 * 推荐人提成/收益比例
	 */
	@ApiModelProperty(value = "推荐人提成/收益比例")
	private int parentReturnScaleTc;

	/**
	 * 推荐人用户ID
	 */
	@ApiModelProperty(value = "推荐人用户ID")
	private Long parentWxUserId;

	/**
	 * 推荐人是否租户
	 */
	@ApiModelProperty(value = "推荐人是否租户")
	private Boolean parentWxUserIdIsTenant = false;
}
