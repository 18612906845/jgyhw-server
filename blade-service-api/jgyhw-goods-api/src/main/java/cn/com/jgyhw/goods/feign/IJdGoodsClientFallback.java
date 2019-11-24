package cn.com.jgyhw.goods.feign;

import cn.com.jgyhw.goods.entity.JdGoods;
import cn.com.jgyhw.goods.vo.JdGoodsVo;
import org.springblade.core.tool.api.R;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Feign失败配置
 *
 * Created by WangLei on 2019/11/23 0023 01:42
 */
@Component
public class IJdGoodsClientFallback implements IJdGoodsClient {
	/**
	 * 根据商品编号获取商品主图地址
	 *
	 * @param goodsId 商品编号
	 * @return
	 */
	@Override
	public R<String> findJdGoodsImgUrl(@RequestParam("goodsId") String goodsId) {
		return R.fail(400,"Feign未获取到商品主图地址");
	}

	/**
	 * 根据商品编号查询京东商品信息（缓存）
	 *
	 * @param goodsId 商品编号
	 * @return
	 */
	@Override
	public R<JdGoods> findJdGoodsCacheByGoodsId(@RequestParam("goodsId") String goodsId) {
		return R.fail(400,"Feign未获取到京东商品信息");
	}

	/**
	 * 根据商品编号、微信用户标识获取京东商品推广信息（包含商品信息和推广链接）
	 *
	 * @param goodsId          商品编号
	 * @param wxUserId         微信用户标识
	 * @param returnMoneyShare 返现比例
	 * @return
	 */
	@Override
	public R<JdGoodsVo> findJdCpsInfo(@RequestParam("goodsId") String goodsId, @RequestParam("wxUserId") String wxUserId, @RequestParam("returnMoneyShare") Integer returnMoneyShare) {
		return R.fail(400,"Feign未获取到京东商品推广信息");
	}
}
