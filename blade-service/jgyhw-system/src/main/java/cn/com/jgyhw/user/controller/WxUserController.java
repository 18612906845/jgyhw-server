package cn.com.jgyhw.user.controller;

import cn.com.jgyhw.user.entity.WxUser;
import cn.com.jgyhw.user.service.IWxUserService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springblade.core.tool.api.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 微信用户控制器
 *
 * Created by WangLei on 2019/11/21 0021 22:02
 */
@Slf4j
@RestController
@RequestMapping("/wxUser")
public class WxUserController {

	@Autowired
	private IWxUserService wxUserService;

	/**
	 * 根据公众号标识获取微信用户
	 *
	 * @param openIdGzh 公众号标识
	 * @return
	 */
	@GetMapping("/findWxUserByOpenIdGzh")
	public R<WxUser> findWxUserByOpenIdGzh(String openIdGzh){
		if(StringUtils.isBlank(openIdGzh)){
			return R.data(null);
		}
		WxUser wu = wxUserService.getOne(Wrappers.<WxUser>lambdaQuery().eq(WxUser::getOpenIdGzh, openIdGzh));
		return R.data(wu);
	}

	/**
	 * 根据用户标识获取微信用户
	 *
	 * @param wxUserId 用户标识
	 * @return
	 */
	@GetMapping("/findWxUserById")
	public R<WxUser> findWxUserById(Long wxUserId){
		if(wxUserId == null){
			return R.data(null);
		}
		WxUser wu = wxUserService.getById(wxUserId);
		return R.data(wu);
	}

	/**
	 * 查询我的邀请总数
	 *
	 * @param loginKey 登陆标识
	 * @return
	 */
	@GetMapping("/findMyInviteSum")
	public R<Integer> findMyInviteSum(Long loginKey){
		int userSum = wxUserService.count(Wrappers.<WxUser>lambdaQuery().eq(WxUser::getParentWxUserId, loginKey));
		return R.data(userSum);
	}

}
