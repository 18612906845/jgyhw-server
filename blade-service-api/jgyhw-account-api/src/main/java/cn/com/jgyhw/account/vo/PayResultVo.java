package cn.com.jgyhw.account.vo;

import lombok.Data;

/**
 * 支付结果
 *
 * Created by WangLei on 2019/11/30 0030 23:10
 */
@Data
public class PayResultVo {

	/**
	 * 支付结果消息
	 */
	private String msg;

	/**
	 * 是否支付成功
	 */
	private boolean status;
}
