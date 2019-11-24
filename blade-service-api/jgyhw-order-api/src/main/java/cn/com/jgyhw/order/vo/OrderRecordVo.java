package cn.com.jgyhw.order.vo;

import cn.com.jgyhw.order.entity.OrderGoods;
import cn.com.jgyhw.order.entity.OrderRecord;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 订单记录Vo对象
 *
 * Created by WangLei on 2019/11/23 0023 18:29
 */
@Data
@ApiModel(value = "订单记录Vo对象", description = "订单记录Vo对象")
public class OrderRecordVo extends OrderRecord {

	/**
	 * 订单商品集合
	 */
	@ApiModelProperty(value = "订单商品集合")
	List<OrderGoods> orderGoodsList = new ArrayList<>();
}
