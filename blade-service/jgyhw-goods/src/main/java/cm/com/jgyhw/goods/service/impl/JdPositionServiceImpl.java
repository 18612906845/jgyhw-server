package cm.com.jgyhw.goods.service.impl;

import cm.com.jgyhw.goods.mapper.JdPositionMapper;
import cm.com.jgyhw.goods.service.IJdPositionService;
import cn.com.jgyhw.goods.entity.JdPosition;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.springblade.core.mp.base.BaseServiceImpl;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * Created by WangLei on 2019/11/24 0024 23:38
 */
@Slf4j
@Service
public class JdPositionServiceImpl extends BaseServiceImpl<JdPositionMapper, JdPosition> implements IJdPositionService {

	/**
	 * 根据微信用户标识获取京东推广位
	 *
	 * @param wxUserId 微信用户标识
	 * @return
	 */
	@Override
	public synchronized JdPosition queryJdPositionByWxUserId(Long wxUserId) {
		log.info("微信用户：" + wxUserId + "获取京东推广位");
		// 查询是否有对应记录
		JdPosition jp = baseMapper.selectOne(Wrappers.<JdPosition>lambdaQuery().eq(JdPosition::getWxUserId, wxUserId));
		if(jp == null){
			jp = baseMapper.selectJdPositionByOldest();
			if(jp == null){
				return null;
			}
		}
		jp.setWxUserId(wxUserId);
		jp.setUpdateTime(new Date());
		baseMapper.updateById(jp);
		log.info("微信用户：" + wxUserId + "获取京东推广位，结果：" + JSON.toJSONString(jp));
		return jp;
	}
}
