package org.springblade.system.user.feign;


import org.springblade.core.launch.constant.AppConstant;
import org.springblade.core.tool.api.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 微信用户 Feign接口类
 *
 * Created by WangLei on 2019/11/21 0021 22:02
 */
@FeignClient(
	value = AppConstant.APPLICATION_USER_NAME,
	fallback = IWxUserClientFallback.class
)
public interface IWxUserClient {

	String API_PREFIX = "/wxUser";

	/**
	 * 根据公众号标识获取开放平台唯一标识
	 *
	 * @param openIdGzh 公众号标识
	 * @return
	 */
	@GetMapping(API_PREFIX + "/findUnionIdByOpenIdGzh")
	R<String> findUnionIdByOpenIdGzh(String openIdGzh);

}
