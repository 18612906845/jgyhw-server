package org.springblade.common.constant;

/**
 * 微信小程序通用参数常量
 *
 * Created by WangLei on 2019/11/24 0024 11:29
 */
public interface WxXcxParamConstant {

	/**
	 * 微信小程序AppId
	 */
	String APP_ID = "wx6716f69ca2571bca";

	/**
	 * 微信小程序密匙
	 */
	String APP_SECRET = "c2a2dff4f2bd2e72e4b75ca714cbcb9e";

	/**
	 * 微信小程序ServiceApi Token Redis Key前缀
	 */
	String WX_XCX_SERVICE_API_TOKEN_KEY_PREFIX = "wxServiceApiTokenKey:xcx:";

	/**
	 * 微信小程序获取OpenId SessionKey 请求地址
	 */
	String WX_XCX_GET_OPENID_REQ_URL = "https://api.weixin.qq.com/sns/jscode2session?appid=APPID&secret=SECRET&js_code=JSCODE&grant_type=authorization_code";

	/**
	 * 微信支付Api地址
	 */
	String WX_PAY_BY_API_URL = "https://api.mch.weixin.qq.com/mmpaymkttransfers/promotion/transfers";

	/**
	 * 微信支付商户号
	 */
	String WX_PAY_BY_MCHID = "1509830291";

	/**
	 * 微信支付Key
	 */
	String WX_PAY_BY_KEY = "b755047666639eeaf48a840eb4f54ab4";
}
