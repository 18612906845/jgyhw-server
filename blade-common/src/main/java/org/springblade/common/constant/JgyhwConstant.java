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
	 * 任务服务名称
	 */
	String APPLICATION_TASK_NAME = "jgyhw-task";

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


}
