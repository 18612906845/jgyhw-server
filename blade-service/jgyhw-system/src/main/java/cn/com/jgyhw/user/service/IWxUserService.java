package cn.com.jgyhw.user.service;


import cn.com.jgyhw.user.entity.WxUser;
import cn.com.jgyhw.user.vo.WxUserReturnMoneyScaleVo;
import org.springblade.core.mp.base.BaseService;

/**
 * 微信用户服务类
 *
 * Created by WangLei on 2019/11/19 0019 19:26
 */
public interface IWxUserService extends BaseService<WxUser> {

	/**
	 * 根据微信用户标识获取返现/提成/收益比例、推荐人租户信息
	 *
	 * @param wxUserId 微信用户标识
	 * @return
	 */
	WxUserReturnMoneyScaleVo findWxUserReturnMoneyScaleVoById(Long wxUserId);
}
