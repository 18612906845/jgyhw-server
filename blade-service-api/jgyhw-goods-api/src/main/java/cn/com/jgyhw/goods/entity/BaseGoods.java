package cn.com.jgyhw.goods.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springblade.core.mp.base.BaseEntity;

import java.util.Date;

/**
 *
 * 商品基础信息
 *
 * Created by WangLei on 2019/11/21 0021 22:47
 */
@Data
@ApiModel(value = "商品基础信息对象", description = "商品基础信息对象")
public class BaseGoods extends BaseEntity {

	/**
	 * 商品编号
	 */
	@ApiModelProperty(value = "商品编号")
	private String goodsId;

	/**
	 * 商品名称
	 */
	@ApiModelProperty(value = "商品名称")
	private String goodsName;

	/**
	 * 商品主图地址
	 */
	@ApiModelProperty(value = "商品主图地址")
	private String goodsImgUrl;

	/**
	 * 销售数量
	 */
	@ApiModelProperty(value = "销售数量")
	private Long salesVolume;

	/**
	 * 价格（原价）
	 */
	@ApiModelProperty(value = "价格（原价）")
	private Double price;

	/**
	 * 联盟佣金
	 */
	@ApiModelProperty(value = "联盟佣金")
	private Double commission;

	/**
	 * 联盟佣金比例
	 */
	@ApiModelProperty(value = "联盟佣金比例")
	private Double commissionRate;

	/**
	 * 返现金额
	 */
	@ApiModelProperty(value = "返现金额")
	private Double returnMoney;

	/**
	 * 更新时间
	 */
	@ApiModelProperty(value = "更新时间")
	private Date updateTime;
}
