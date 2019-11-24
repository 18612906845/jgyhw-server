package cm.com.jgyhw.goods.mapper;

import cn.com.jgyhw.goods.entity.JdPosition;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * 京东推广位 Mapper 接口
 *
 * Created by WangLei on 2019/11/24 0024 23:39
 */
public interface JdPositionMapper extends BaseMapper<JdPosition> {

	/**
	 * 查询更新时间最早的一条记录
	 *
	 * @return
	 */
	JdPosition selectJdPositionByOldest();
}
