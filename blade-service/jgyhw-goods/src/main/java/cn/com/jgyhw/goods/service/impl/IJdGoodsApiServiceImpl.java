package cn.com.jgyhw.goods.service.impl;

import cn.com.jgyhw.goods.service.IJdGoodsApiService;
import cn.com.jgyhw.goods.service.IJdGoodsService;
import cn.com.jgyhw.goods.entity.JdGoods;
import com.alibaba.fastjson.JSON;
import com.jd.open.api.sdk.DefaultJdClient;
import com.jd.open.api.sdk.JdClient;
import com.jd.open.api.sdk.JdException;
import jd.union.open.goods.promotiongoodsinfo.query.request.UnionOpenGoodsPromotiongoodsinfoQueryRequest;
import jd.union.open.goods.promotiongoodsinfo.query.response.PromotionGoodsResp;
import jd.union.open.goods.promotiongoodsinfo.query.response.UnionOpenGoodsPromotiongoodsinfoQueryResponse;
import jd.union.open.promotion.common.get.request.PromotionCodeReq;
import jd.union.open.promotion.common.get.request.UnionOpenPromotionCommonGetRequest;
import jd.union.open.promotion.common.get.response.PromotionCodeResp;
import jd.union.open.promotion.common.get.response.UnionOpenPromotionCommonGetResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springblade.common.constant.JdParamConstant;
import org.springblade.common.tool.CommonUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

/**
 * 京东商品信息实现类-Redis
 *
 * Created by WangLei on 2019/11/21 0021 23:04
 */
@Slf4j
@Service
public class IJdGoodsApiServiceImpl implements IJdGoodsApiService {

	@Resource(name = "jdGoodsServiceRedisImpl")
	private IJdGoodsService jdGoodsServiceRedis;

	/**
	 * 请求京东Api获取商品信息
	 *
	 * @param goodsId 商品编号
	 * @param returnMoneyShare 返现比例
	 * @return
	 */
	@Override
	public JdGoods reqJdApiGetJdGoodsByGoodsId(String goodsId, Integer returnMoneyShare) {
		if(StringUtils.isBlank(goodsId)){
			return null;
		}
		JdClient client = new DefaultJdClient(JdParamConstant.API_SERVER_URL, "", JdParamConstant.APP_KEY, JdParamConstant.APP_SECRET);
		UnionOpenGoodsPromotiongoodsinfoQueryRequest request = new UnionOpenGoodsPromotiongoodsinfoQueryRequest();
		request.setSkuIds(goodsId);
		JdGoods jdGoods = null;
		try {
			UnionOpenGoodsPromotiongoodsinfoQueryResponse response = client.execute(request);
			if(response.getCode().equals(200)){
				PromotionGoodsResp[] pgrArray = response.getData();
				if(pgrArray != null && pgrArray.length > 0){
					PromotionGoodsResp pgr = pgrArray[0];
					if(pgr == null){
						return jdGoods;
					}
					jdGoods = new JdGoods();
					jdGoods.setGoodsId(goodsId);
					jdGoods.setGoodsName(pgr.getGoodsName());
					jdGoods.setGoodsImgUrl(pgr.getImgUrl());
					jdGoods.setSalesVolume(pgr.getInOrderCount());
					jdGoods.setPrice(pgr.getUnitPrice());
					jdGoods.setCommissionRate(pgr.getCommisionRatioWl());
					// 计算联盟返利
					Double commission = CommonUtil.rebateCompute(pgr.getUnitPrice(), pgr.getCommisionRatioWl());
					jdGoods.setCommission(CommonUtil.formatDouble(commission));
					// 计算用户返利
					Double returnMoney = CommonUtil.rebateCompute(pgr.getUnitPrice(), pgr.getCommisionRatioWl(), returnMoneyShare);
					jdGoods.setReturnMoney(CommonUtil.formatDouble(returnMoney));
					jdGoods.setUpdateTime(new Date());
					jdGoods.setMaterialUrl(pgr.getMaterialUrl());
					jdGoods.setIsJdSale(pgr.getIsJdSale());
					jdGoodsServiceRedis.saveJdGoods(jdGoods);
				}else{
					log.info("请求京东Api获取商品信息无结果，商品编号：" + goodsId);
				}
			}else{
				log.warn("请求京东Api获取商品信息失败，商品编号：" + goodsId + "；错误编号：" + response.getCode() + "；错误描述：" + response.getMessage());
			}
		} catch (JdException e) {
			log.error("请求京东Api获取商品信息异常，商品编号：" + goodsId, e);
		}
		return jdGoods;
	}

	/**
	 * 根据参数查询京东推广链接
	 *
	 * @param promotionCodeReq 推广链接查询参数
	 * @return
	 */
	@Override
	public String queryJdCpsUrl(PromotionCodeReq promotionCodeReq) {
		if(promotionCodeReq == null){
			return null;
		}
		log.info("根据参数查询京东推广链接，参数：" + JSON.toJSONString(promotionCodeReq));
		String jdCpsUrl = null;
		JdClient client = new DefaultJdClient(JdParamConstant.API_SERVER_URL, "", JdParamConstant.APP_KEY, JdParamConstant.APP_SECRET);
		UnionOpenPromotionCommonGetRequest request = new UnionOpenPromotionCommonGetRequest();
		request.setPromotionCodeReq(promotionCodeReq);
		try {
			UnionOpenPromotionCommonGetResponse response = client.execute(request);
			if(response.getCode().equals(200)){
				PromotionCodeResp pcr =  response.getData();
				if(pcr != null){
					jdCpsUrl = pcr.getClickURL();
				}
			}else{
				log.warn("根据参数查询京东推广链接失败，参数：" + promotionCodeReq.toString() + "；错误编号：" + response.getCode() + "；错误描述：" + response.getMessage());
			}
		} catch (JdException e) {
			log.error("根据参数查询京东推广链接，参数：" + promotionCodeReq.toString(), e);
		}
		log.info("根据参数查询京东推广链接，连接信息：" + jdCpsUrl);
		return jdCpsUrl;
	}
}
