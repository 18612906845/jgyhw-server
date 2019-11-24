package cn.com.jgyhw.goods.feign;

import cn.com.jgyhw.goods.entity.JdPosition;
import org.springblade.common.constant.JgyhwConstant;
import org.springblade.core.tool.api.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 京东推广位 Feign接口类
 *
 * Created by WangLei on 2019/11/25 0025 00:04
 */
@FeignClient(
	value = JgyhwConstant.APPLICATION_GOODS_NAME,
	fallback = IJdPositionClientFallback.class
)
public interface IJdPositionClient {

	String API_PREFIX = "/jdPosition";

	/**
	 * 根据京东推广位ID查询京东推广位
	 *
	 * @param positionId 推广位ID
	 * @return
	 */
	@GetMapping(API_PREFIX + "/findJdPositionByPositionId")
	R<JdPosition> findJdPositionByPositionId(@RequestParam("positionId") Long positionId);
}
