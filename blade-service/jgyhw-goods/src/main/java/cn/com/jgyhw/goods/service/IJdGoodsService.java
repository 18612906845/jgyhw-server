package cn.com.jgyhw.goods.service;

import cn.com.jgyhw.goods.entity.JdGoods;

/**
 * 京东商品信息服务类
 *
 * Created by WangLei on 2019/11/21 0021 22:56
 */
public interface IJdGoodsService {

	/**
	 * 保存京东商品信息
	 *
	 * @param jdGoods 京东商品信息对象
	 * @return
	 */
	boolean saveJdGoods(JdGoods jdGoods);

	/**
	 * 查询京东商品信息
	 *
	 * @param goodsId 商品编号
	 * @return
	 */
	JdGoods queryJdGoodsByGoodsId(String goodsId);
}
