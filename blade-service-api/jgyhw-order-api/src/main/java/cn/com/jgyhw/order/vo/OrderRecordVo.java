package cn.com.jgyhw.order.vo;

import cn.com.jgyhw.order.entity.OrderGoods;
import cn.com.jgyhw.order.entity.OrderRecord;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 订单记录Vo对象
 *
 * Created by WangLei on 2019/11/23 0023 18:29
 */
@Data
public class OrderRecordVo extends OrderRecord {

	/**
	 * 订单商品集合
	 */
	List<OrderGoods> orderGoodsList = new ArrayList<>();

	/**
	 * 订单状态，1：待付款，2：已付款，3：已取消，4：已成团，5：已完成，6：已入账，7：无效
	 */
	private String statusName;

	/**
	 * 是否多商品
	 */
	private boolean isMultipartiteGoods;
}
