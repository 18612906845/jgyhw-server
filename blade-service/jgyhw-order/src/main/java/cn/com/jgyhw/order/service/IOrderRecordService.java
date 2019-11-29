package cn.com.jgyhw.order.service;

import cn.com.jgyhw.order.entity.OrderRecord;
import cn.com.jgyhw.order.vo.OrderRecordVo;
import org.springblade.core.mp.base.BaseService;

/**
 * 订单记录服务类
 *
 * Created by WangLei on 2019/11/23 0023 18:34
 */
public interface IOrderRecordService extends BaseService<OrderRecord> {

	/**
	 * 保存订单记录及订单商品集合
	 *
	 * @param orderRecordVo 订单记录视图对象
	 */
	void saveOrderRecord(OrderRecordVo orderRecordVo);

	/**
	 * 根据订单ID删除订单及订单商品集合
	 */
	void deleteOrderRecord(Long orderId);


}
