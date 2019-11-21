package cm.com.jgyhw.goods.service;

import cn.com.jgyhw.goods.entity.JdGoods;

/**
 * 京东商品Api服务类
 *
 * Created by WangLei on 2019/11/21 0021 23:35
 */
public interface IJdGoodsApiService {

	/**
	 * 请求京东Api获取商品信息
	 *
	 * @param goodsId 商品编号
	 * @param returnMoneyShare 返现比例
	 * @return
	 */
	JdGoods reqJdApiGetJdGoodsByGoodsId(String goodsId, Integer returnMoneyShare);
}
