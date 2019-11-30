package cn.com.jgyhw.account.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import org.springblade.core.mp.base.BaseEntity;

import java.util.Date;

/**
 * 流水账本
 *
 * Created by WangLei on 2019/11/23 0023 23:26
 */
@Data
@TableName("jgyhw_money_account")
public class MoneyAccount extends BaseEntity {

	/**
	 * 主键id
	 */
	@JsonSerialize(using = ToStringSerializer.class)
	@TableId(value = "id", type = IdType.ID_WORKER)
	private Long id;

	/**
	 * 微信用户标识
	 */
	@JsonSerialize(using = ToStringSerializer.class)
	private Long wxUserId;

	/**
	 * 变更类型，1：购物返现；2：余额提现；3：推广提成；4：邀新奖励；5：推广收益
	 */
	private Integer changeType;

	/**
	 * 变更时间
	 */
	private Date changeTime;

	/**
	 * 变更金额
	 */
	private Double changeMoney;

	/**
	 * 余额
	 */
	private Double balance;

	/**
	 * 累计提现
	 */
	private Double returnMoneySum;

	/**
	 * 关联主体Json字符串
	 */
	private String targetJson;

	/**
	 * 根据条件值计算md5，避免重复
	 */
	private String md5;

	/**
	 * 支付时间
	 */
	private Date payTime;

	/**
	 * 支付状态，1：待支付；2：已支付；3：支付失败
	 */
	private Integer payStatus;

	/**
	 * 商户订单号
	 */
	private String partnerTradeNo;

	/**
	 * 微信付款单号
	 */
	private String paymentNo;

	/**
	 * 错误代码
	 */
	private String errCode;

	/**
	 * 错误代码描述
	 */
	private String errCodeDes;

}
