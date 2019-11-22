package cm.com.jgyhw.goods.controller;

import cm.com.jgyhw.goods.service.IJdGoodsApiService;
import cm.com.jgyhw.goods.service.IJdGoodsService;
import cn.com.jgyhw.goods.JdGoodsVo;
import cn.com.jgyhw.goods.entity.JdGoods;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiOperationSupport;
import io.swagger.annotations.ApiParam;
import jd.union.open.promotion.common.get.request.PromotionCodeReq;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springblade.core.tool.api.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cglib.beans.BeanCopier;
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

	@Value("${jgyhw.jd.webId}")
	private String jdWebId;

	@Autowired
	private IJdGoodsApiService jdGoodsApiService;

	@Resource(name = "jdGoodsServiceRedisImpl")
	private IJdGoodsService jdGoodsServiceRedis;

	/**
	 * 根据商品编号获取商品主图地址
	 *
	 * @param goodsId 商品编号
	 * @return
	 */
	@GetMapping("/findJdGoodsImgUrl")
	@ApiOperationSupport(order = 1)
	@ApiOperation(value = "根据商品编号获取商品主图地址", notes = "")
	public R<String> findJdGoodsImgUrl(@ApiParam(value = "商品编号", required = true) String goodsId){
		JdGoods jdGoods = jdGoodsApiService.reqJdApiGetJdGoodsByGoodsId(goodsId, null);
		String goodsImgUrl = null;
		if(jdGoods != null){
			goodsImgUrl = jdGoods.getGoodsImgUrl();
		}
		return R.data(goodsImgUrl);
	}

	/**
	 * 根据商品编号查询京东商品信息（缓存）
	 *
	 * @param goodsId 商品编号
	 * @return
	 */
	@GetMapping("/findJdGoodsCacheByGoodsId")
	@ApiOperationSupport(order = 2)
	@ApiOperation(value = "根据商品编号查询京东商品信息（缓存）", notes = "")
	public R<JdGoods> findJdGoodsCacheByGoodsId(@ApiParam(value = "商品编号", required = true) String goodsId){
		JdGoods jdGoods = jdGoodsServiceRedis.queryJdGoodsByGoodsId(goodsId);
		return R.data(jdGoods);
	}

	/**
	 * 根据商品编号、微信用户标识获取京东商品推广信息（包含商品信息和推广链接）
	 *
	 * @param goodsId 商品编号
	 * @param wxUserId 微信用户标识
	 * @param returnMoneyShare 返现比例
	 * @return
	 */
	@GetMapping("/findJdCpsInfo")
	@ApiOperationSupport(order = 3)
	@ApiOperation(value = "根据商品编号、微信用户标识获取京东商品推广链接", notes = "")
	public R<JdGoodsVo> findJdCpsInfo(@ApiParam(value = "商品编号", required = true) String goodsId,
									  @ApiParam(value = "微信用户标识", required = true) String wxUserId,
									  @ApiParam(value = "返现比例", required = true) Integer returnMoneyShare){
		JdGoods jdGoods = jdGoodsApiService.reqJdApiGetJdGoodsByGoodsId(goodsId, returnMoneyShare);
		if(jdGoods == null || StringUtils.isBlank(wxUserId)){
			return R.data(null);
		}
		PromotionCodeReq pcr = new PromotionCodeReq();
		pcr.setExt1(wxUserId);
		pcr.setMaterialId(jdGoods.getMaterialUrl());
		pcr.setSiteId(jdWebId);
		String cpsUrl = jdGoodsApiService.queryJdCpsUrl(pcr);

		JdGoodsVo jgVo = new JdGoodsVo();
		BeanCopier copier = BeanCopier.create(JdGoods.class, JdGoodsVo.class, false);
		copier.copy(jdGoods, jgVo, null);
		jgVo.setCpsUrl(cpsUrl);
		return R.data(jgVo);
	}
}
