package cm.com.jgyhw.goods.service;

import cn.com.jgyhw.goods.entity.JdGoods;
import jd.union.open.promotion.common.get.request.PromotionCodeReq;

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

	/**
	 * 根据参数查询京东推广链接
	 *
	 * @param promotionCodeReq 推广链接查询参数
	 *                         materialId：推广物料
	 *                         siteId：站点ID是指在联盟后台的推广管理中的网站Id
	 *                         ext1：推客生成推广链接时传入的扩展字段
	 *                         positionId: 推广位ID
	 * @return
	 */
	String queryJdCpsUrl(PromotionCodeReq promotionCodeReq);
}
