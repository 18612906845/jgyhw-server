package cn.com.jgyhw.order.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 更新订单结果Vo对象
 *
 * Created by WangLei on 2019/11/23 0023 19:10
 */
@Data
@ApiModel(value = "更新订单结果Vo对象", description = "更新订单结果Vo对象")
public class UpdateOrderRespVo {

	/**
	 * 查询状态，true为正常
	 */
	@ApiModelProperty(value = "查询状态，true为正常")
	private boolean status = false;

	/**
	 * 是否还有更多
	 */
	@ApiModelProperty(value = "是否还有更多")
	private boolean isMore = true;
}
