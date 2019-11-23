package cn.com.jgyhw.message.feign;

import org.springblade.core.tool.api.R;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;

/**
 * Feign失败配置
 *
 * Created by WangLei on 2019/11/23 0023 21:05
 */
@Component
public class IWxGzhMessageClientFallback implements IWxGzhMessageClient {
	/**
	 * 发送文本消息
	 *
	 * @param toUser  接收者微信公众号标识
	 * @param content 消息内容
	 * @return
	 */
	@Override
	public R sendTextMessage(@RequestParam("toUser") String toUser, @RequestParam("content") String content) {
		return R.fail(400,"发送文本消息Feign失败");
	}

	/**
	 * 发送确认订单微信消息
	 *
	 * @param openIdGzh   微信用户公众号标识
	 * @param orderId     订单编号
	 * @param orderStatus 订单状态
	 * @return
	 */
	@Override
	public R sendAffirmOrderWxMessage(@RequestParam("openIdGzh") String openIdGzh, @RequestParam("orderId") String orderId, @RequestParam("orderStatus") String orderStatus) {
		return R.fail(400,"发送确认订单微信消息Feign失败");
	}

	/**
	 * 发送完成订单微信消息
	 *
	 * @param openIdGzh       微信用户公众号标识
	 * @param orderId         订单编号
	 * @param orderFinishTime 订单完成时间
	 */
	@Override
	public R sendFinishOrderWxMessage(@RequestParam("openIdGzh") String openIdGzh, @RequestParam("orderId") String orderId, @RequestParam("orderFinishTime") Date orderFinishTime) {
		return R.fail(400,"发送完成订单微信消息Feign失败");
	}

	/**
	 * 发送获得返现微信消息
	 *
	 * @param openIdGzh        微信用户公众号标识
	 * @param returnMoneyCause 返现原因
	 * @param returnMoney      返现金额
	 */
	@Override
	public R sendRebateWxMessage(@RequestParam("openIdGzh") String openIdGzh, @RequestParam("returnMoneyCause") String returnMoneyCause, @RequestParam("returnMoney") double returnMoney) {
		return R.fail(400,"发送获得返现微信消息Feign失败");
	}

	/**
	 * 发送支付成功消息
	 *
	 * @param openIdGzh 微信用户公众号标识
	 * @param payMoney  支付金额
	 * @param payTime   支付时间
	 */
	@Override
	public R sendPaySuccessWxMessage(@RequestParam("openIdGzh") String openIdGzh, @RequestParam("payMoney") Double payMoney, @RequestParam("payTime") Date payTime) {
		return R.fail(400,"发送支付成功消息Feign失败");
	}
}
