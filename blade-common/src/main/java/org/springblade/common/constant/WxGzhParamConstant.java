package org.springblade.common.constant;

/**
 * 微信公众号通用参数常量
 *
 * Created by WangLei on 2019/11/24 0024 11:07
 */
public interface WxGzhParamConstant {

	/**
	 * 微信公众号ServiceApi Token Redis Key前缀
	 */
	String WX_GZH_SERVICE_API_TOKEN_KEY_PREFIX = "wxServiceApiTokenKey:gzh:";

	/**
	 * 获取Token和openId（获取用户详细信息的上一步）
	 */
	String GET_ACCESS_TOKEN_OPEN_ID_REQ_URL = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=APPID&secret=SECRET&code=CODE&grant_type=authorization_code";

	/**
	 * 获取用户详细信息
	 */
	String GET_USER_INFO_REQ_URL = "https://api.weixin.qq.com/sns/userinfo?access_token=ACCESS_TOKEN&openid=OPENID&lang=zh_CN";

	/**
	 * 获取微信ServiceApi令牌请求地址
	 */
	String WX_UPDATE_SERVICE_API_TOKEN_REQ_URL = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=APPID&secret=APPSECRET";

	/**
	 * 发送客服消息接口URL
	 */
	String SEND_CUSTOM_MESSAGE_URL = "https://api.weixin.qq.com/cgi-bin/message/custom/send?access_token=ACCESS_TOKEN";

	/**
	 * 发送模版消息接口URL
	 */
	String SEND_TEMPLATE_MESSAGE_URL = "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=ACCESS_TOKEN";
}
