package cn.com.jgyhw.order.service.impl;

import cn.com.jgyhw.goods.entity.JdGoods;
import cn.com.jgyhw.goods.feign.IJdGoodsClient;
import cn.com.jgyhw.message.feign.IWxGzhMessageClient;
import cn.com.jgyhw.order.entity.OrderGoods;
import cn.com.jgyhw.order.entity.OrderRecord;
import cn.com.jgyhw.order.enums.OrderEnum;
import cn.com.jgyhw.order.service.IJdOrderApiService;
import cn.com.jgyhw.order.service.IOrderGoodsService;
import cn.com.jgyhw.order.service.IOrderRecordService;
import cn.com.jgyhw.order.vo.OrderRecordVo;
import cn.com.jgyhw.order.vo.UpdateOrderRespVo;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.jd.open.api.sdk.DefaultJdClient;
import com.jd.open.api.sdk.JdClient;
import jd.union.open.order.query.request.OrderReq;
import jd.union.open.order.query.request.UnionOpenOrderQueryRequest;
import jd.union.open.order.query.response.OrderResp;
import jd.union.open.order.query.response.SkuInfo;
import jd.union.open.order.query.response.UnionOpenOrderQueryResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springblade.common.constant.JdParamConstant;
import org.springblade.common.tool.CommonUtil;
import org.springblade.core.tool.api.R;
import org.springblade.system.user.entity.WxUser;
import org.springblade.system.user.feign.IWxUserClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cglib.beans.BeanCopier;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by WangLei on 2019/11/23 0023 18:41
 */
@Slf4j
@RefreshScope
@Service
public class JdOrderApiServiceImpl implements IJdOrderApiService {

	@Value("${jgyhw.system.returnMoneyWxUserIdDefault}")
	private Long returnMoneyWxUserIdDefault;

	@Value("${jgyhw.system.returnMoneyShare}")
	private Integer systemReturnMoneyShare;

	@Autowired
	private IOrderRecordService orderRecordService;

	@Autowired
	private IOrderGoodsService orderGoodsService;

	@Autowired
	private IWxUserClient wxUserClient;

	@Autowired
	private IJdGoodsClient jdGoodsClient;

	@Autowired
	private IWxGzhMessageClient wxGzhMessageSendService;

	/**
	 * 更新京东订单信息
	 *
	 * @param queryTimeStr  查询时间字符串，查询时间,输入格式必须为yyyyMMddHHmm,yyyyMMddHHmmss或者yyyyMMddHH格式之一
	 * @param queryTimeType 查询时间类型，1：下单时间，2：完成时间，3：更新时间
	 * @param isUnfreeze    是否解冻
	 * @param pageNum       页码
	 * @param pageSize      每页条数
	 */
	@Override
	public UpdateOrderRespVo updateJdOrderInfoByTime(String queryTimeStr, int queryTimeType, boolean isUnfreeze, int pageNum, int pageSize) {
		log.info("更新京东订单信息，参数：查询时间：" + queryTimeStr + "；查询时间类型：" + queryTimeType +  "；是否解冻：" + isUnfreeze + " ；页数：" + pageNum + "；每页条数：" + pageSize);
		UpdateOrderRespVo uorVo = new UpdateOrderRespVo();

		JdClient client = new DefaultJdClient(JdParamConstant.API_SERVER_URL, "", JdParamConstant.APP_KEY, JdParamConstant.APP_SECRET);
		UnionOpenOrderQueryRequest unionOpenOrderQueryRequest = new UnionOpenOrderQueryRequest();
		OrderReq orderReq = new OrderReq();
		orderReq.setPageNo(pageNum);
		orderReq.setPageSize(pageSize);
		orderReq.setType(queryTimeType);
		orderReq.setTime(queryTimeStr);
		unionOpenOrderQueryRequest.setOrderReq(orderReq);

		try {
			UnionOpenOrderQueryResponse response = client.execute(unionOpenOrderQueryRequest);
			if(response.getCode() == 200){
				if(response.getHasMore() == null){
					log.info("更新京东订单信息，无数据");
					uorVo.setMore(false);
					uorVo.setStatus(true);
					return uorVo;
				}
				uorVo.setMore(response.getHasMore());
				OrderResp[] orArray = response.getData();
				if(orArray == null || orArray.length < 1){
					log.info("更新京东订单信息，查询到" + 0 + "条订单记录");
					uorVo.setStatus(true);
					uorVo.setMore(false);
					return uorVo;
				}
				log.info("更新京东订单信息，查询到" + orArray.length + "条订单记录");
				for(OrderResp or : orArray){
					int validCode = or.getValidCode();
					if(OrderEnum.JD_VALID_CODE_WXCD.getKey() == validCode){//无效-拆单
						invalidSeparateOrderDispose(or);
					}else if(OrderEnum.JD_VALID_CODE_WXQX.getKey() == validCode){//无效-取消
						invalidCancelOrderDispose(or);
					}else if(OrderEnum.JD_VALID_CODE_DFK.getKey() == validCode){//待付款
						awaitPayOrderDispose(or);
					}else if(OrderEnum.JD_VALID_CODE_YFK.getKey() == validCode){//已付款
						finishPayOrderDispose(or);
					}else if(OrderEnum.JD_VALID_CODE_YWC.getKey() == validCode){//已完成
						finishOrderDispose(or, isUnfreeze);
					}else if(validCode > OrderEnum.JD_VALID_CODE_WXQX.getKey() && validCode < OrderEnum.JD_VALID_CODE_DFK.getKey()){// 其他无效，删除订单
						invalidSeparateOrderDispose(or);
					}
				}
				log.info("更新京东订单信息，处理完成");
				uorVo.setStatus(true);
			}else{
				log.error("更新京东订单错误，Code：" + response.getCode() + "；message：" + response.getMessage());
			}
		}catch (Exception e){
			uorVo.setStatus(false);
			uorVo.setMore(true);
			log.error("更新京东订单信息异常", e);
		}finally {
			log.info("更新京东订单信息结束");
			return uorVo;
		}
	}

	/**
	 * 无效-拆单订单处理
	 *
	 * @param orderResp 订单对象
	 */
	private void invalidSeparateOrderDispose(OrderResp orderResp){
		Long orderId = orderResp.getOrderId();
		// 查询订单是否存在
		OrderRecord or = orderRecordService.getOne(Wrappers.<OrderRecord>lambdaQuery().eq(OrderRecord::getOrderId, orderId).eq(OrderRecord::getPlatform, OrderEnum.ORDER_PLATFORM_JD.getKey()));
		if(or != null){
			// 删除订单及订单商品信息
			orderRecordService.deleteOrderRecord(or.getId());
		}
		log.info("京东无效-拆单订单处理，删除订单记录，编号：" + orderId + "，平台：京东");
	}

	/**
	 * 无效-取消订单处理
	 *
	 * @param orderResp 订单对象
	 */
	private void invalidCancelOrderDispose(OrderResp orderResp){
		Long orderId = orderResp.getOrderId();
		// 查询订单是否存在
		OrderRecord or = orderRecordService.getOne(Wrappers.<OrderRecord>lambdaQuery().eq(OrderRecord::getOrderId, orderId).eq(OrderRecord::getPlatform, OrderEnum.ORDER_PLATFORM_JD.getKey()));
		if(or == null){//新建订单
			or = new OrderRecord();
			or.setStatus(OrderEnum.ORDER_STATUS_YQX.getKey());
			createOrderRecordByOrderResp(or, orderResp);
			log.info("京东无效-取消订单处理，无订单记录，新建订单，编号：" + orderId);
		}else{// 更改状态
			or.setStatus(OrderEnum.ORDER_STATUS_YQX.getKey());
			updateOrderRecordByOrderResp(or, orderResp, false);
			log.info("京东无效-取消订单处理，有订单记录，更新订单，编号：" + orderId);
		}
	}

	/**
	 * 待支付订单处理
	 *
	 * @param orderResp 订单对象
	 */
	private void awaitPayOrderDispose(OrderResp orderResp){
		Long orderId = orderResp.getOrderId();
		// 查询订单是否存在
		OrderRecord or = orderRecordService.getOne(Wrappers.<OrderRecord>lambdaQuery().eq(OrderRecord::getOrderId, orderId).eq(OrderRecord::getPlatform, OrderEnum.ORDER_PLATFORM_JD.getKey()));
		if(or == null){// 新建订单
			or = new OrderRecord();
			or.setStatus(OrderEnum.ORDER_STATUS_DFK.getKey());
			createOrderRecordByOrderResp(or, orderResp);
			// 判断用户是否关注公众号
			R<WxUser> wxUserR = wxUserClient.findWxUserById(or.getWxUserId());
			if(wxUserR.getCode() == 200){
				WxUser wu = wxUserR.getData();
				if(wu != null && StringUtils.isNotBlank(wu.getOpenIdGzh())){
					wxGzhMessageSendService.sendAffirmOrderWxMessage(wu.getOpenIdGzh(), or.getOrderId(), "待付款");
				}
			}
			log.info("京东待支付订单处理，无订单记录，新建订单，编号：" + orderId);
		}
	}

	/**
	 * 已支付订单处理
	 *
	 * @param orderResp 订单对象
	 */
	private void finishPayOrderDispose(OrderResp orderResp){
		Long orderId = orderResp.getOrderId();
		// 查询订单是否存在
		OrderRecord or = orderRecordService.getOne(Wrappers.<OrderRecord>lambdaQuery().eq(OrderRecord::getOrderId, orderId).eq(OrderRecord::getPlatform, OrderEnum.ORDER_PLATFORM_JD.getKey()));
		if(or == null){// 新建订单
			or = new OrderRecord();
			or.setStatus(OrderEnum.ORDER_STATUS_YFK.getKey());
			createOrderRecordByOrderResp(or, orderResp);
			// 判断用户是否关注公众号
			R<WxUser> wxUserR = wxUserClient.findWxUserById(or.getWxUserId());
			if(wxUserR.getCode() == 200){
				WxUser wu = wxUserR.getData();
				if(wu != null && StringUtils.isNotBlank(wu.getOpenIdGzh())){
					wxGzhMessageSendService.sendAffirmOrderWxMessage(wu.getOpenIdGzh(), or.getOrderId(), "已付款");
				}
			}
			log.info("京东已支付订单处理，无订单记录，新建订单，编号：" + orderId);
		}else{
			if(or.getStatus().equals(OrderEnum.ORDER_STATUS_DFK.getKey())){// 若是待付款订单，则修改为已付款
				Integer updateBeforeStatus = or.getStatus(); // 修改之前订单状态
				or.setStatus(OrderEnum.ORDER_STATUS_YFK.getKey());
				updateOrderRecordByOrderResp(or, orderResp, false);
				// 如果修改之前订单状态已经是已付款，则不发送消息
				if(!updateBeforeStatus.equals(OrderEnum.ORDER_STATUS_YFK.getKey())) {
					// 判断用户是否关注公众号
					R<WxUser> wxUserR = wxUserClient.findWxUserById(or.getWxUserId());
					if(wxUserR.getCode() == 200){
						WxUser wu = wxUserR.getData();
						if(wu != null && StringUtils.isNotBlank(wu.getOpenIdGzh())){
							wxGzhMessageSendService.sendAffirmOrderWxMessage(wu.getOpenIdGzh(), or.getOrderId(), "已付款");
						}
					}
				}
				log.info("京东已支付订单处理，有订单记录，更新订单，编号：" + orderId);
			}
		}
	}

	/**
	 * 完成订单处理
	 *
	 * @param orderResp 订单对象
	 * @param isUnfreeze 是否解冻
	 */
	private void finishOrderDispose(OrderResp orderResp, boolean isUnfreeze){
		Long orderId = orderResp.getOrderId();
		// 查询订单是否存在
		OrderRecord or = orderRecordService.getOne(Wrappers.<OrderRecord>lambdaQuery().eq(OrderRecord::getOrderId, orderId).eq(OrderRecord::getPlatform, OrderEnum.ORDER_PLATFORM_JD.getKey()));
		if(or == null){// 新建订单
			or = new OrderRecord();
			or.setStatus(OrderEnum.ORDER_STATUS_YWC.getKey());
			createOrderRecordByOrderResp(or, orderResp);
			or.setFinishTime(new Date(orderResp.getFinishTime()));
			// 判断用户是否关注公众号
			R<WxUser> wxUserR = wxUserClient.findWxUserById(or.getWxUserId());
			if(wxUserR.getCode() == 200){
				WxUser wu = wxUserR.getData();
				if(wu != null && StringUtils.isNotBlank(wu.getOpenIdGzh())){
					wxGzhMessageSendService.sendFinishOrderWxMessage(wu.getOpenIdGzh(), or.getOrderId(), or.getFinishTime());
				}
			}
			log.info("京东完成订单处理，无订单记录，新建订单，编号：" + orderId);
		}else{// 更改状态
			or.setFinishTime(new Date(orderResp.getFinishTime()));
			if(!or.getStatus().equals(OrderEnum.ORDER_STATUS_YQX.getKey()) &&
				!or.getStatus().equals(OrderEnum.ORDER_STATUS_WX.getKey()) &&
				!or.getStatus().equals(OrderEnum.ORDER_STATUS_YRZ.getKey())){// 跳过取消/无效/已入账订单
				Integer updateBeforeStatus = or.getStatus(); // 修改之前订单状态
				or.setStatus(OrderEnum.ORDER_STATUS_YWC.getKey());
				updateOrderRecordByOrderResp(or, orderResp, true);
				// 如果修改之前订单状态已经是已完成，则不发送消息
				if(!updateBeforeStatus.equals(OrderEnum.ORDER_STATUS_YWC.getKey())){
					// 判断用户是否关注公众号
					R<WxUser> wxUserR = wxUserClient.findWxUserById(or.getWxUserId());
					if(wxUserR.getCode() == 200){
						WxUser wu = wxUserR.getData();
						if(wu != null && StringUtils.isNotBlank(wu.getOpenIdGzh())){
							wxGzhMessageSendService.sendFinishOrderWxMessage(wu.getOpenIdGzh(), or.getOrderId(), or.getFinishTime());
						}
					}
				}
				log.info("京东完成订单处理，有订单记录，更新订单，编号：" + orderId);
			}
		}
		if(isUnfreeze){
//			unfreezeOrder(or, orderResp);
		}
	}

	/**
	 * 订单解冻
	 *
	 * @param orderResp 订单对象
	 */
	/*private void unfreezeOrder(OrderRecord or, OrderResp orderResp){
		if(or == null){
			log.warn("京东订单结算，数据库无记录");
			return;
		}
		// 根据订单状态判断订单是否可以结算
		if(or.getStatus().equals(OrderEnum.ORDER_STATUS_YRZ.getKey()) ||
			or.getStatus().equals(OrderEnum.ORDER_STATUS_WX.getKey()) ||
			or.getStatus().equals(OrderEnum.ORDER_STATUS_YQX.getKey())){
			return;
		}
		// 更改京东订单状态为已入账
		or.setStatus(OrderEnum.ORDER_STATUS_YRZ.getKey());

		// 更新订单到数据库
		updateOrderRecordByOrderResp(or, orderResp, true);

		// 判断返现金额是否大于零
		if(or.getReturnMoney() <= 0){
			return;
		}

		// 保存购买人入账信息
		Long wxUserId = or.getWxUserId();
		R<WxUser> wxUserR = wxUserClient.findWxUserById(wxUserId);
		if(wxUserR.getCode() == 200){
			WxUser wu = wxUserR.getData();
			if(wu != null && StringUtils.isNotBlank(wu.getOpenIdGzh())){


			}
		}
		WxUser wu = wxUserService.queryWxUserById(wxUserId);
		Integer changeType = Integer.valueOf(ApplicationParamConstant.XT_PARAM_MAP.get("return_money_change_type_rz"));
		ReturnMoneyChangeRecord rmcr = returnMoneyChangeRecordService.queryReturnMoneyChangeRecordByWxUserIdAndChangeTypeAndTargetIdAndOrderId(wxUserId, changeType, or.getId(), or.getOrderId());
		if(rmcr == null){
			rmcr = new ReturnMoneyChangeRecord();
			rmcr.setId(UUID.randomUUID().toString());
			rmcr.setWxUserId(wxUserId);
			rmcr.setChangeType(changeType);
			rmcr.setTargetId(or.getId());
			rmcr.setChangeTime(new Date());
			rmcr.setChangeMoney(or.getReturnMoney());
			rmcr.setOrderId(or.getOrderId());
			returnMoneyChangeRecordService.saveReturnMoneyChangeRecord(rmcr);
			// 发送获得返现消息，判断用户是否关注公众号
			if(wu != null && !StringUtils.isBlank(wu.getOpenIdGzh())){
				this.sendRebateWxMessage(wxGzhMessageSendService, wu.getOpenIdGzh(), "订单（" + or.getOrderId() + "）完成审核，并已入账", rmcr.getChangeMoney());
			}
		}
		// 保存推荐人提成入账信息
		if(wu == null){
			return;
		}
		// 查询推荐人用户信息
		Long parentWxUserId = wu.getParentWxUserId();
		if(parentWxUserId == null){
			return;
		}
		WxUser pwu = wxUserService.queryWxUserById(parentWxUserId);
		if(pwu == null){
			return;
		}
		// 保存推荐人入账信息
		Integer pChangeType = Integer.valueOf(ApplicationParamConstant.XT_PARAM_MAP.get("return_money_change_type_tc"));
		ReturnMoneyChangeRecord pRmcr = returnMoneyChangeRecordService.queryReturnMoneyChangeRecordByWxUserIdAndChangeTypeAndTargetIdAndOrderId(pwu.getId(), pChangeType, String.valueOf(wxUserId), or.getOrderId());
		if(pRmcr == null){
			pRmcr = new ReturnMoneyChangeRecord();
			pRmcr.setId(UUID.randomUUID().toString());
			pRmcr.setWxUserId(pwu.getId());
			pRmcr.setChangeType(pChangeType);
			pRmcr.setTargetId(String.valueOf(wxUserId));
			pRmcr.setChangeTime(new Date());
			// 计算提成金额
			int recommendTcScale = Integer.valueOf(ApplicationParamConstant.XT_PARAM_MAP.get("recommend_tc_scale"));
			if(pwu.getReturnMoneyShareTc() > 0){
				recommendTcScale = pwu.getReturnMoneyShareTc();
			}
			pRmcr.setChangeMoney(this.rebateCompute(or.getCommission(), recommendTcScale));
			pRmcr.setOrderId(or.getOrderId());
			if(pRmcr.getChangeMoney() > 0){// 提成大于0，保存记录并发送通知
				returnMoneyChangeRecordService.saveReturnMoneyChangeRecord(pRmcr);
				// 判断邀请人是否关注公众号
				if(pwu != null && !StringUtils.isBlank(pwu.getOpenIdGzh())){
					this.sendRebateWxMessage(wxGzhMessageSendService, pwu.getOpenIdGzh(), "您邀请的 " + wu.getNickName() + " 完成订单结算，提成奖励已入账", pRmcr.getChangeMoney());
				}
			}
		}
	}*/

	/**
	 * 新建京东订单
	 *
	 * @param orderRecord 订单数据库对象
	 * @param orderResp 订单对象
	 */
	private void createOrderRecordByOrderResp(OrderRecord orderRecord, OrderResp orderResp){
		//保存商品
		SkuInfo[] skuArray = orderResp.getSkuList();
		if(skuArray == null || skuArray.length < 1){
			return;
		}

		Long orderId = orderResp.getOrderId();

		Double orderEstimateFeeCount = 0d;
		Double orderReturnMoneyCount = 0d;

		// 获取扩展字段
		String ext1 = "";
		for(SkuInfo si : skuArray){
			if(StringUtils.isBlank(ext1)){
				ext1 = si.getExt1();
			}
		}
		// 获取返现比例
		int returnScale = getReturnScaleByExt1(ext1);

		List<OrderGoods> ogList = new ArrayList<>();
		int skuArrayLength = skuArray.length;
		Long[] skuIdArray = new Long[skuArrayLength];
		for(int i=0; i<skuArrayLength; i++){
			SkuInfo si = skuArray[i];
			// 预防同订单多个同商品信息
			boolean exist = Arrays.asList(skuIdArray).contains(si.getSkuId());
			skuIdArray[i] = si.getSkuId();
			OrderGoods og = null;
			// 已存在，查询库里信息
			if(exist == false){
				og = orderGoodsService.getOne(Wrappers.<OrderGoods>lambdaQuery().eq(OrderGoods::getOrderRecordId, orderRecord.getId()).eq(OrderGoods::getCode, si.getSkuId()));
			}
			// 不存在
			if(og == null){
				og = new OrderGoods();
				og.setCode(si.getSkuId().toString());
				og.setName(si.getSkuName());
				og.setImageUrl(getGoodsImageUrlBySkuId(si.getSkuId().toString()));
			}
			og.setReturnScale(returnScale);

			double estimateFee = si.getEstimateFee();
			orderEstimateFeeCount += estimateFee;
			// 计算返现
			Double returnMoney = CommonUtil.rebateCompute(estimateFee, returnScale);
			orderReturnMoneyCount += returnMoney;

			// 将商品添加到订单集合
			ogList.add(og);
		}
		// 设置订单
		orderRecord.setOrderId(orderId.toString());
		orderRecord.setOrderTime(new Date(orderResp.getOrderTime()));
		if(orderResp.getFinishTime() != 0){
			orderRecord.setFinishTime(new Date(orderResp.getFinishTime()));
		}
		orderRecord.setCommission(CommonUtil.formatDouble(orderEstimateFeeCount));
		orderRecord.setReturnMoney(CommonUtil.formatDouble(orderReturnMoneyCount));
		orderRecord.setPlatform(OrderEnum.ORDER_PLATFORM_JD.getKey());

		// 获取微信用户标识
		if(StringUtils.isBlank(ext1)){
			orderRecord.setWxUserId(returnMoneyWxUserIdDefault);
		}else{
			orderRecord.setWxUserId(Long.valueOf(ext1));
		}

		OrderRecordVo orVo = new OrderRecordVo();
		BeanCopier copier = BeanCopier.create(OrderRecord.class, OrderRecordVo.class, false);
		copier.copy(orderRecord, orVo, null);
		// 设置订单商品集合
		orVo.setOrderGoodsList(ogList);
		// 保存订单
		orderRecordService.saveOrderRecord(orVo);
	}

	/**
	 * 更新京东订单
	 *
	 * @param orderRecord 订单数据库对象
	 * @param orderResp 订单对象
	 * @param isActualFee 是否实际佣金计算
	 */
	private void updateOrderRecordByOrderResp(OrderRecord orderRecord, OrderResp orderResp, boolean isActualFee){
		//更新商品
		SkuInfo[] skuInfoArray = orderResp.getSkuList();
		if(skuInfoArray == null || skuInfoArray.length < 1){
			return;
		}
		Double orderEstimateFeeCount = 0d;// 联盟佣金总和
		Double orderReturnMoneyCount = 0d;// 返现总和
		boolean isReturnGoods = false;// 是否退货

		int skuInfoArrayLength = skuInfoArray.length;
		Long[] skuIdArray = new Long[skuInfoArrayLength];
		for(int i=0; i<skuInfoArrayLength; i++){
			SkuInfo si = skuInfoArray[i];
			// 判断是否有退货
			if(si.getSkuReturnNum() > 0){
				isReturnGoods = true;
			}

			// 判断商品是否已计算过，是为了避免同一个商品购买多件，会出现重复计算的问题
			boolean exist = Arrays.asList(skuIdArray).contains(si.getSkuId());
			skuIdArray[i] = si.getSkuId();
			if(exist){
				continue;
			}

			// 计算联盟佣金总和
			Double estimateFee = si.getEstimateFee();
			if(isActualFee){
				estimateFee = si.getActualFee();
			}
			orderEstimateFeeCount += estimateFee;

			// 根据订单记录标识和商品编号查询订单商品返现比例
			int goodsReturnScale = systemReturnMoneyShare;
			OrderGoods og = orderGoodsService.getOne(Wrappers.<OrderGoods>lambdaQuery().eq(OrderGoods::getOrderRecordId, orderRecord.getId()).eq(OrderGoods::getCode, si.getSkuId()));
			if(og != null){
				goodsReturnScale = og.getReturnScale();
			}
			// 计算返现总和
			Double returnMoney = CommonUtil.rebateCompute(estimateFee, goodsReturnScale);
			orderReturnMoneyCount += returnMoney;

		}
		// 更新订单
		orderRecord.setCommission(CommonUtil.formatDouble(orderEstimateFeeCount));
		orderRecord.setReturnMoney(CommonUtil.formatDouble(orderReturnMoneyCount));
		orderRecord.setUpdateTime(new Date());

		// 订单入账并返现金额小于等于0
		if(orderRecord.getStatus().equals(OrderEnum.ORDER_STATUS_YRZ.getKey()) && orderRecord.getReturnMoney() <= 0 && isReturnGoods){
			orderRecord.setStatus(OrderEnum.ORDER_STATUS_WX.getKey());// 设置订单状态为无效
		}
		orderRecordService.updateById(orderRecord);
	}

	/**
	 * 根据商品扩展字段获取用户返现比例
	 *
	 * @param ext1 扩展字段
	 * @return
	 */
	private int getReturnScaleByExt1(String ext1){
		log.info("根据商品扩展字段获取用户返现比例，扩展字段：" + ext1);
		int returnScale = systemReturnMoneyShare;
		if(StringUtils.isBlank(ext1)){
			log.info("根据商品扩展字段获取用户返现比例，最终返现比例：" + returnScale + "%");
			return returnScale;
		}
		// 请求用户接口
		Long wxUserId = Long.valueOf(ext1);
		R<WxUser> wxUserR = wxUserClient.findWxUserById(wxUserId);
		if(wxUserR.getCode() == 200){
			WxUser wu = wxUserR.getData();
			if(wu != null && wu.getId() != null && wu.getReturnMoneyShare() > 0){
				returnScale = wu.getReturnMoneyShare();
			}
		}
		log.info("根据商品扩展字段获取用户返现比例，最终返现比例：" + returnScale + "%");
		return returnScale;
	}

	/**
	 * 根据商品编号获取商品图片
	 *
	 * @param goodsId 商品编号
	 * @return
	 */
	private String getGoodsImageUrlBySkuId(String goodsId){
		String goodsImgUrl = "";
		R<JdGoods> jdGoodsR = jdGoodsClient.findJdGoodsCacheByGoodsId(goodsId);
		if(jdGoodsR.getCode() == 200){
			JdGoods jg = jdGoodsR.getData();
			if(jg == null || jg.getGoodsId() == null){
				R<String> imgUrlR = jdGoodsClient.findJdGoodsImgUrl(goodsId);
				if(imgUrlR.getCode() == 200){
					goodsImgUrl = imgUrlR.getData();
				}
			}else{
				goodsImgUrl = jg.getGoodsImgUrl();
			}

		}
		return goodsImgUrl;
	}
}
