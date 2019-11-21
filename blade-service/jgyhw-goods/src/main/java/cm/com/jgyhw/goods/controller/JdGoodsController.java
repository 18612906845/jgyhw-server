package cm.com.jgyhw.goods.controller;

import cm.com.jgyhw.goods.service.IJdGoodsApiService;
import cm.com.jgyhw.goods.service.IJdGoodsService;
import cn.com.jgyhw.goods.entity.JdGoods;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiOperationSupport;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springblade.core.tool.api.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 京东商品控制器
 *
 * Created by WangLei on 2019/11/22 0022 00:37
 */
@Slf4j
@RefreshScope
@RestController
@RequestMapping("/jdGoods")
@Api(value = "京东商品", tags = "京东商品")
public class JdGoodsController {

	@Autowired
	private IJdGoodsApiService jdGoodsApiService;

	@Resource(name = "jdGoodsServiceRedisImpl")
	private IJdGoodsService jdGoodsServiceRedis;

	/**
	 * 根据商品编号、返现比例查询京东商品信息
	 *
	 * @param goodsId 商品编号
	 * @param returnMoneyShare 返现比例
	 * @return
	 */
	@GetMapping("/findJdGoodsByGoodsIdAndReturnMoneyShare")
	@ApiOperationSupport(order = 1)
	@ApiOperation(value = "根据商品编号、返现比例查询京东商品信息", notes = "")
	public R<JdGoods> findJdGoodsByGoodsIdAndReturnMoneyShare(@ApiParam(value = "商品编号", required = true) String goodsId, @ApiParam(value = "返现比例") Integer returnMoneyShare){
		JdGoods jdGoods = jdGoodsApiService.reqJdApiGetJdGoodsByGoodsId(goodsId, returnMoneyShare);
		return R.data(jdGoods);
	}

	/**
	 * 根据商品编号查询京东商品信息
	 *
	 * @param goodsId 商品编号
	 * @return
	 */
	@GetMapping("/findJdGoodsByGoodsId")
	@ApiOperationSupport(order = 2)
	@ApiOperation(value = "根据商品编号查询京东商品信息", notes = "")
	public R<JdGoods> findJdGoodsByGoodsId(@ApiParam(value = "商品编号", required = true) String goodsId){
		JdGoods jdGoods = jdGoodsServiceRedis.queryJdGoodsByGoodsId(goodsId);
		return R.data(jdGoods);
	}
}
