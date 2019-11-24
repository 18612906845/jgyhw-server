package cn.com.jgyhw.order.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 用户返现比例Vo对象
 *
 * Created by WangLei on 2019/11/23 0011 20:07
 */
@Data
@ApiModel(value = "用户返现比例Vo对象", description = "用户返现比例Vo对象")
public class ReturnMoneyScaleVo {

	/**
	 * 推荐人租户ID
	 */
	@ApiModelProperty(value = "推荐人租户ID")
	private String parentTenantId;

	/**
	 * 订单所属用户
	 */
	@ApiModelProperty(value = "订单所属用户")
	private Long wxUserId;

	/**
	 * 返现比例
	 */
	@ApiModelProperty(value = "返现比例")
    private int returnScale;
}
