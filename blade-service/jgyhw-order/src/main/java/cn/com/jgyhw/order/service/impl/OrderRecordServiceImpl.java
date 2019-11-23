package cn.com.jgyhw.order.service.impl;

import cn.com.jgyhw.order.entity.OrderGoods;
import cn.com.jgyhw.order.entity.OrderRecord;
import cn.com.jgyhw.order.mapper.OrderGoodsMapper;
import cn.com.jgyhw.order.mapper.OrderRecordMapper;
import cn.com.jgyhw.order.service.IOrderRecordService;
import cn.com.jgyhw.order.vo.OrderRecordVo;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.springblade.core.mp.base.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.beans.BeanCopier;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * Created by WangLei on 2019/11/23 0023 18:45
 */
@Slf4j
@Service
public class OrderRecordServiceImpl extends BaseServiceImpl<OrderRecordMapper, OrderRecord>  implements IOrderRecordService {

	@Autowired
	private OrderGoodsMapper orderGoodsMapper;

	/**
	 * 保存订单记录及订单商品集合
	 *
	 * @param orderRecordVo 订单记录视图对象
	 */
	@Override
	public void saveOrderRecord(OrderRecordVo orderRecordVo) {
		OrderRecord or = new OrderRecord();
		BeanCopier copier = BeanCopier.create(OrderRecordVo.class, OrderRecord.class, false);
		copier.copy(orderRecordVo, or, null);
		or.setCreateTime(new Date());
		or.setUpdateTime(new Date());
		int size = baseMapper.insert(or);

		if(size > 0 && CollectionUtils.isNotEmpty(orderRecordVo.getOrderGoodsList())){
			Long recordId = or.getId();
			for(OrderGoods og : orderRecordVo.getOrderGoodsList()){
				og.setOrderRecordId(recordId);
				og.setCreateTime(new Date());
				og.setUpdateTime(new Date());
				orderGoodsMapper.insert(og);
			}
		}
	}

	/**
	 * 根据订单ID删除订单及订单商品集合
	 *
	 * @param orderId
	 */
	@Override
	public void deleteOrderRecord(Long orderId) {
		OrderRecord or = baseMapper.selectById(orderId);
		if(or != null){
			orderGoodsMapper.delete(Wrappers.<OrderGoods>lambdaQuery().eq(OrderGoods::getOrderRecordId, or.getId()));
			baseMapper.deleteById(orderId);
		}
	}
}
