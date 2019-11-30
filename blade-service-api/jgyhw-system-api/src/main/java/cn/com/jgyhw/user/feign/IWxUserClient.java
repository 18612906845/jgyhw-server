package cn.com.jgyhw.user.feign;


import cn.com.jgyhw.user.entity.WxUser;
import org.springblade.common.constant.JgyhwConstant;
import org.springblade.core.tool.api.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 微信用户 Feign接口类
 *
 * Created by WangLei on 2019/11/21 0021 22:02
 */
@FeignClient(
	value = JgyhwConstant.APPLICATION_SYSTEM_NAME,
	fallback = IWxUserClientFallback.class
)
public interface IWxUserClient {

	String API_PREFIX = "/wxUser";

	/**
	 * 根据公众号标识获取微信用户
	 *
	 * @param openIdGzh 公众号标识
	 * @return
	 */
	@GetMapping(API_PREFIX + "/findWxUserByOpenIdGzh")
	R<WxUser> findWxUserByOpenIdGzh(@RequestParam("openIdGzh") String openIdGzh);

	/**
	 * 根据用户标识获取微信用户
	 *
	 * @param wxUserId 用户标识
	 * @return
	 */
	@GetMapping(API_PREFIX + "/findWxUserById")
	R<WxUser> findWxUserById(@RequestParam("wxUserId") Long wxUserId);
}
