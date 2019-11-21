package cn.com.jgyhw.goods.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 京东商品信息
 *
 * Created by WangLei on 2019/11/21 0021 22:53
 */
@Data
@ApiModel(value = "京东商品信息对象", description = "京东商品信息对象")
public class JdGoods extends BaseGoods {

	/**
	 * 商品落地页URL
	 */
	@ApiModelProperty(value = "商品落地页URL")
	private String materialUrl;

	/**
	 * 是否自营(1:是,0:否)
	 */
	@ApiModelProperty(value = "是否自营(1:是,0:否)")
	private Integer isJdSale;
}
