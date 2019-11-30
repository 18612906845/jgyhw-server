package cn.com.jgyhw.goods.entity;

import lombok.Data;

/**
 * 京东商品信息
 *
 * Created by WangLei on 2019/11/21 0021 22:53
 */
@Data
public class JdGoods extends BaseGoods {

	/**
	 * 商品落地页URL
	 */
	private String materialUrl;

	/**
	 * 是否自营(1:是,0:否)
	 */
	private Integer isJdSale;
}
