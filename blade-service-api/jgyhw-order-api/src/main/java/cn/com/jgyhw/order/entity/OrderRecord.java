package cn.com.jgyhw.order.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
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
public class OrderRecord extends BaseEntity {

	/**
	 * 标识
	 */
	@JsonSerialize(using = ToStringSerializer.class)
	@TableId(value = "id", type = IdType.ID_WORKER)
	private Long id;

	/**
	 * 订单编号
	 */
	private String orderId;

	/**
	 * 下单时间
	 */
	private Date orderTime;

	/**
	 * 完成时间
	 */
	private Date finishTime;

	/**
	 * 联盟佣金
	 */
	private Double commission;

	/**
	 * 返现金额
	 */
	private Double returnMoney;

	/**
	 * 订单平台：1：京东，2：拼多多，3：淘宝
	 */
	private Integer platform;

	/**
	 * 微信用户标识
	 */
	@JsonSerialize(using = ToStringSerializer.class)
	private Long wxUserId;

	/**
	 * 订单状态，1：待付款，2：已付款，3：已取消，4：已成团，5：已完成，6：已入账，7：无效
	 */
	private Integer status;
}
