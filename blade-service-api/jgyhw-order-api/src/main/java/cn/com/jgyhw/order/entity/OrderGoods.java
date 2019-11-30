package cn.com.jgyhw.order.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import org.springblade.core.mp.base.BaseEntity;

/**
 * 订单商品
 *
 * Created by WangLei on 2019/11/23 0023 18:21
 */
@Data
@TableName("jgyhw_order_goods")
public class OrderGoods extends BaseEntity {

	/**
	 * 标识
	 */
	@JsonSerialize(using = ToStringSerializer.class)
	@TableId(value = "og_id", type = IdType.ID_WORKER)
	private Long ogId;

	/**
	 * 商品编号
	 */
	private String code;

	/**
	 * 商品名称
	 */
	private String name;

	/**
	 * 商品图片地址
	 */
	private String imageUrl;

	/**
	 * 订单记录标识
	 */
	@JsonSerialize(using = ToStringSerializer.class)
	private Long orderRecordId;

	/**
	 * 返现比例
	 */
	private Integer returnScale;
}
