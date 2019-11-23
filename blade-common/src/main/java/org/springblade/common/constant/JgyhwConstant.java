package org.springblade.common.constant;

/**
 * 京购优惠网通用常量
 *
 * Created by WangLei on 2019/11/19 0019 19:23
 */
public interface JgyhwConstant {

	/**
	 * 回复文本类型消息
	 */
	String ANSWER_MSG_TYPE_TEXT = "text";

	/**
	 * 回复图片类型消息
	 */
	String ANSWER_MSG_TYPE_IMAGE = "image";

	/**
	 * 回复语音类型消息
	 */
	String ANSWER_MSG_TYPE_VOICE = "voice";

	/**
	 * 回复视频类型消息
	 */
	String ANSWER_MSG_TYPE_VIDEO = "video";

	/**
	 * 回复音乐类型消息
	 */
	String ANSWER_MSG_TYPE_MUSIC = "music";

	/**
	 * 回复图文类型消息
	 */
	String ANSWER_MSG_TYPE_NEWS = "news";

	/**
	 * 消息服务名称
	 */
	String APPLICATION_MESSAGE_NAME = "jgyhw-message";

	/**
	 * 商品服务名称
	 */
	String APPLICATION_GOODS_NAME = "jgyhw-goods";

	/**
	 * 令牌服务名称
	 */
	String APPLICATION_TOKEN_NAME = "jgyhw-token";

	/**
	 * 订单服务名称
	 */
	String APPLICATION_ORDER_NAME = "jgyhw-order";

	/**
	 * 账本服务名称
	 */
	String APPLICATION_ACCOUNT_NAME = "jgyhw-account";

	/**
	 * 京东商品Redis Key前缀
	 */
	String JD_GOODS_KEY_PREFIX = "jdGoodsKey:";

	/**
	 * 微信公众号ServiceApi Token Redis Key前缀
	 */
	String WX_GZH_SERVICE_API_TOKEN_KEY_PREFIX = "wxServiceApiTokenKey:gzh:";

	/**
	 * 微信小程序ServiceApi Token Redis Key前缀
	 */
	String WX_XCX_SERVICE_API_TOKEN_KEY_PREFIX = "wxServiceApiTokenKey:xcx:";

	/**
	 * 订单状态-待付款
	 */
	Integer ORDER_STATUS_DFK = 1;

	/**
	 * 订单状态-已付款
	 */
	Integer ORDER_STATUS_YFK = 2;

	/**
	 * 订单状态-已取消
	 */
	Integer ORDER_STATUS_YQX = 3;

	/**
	 * 订单状态-已成团
	 */
	Integer ORDER_STATUS_YCT = 4;

	/**
	 * 订单状态-已完成
	 */
	Integer ORDER_STATUS_YWC = 5;

	/**
	 * 订单状态-已入账
	 */
	Integer ORDER_STATUS_YRZ = 6;

	/**
	 * 订单状态-无效
	 */
	Integer ORDER_STATUS_WX = 7;

	/**
	 * 订单平台-京东
	 */
	Integer ORDER_PLATFORM_JD = 1;
}
