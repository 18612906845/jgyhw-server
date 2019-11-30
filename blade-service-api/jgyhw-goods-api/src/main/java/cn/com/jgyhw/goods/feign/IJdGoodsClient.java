package cn.com.jgyhw.goods.feign;

import cn.com.jgyhw.goods.entity.JdGoods;
import cn.com.jgyhw.goods.vo.JdGoodsVo;
import org.springblade.common.constant.JgyhwConstant;
import org.springblade.core.tool.api.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 京东商品 Feign接口类
 *
 * Created by WangLei on 2019/11/23 0023 01:38
 */
@FeignClient(
	value = JgyhwConstant.APPLICATION_GOODS_NAME,
	fallback = IJdGoodsClientFallback.class
)
public interface IJdGoodsClient {

	String API_PREFIX = "/jdGoods";

	/**
	 * 根据商品编号获取商品主图地址
	 *
	 * @param goodsId 商品编号
	 * @return
	 */
	@GetMapping(API_PREFIX + "/findJdGoodsImgUrl")
	R<String> findJdGoodsImgUrl(@RequestParam("goodsId") String goodsId);

	/**
	 * 根据商品编号查询京东商品信息（缓存）
	 *
	 * @param goodsId 商品编号
	 * @return
	 */
	@GetMapping(API_PREFIX + "/findJdGoodsCacheByGoodsId")
	R<JdGoods> findJdGoodsCacheByGoodsId(@RequestParam("goodsId") String goodsId);

	/**
	 * 根据关键词查询京东推广信息（包含商品信息和推广链接）
	 *
	 * @param keyword 关键字
	 * @param wxUserId 微信用户标识
	 * @param returnMoneyShare 返现比例
	 * @return
	 */
	@GetMapping(API_PREFIX + "/findJdCpsInfoByKeyword")
	R<JdGoodsVo> findJdCpsInfoByKeyword(@RequestParam("keyword") String keyword, @RequestParam("wxUserId") String wxUserId, @RequestParam("returnMoneyShare") Integer returnMoneyShare);
}
