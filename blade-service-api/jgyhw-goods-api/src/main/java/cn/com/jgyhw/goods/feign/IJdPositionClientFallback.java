package cn.com.jgyhw.goods.feign;

import cn.com.jgyhw.goods.entity.JdPosition;
import org.springblade.core.tool.api.R;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Feign失败配置
 *
 * Created by WangLei on 2019/11/25 0025 00:05
 */
@Component
public class IJdPositionClientFallback implements IJdPositionClient {
	/**
	 * 根据京东推广位ID查询京东推广位
	 *
	 * @param positionId 推广位ID
	 * @return
	 */
	@Override
	public R<JdPosition> findJdPositionByPositionId(@RequestParam("positionId") Long positionId) {
		return R.fail(400,"Feign未获取到京东推广位");
	}
}
