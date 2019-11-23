package cn.com.jgyhw.message.feign;

import org.springblade.common.constant.JgyhwConstant;
import org.springblade.core.tool.api.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;

/**
 * 消息服务 Feign接口类
 *
 * Created by WangLei on 2019/11/23 0023 21:01
 */
@FeignClient(
	value = JgyhwConstant.APPLICATION_MESSAGE_NAME,
	fallback = IWxGzhMessageClientFallback.class
)
public interface IWxGzhMessageClient {

	String API_PREFIX = "/wxGzhMessage";

	/**
	 * 发送文本消息
	 *
	 * @param toUser 接收者微信公众号标识
	 * @param content 消息内容
	 * @return
	 */
	@GetMapping(API_PREFIX + "/sendTextMessage")
	R sendTextMessage(@RequestParam("toUser") String toUser, @RequestParam("content") String content);

	/**
	 * 发送确认订单微信消息
	 *
	 * @param openIdGzh 微信用户公众号标识
	 * @param orderId 订单编号
	 * @param orderStatus 订单状态
	 * @return
	 */
	@GetMapping(API_PREFIX + "/sendAffirmOrderWxMessage")
	R sendAffirmOrderWxMessage(@RequestParam("openIdGzh") String openIdGzh, @RequestParam("orderId") String orderId, @RequestParam("orderStatus") String orderStatus);

	/**
	 * 发送完成订单微信消息
	 *
	 * @param openIdGzh 微信用户公众号标识
	 * @param orderId 订单编号
	 * @param orderFinishTime 订单完成时间
	 */
	@GetMapping(API_PREFIX + "/sendFinishOrderWxMessage")
	R sendFinishOrderWxMessage(@RequestParam("openIdGzh") String openIdGzh, @RequestParam("orderId") String orderId, @RequestParam("orderFinishTime") Date orderFinishTime);

	/**
	 * 发送获得返现微信消息
	 *
	 * @param openIdGzh 微信用户公众号标识
	 * @param returnMoneyCause 返现原因
	 * @param returnMoney 返现金额
	 */
	@GetMapping(API_PREFIX + "/sendRebateWxMessage")
	 R sendRebateWxMessage(@RequestParam("openIdGzh") String openIdGzh, @RequestParam("returnMoneyCause") String returnMoneyCause, @RequestParam("returnMoney") double returnMoney);

	/**
	 * 发送支付成功消息
	 *
	 * @param openIdGzh 微信用户公众号标识
	 * @param payMoney 支付金额
	 * @param payTime 支付时间
	 */
	@GetMapping(API_PREFIX + "/sendPaySuccessWxMessage")
	R sendPaySuccessWxMessage(@RequestParam("openIdGzh") String openIdGzh, @RequestParam("payMoney") Double payMoney, @RequestParam("payTime") Date payTime);
}
