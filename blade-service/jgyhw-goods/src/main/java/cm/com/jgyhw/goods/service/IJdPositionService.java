package cm.com.jgyhw.goods.service;

import cn.com.jgyhw.goods.entity.JdPosition;
import org.springblade.core.mp.base.BaseService;

/**
 * 京东推广位服务类
 *
 * Created by WangLei on 2019/11/24 0024 23:37
 */
public interface IJdPositionService extends BaseService<JdPosition> {

	/**
	 * 根据微信用户标识获取京东推广位
	 *
	 * @param wxUserId 微信用户标识
	 * @return
	 */
	JdPosition queryJdPositionByWxUserId(Long wxUserId);
}
