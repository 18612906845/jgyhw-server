package cn.com.jgyhw.token.service;

/**
 * 微信令牌服务类
 *
 * Created by WangLei on 2019/11/22 0022 21:58
 */
public interface IWxTokenService {

	/**
	 * 定时更新微信公众号服务Api令牌
	 */
	void timingUpdateWxGzhServiceApiToken();

	/**
	 * 定时更新微信小程序服务Api令牌
	 */
	void timingUpdateWxXcxServiceApiToken();
}
