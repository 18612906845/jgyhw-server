package cn.com.jgyhw.account.entity;

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
 * 流水账本
 *
 * Created by WangLei on 2019/11/23 0023 23:26
 */
@Data
@TableName("jgyhw_money_account")
@ApiModel(value = "流水账本", description = "流水账本")
public class MoneyAccount extends BaseEntity {

	/**
	 * 主键id
	 */
	@JsonSerialize(using = ToStringSerializer.class)
	@TableId(value = "id", type = IdType.ID_WORKER)
	@ApiModelProperty(value = "主键id")
	private Long id;

	/**
	 * 微信用户标识
	 */
	@JsonSerialize(using = ToStringSerializer.class)
	@ApiModelProperty(value = "微信用户标识")
	private Long wxUserId;

	/**
	 * 变更类型，1：购物返现；2：余额提现；3：推广提成；4：邀新奖励；5：推广收益
	 */
	@ApiModelProperty(value = "变更类型，1：购物返现；2：余额提现；3：推广提成；4：邀新奖励；5：推广收益")
	private Integer changeType;

	/**
	 * 变更时间
	 */
	@ApiModelProperty(value = "变更时间")
	private Date changeTime;

	/**
	 * 变更金额
	 */
	@ApiModelProperty(value = "变更金额")
	private Double changeMoney;

	/**
	 * 余额
	 */
	@ApiModelProperty(value = "余额")
	private Double balance;

	/**
	 * 关联主体Json字符串
	 */
	@ApiModelProperty(value = "关联主体Json字符串")
	private String targetJson;

}
