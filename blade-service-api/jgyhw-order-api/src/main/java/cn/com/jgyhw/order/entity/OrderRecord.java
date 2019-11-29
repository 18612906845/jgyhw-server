package cn.com.jgyhw.order.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springblade.core.mp.base.BaseEntity;

import java.util.Date;

/**
 * 订单记录
 *
 * Created by WangLei on 2019/11/23 0023 18:11
 */
@Data
@TableName("jgyhw_order_record")
@ApiModel(description = "订单记录")
public class OrderRecord extends BaseEntity {

	/**
	 * 标识
	 */
	@JsonSerialize(using = ToStringSerializer.class)
	@TableId(value = "id", type = IdType.ID_WORKER)
	@ApiModelProperty(value = "标识")
	private Long id;

	/**
	 * 订单编号
	 */
	@ApiModelProperty(value = "订单编号")
	private String orderId;

	/**
	 * 下单时间
	 */
	@ApiModelProperty(value = "下单时间")
	private Date orderTime;

	/**
	 * 完成时间
	 */
	@ApiModelProperty(value = "完成时间")
	private Date finishTime;

	/**
	 * 联盟佣金
	 */
	@ApiModelProperty(value = "联盟佣金")
	private Double commission;

	/**
	 * 返现金额
	 */
	@ApiModelProperty(value = "返现金额")
	private Double returnMoney;

	/**
	 * 订单平台：1：京东，2：拼多多，3：淘宝
	 */
	@ApiModelProperty(value = "订单平台：1：京东，2：拼多多，3：淘宝")
	private Integer platform;

	/**
	 * 微信用户标识
	 */
	@JsonSerialize(using = ToStringSerializer.class)
	@ApiModelProperty(value = "微信用户标识")
	private Long wxUserId;

	/**
	 * 租户ID
	 */
	@ApiModelProperty(value = "租户ID")
	private String tenantId;

	/**
	 * 订单状态，1：待付款，2：已付款，3：已取消，4：已成团，5：已完成，6：已入账，7：无效
	 */
	@ApiModelProperty(value = "订单状态，1：待付款，2：已付款，3：已取消，4：已成团，5：已完成，6：已入账，7：无效")
	private Integer status;
}
