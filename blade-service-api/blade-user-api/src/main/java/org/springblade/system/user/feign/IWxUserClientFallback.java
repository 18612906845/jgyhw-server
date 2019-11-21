package org.springblade.system.user.feign;

import org.springblade.core.tool.api.R;
import org.springframework.stereotype.Component;

/**
 * Feign失败配置
 *
 * @author Chill
 */
@Component
public class IWxUserClientFallback implements IWxUserClient {

	/**
	 * 根据公众号标识获取开放平台唯一标识
	 *
	 * @param openIdGzh 公众号标识
	 * @return
	 */
	@Override
	public R<String> findUnionIdByOpenIdGzh(String openIdGzh) {
		return R.fail("未获取到开放平台唯一标识");
	}
}
