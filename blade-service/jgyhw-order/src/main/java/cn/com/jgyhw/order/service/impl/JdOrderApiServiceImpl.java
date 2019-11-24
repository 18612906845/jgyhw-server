package cn.com.jgyhw.order.service.impl;

import cn.com.jgyhw.account.entity.MoneyAccount;
import cn.com.jgyhw.account.enums.AccountEnum;
import cn.com.jgyhw.account.feign.IMoneyAccountClient;
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
import cn.com.jgyhw.order.vo.ReturnMoneyScaleVo;
import cn.com.jgyhw.order.vo.UpdateOrderRespVo;
import com.alibaba.fastjson.JSON;
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

import java.beans.Transient;
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

	@Value("${jgyhw.system.returnMoneyTenantIdDefault}")
	private String returnMoneyTenantIdDefault;

	@Value("${jgyhw.system.returnMoneyShareDefault}")
	private Integer systemReturnMoneyShareDefault;

	@Value("${jgyhw.system.returnMoneyShareTcDefault}")
	private Integer systemReturnMoneyShareTcDefault;

	@Value("${jgyhw.system.returnMoneyShareSyDefault}")
	private Integer systemReturnMoneyShareSyDefault;

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

	@Autowired
	private IMoneyAccountClient moneyAccountClient;

	/**
	 * 更新京东订单信息
	 *
	 * @param queryTimeStr  查询时间字符串，查询时间,输入格式必须为yyyyMMddHHmm,yyyyMMddHHmmss或者yyyyMMddHH格式之一
	 * @param queryTimeType 查询时间类型，1：下单时间，2：完成时间，3：更新时间
	 * @param isUnfreeze    是否解冻
	 * @param pageNum       页码
	 * @param pageSize      每页条数
	 */
	@Transient
	@Override
	public synchronized UpdateOrderRespVo updateJdOrderInfoByTime(String queryTimeStr, int queryTimeType, boolean isUnfreeze, int pageNum, int pageSize) {
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
			unfreezeOrder(or, orderResp);
		}
	}

	/**
	 * 订单解冻
	 *
	 * @param orderResp 订单对象
	 */
	private void unfreezeOrder(OrderRecord or, OrderResp orderResp){
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
		// 判断返现金额是否大于零
		if(or.getReturnMoney() <= 0){
			// 更改京东订单状态为已入账
			or.setStatus(OrderEnum.ORDER_STATUS_YRZ.getKey());
			// 更新订单到数据库
			updateOrderRecordByOrderResp(or, orderResp, true);
			return;
		}
		// 保存购买人入账信息
		Long wxUserId = or.getWxUserId();
		R<WxUser> wxUserR = wxUserClient.findWxUserById(wxUserId);
		if(wxUserR.getCode() == 200 && wxUserR.getData() != null && wxUserR.getData().getId() != null){
			WxUser wu = wxUserR.getData();
			MoneyAccount ma = new MoneyAccount();
			ma.setWxUserId(wu.getId());
			ma.setChangeType(AccountEnum.CHANGE_TYPE_GWFX.getKey());
			ma.setChangeMoney(or.getReturnMoney());
			ma.setTargetJson(JSON.toJSONString(or));
			// 发送购买人入账请求
			R<Boolean> r = moneyAccountClient.addOrReduce(ma, "订单（" + or.getOrderId() + "）完成审核，返现已入账");
			if(r.getCode() == 200 && r.getData() == true){
				// 更改京东订单状态为已入账
				or.setStatus(OrderEnum.ORDER_STATUS_YRZ.getKey());
				// 更新订单到数据库
				updateOrderRecordByOrderResp(or, orderResp, true);
				log.error("发送购买人入账请求成功，订单信息：" + ma.getTargetJson());
			}else{
				log.error("发送购买人入账请求失败，订单信息：" + ma.getTargetJson());
			}

			// 保存推荐人不为空、且推荐人不是管理员的入账信息
			if(wu.getParentWxUserId() != null && !returnMoneyWxUserIdDefault.equals(wu.getParentWxUserId())){
				R<WxUser> parentWxUserR = wxUserClient.findWxUserById(wu.getParentWxUserId());
				if(parentWxUserR.getCode() == 200 && parentWxUserR.getData() != null && parentWxUserR.getData().getId() != null){
					WxUser pwu = parentWxUserR.getData();
					// 设置推荐人提成比例为系统缺省值
					int returnMoneyShareTc = systemReturnMoneyShareTcDefault;
					int changeType = AccountEnum.CHANGE_TYPE_TGTC.getKey();
					String changeTypeName = AccountEnum.CHANGE_TYPE_TGTC.getText();
					if(StringUtils.isNotBlank(pwu.getTenantId())){
						// 推荐人是租户，推广收益，设置为全局收益比例
						returnMoneyShareTc = systemReturnMoneyShareSyDefault;
						changeType = AccountEnum.CHANGE_TYPE_TGSY.getKey();
						changeTypeName = AccountEnum.CHANGE_TYPE_TGSY.getText();
					}else{
						// 推荐人不是租户，推广提成，判断推荐人是否还有推荐人，并且推荐人的推荐人是租户的
						if(pwu.getParentWxUserId() != null){
							R<WxUser> parentParentWxUserR = wxUserClient.findWxUserById(pwu.getParentWxUserId());
							if(parentParentWxUserR.getCode() == 200 && parentParentWxUserR.getData() != null && StringUtils.isNotBlank(parentParentWxUserR.getData().getTenantId())){
								// 推荐人的推荐人是商户
								WxUser ppWu = parentParentWxUserR.getData();
								// 推荐人的推荐人自定义过提成
								if(ppWu.getTenantReturnMoneyShareTc() != null && ppWu.getTenantReturnMoneyShareTc() > 0){
									returnMoneyShareTc = ppWu.getReturnMoneyShareTc();
								}
							}
						}
						// 设置推广提成比例为推荐人自定义比例
						if(pwu.getReturnMoneyShareTc() != null && pwu.getReturnMoneyShareTc() > 0){
							returnMoneyShareTc = pwu.getReturnMoneyShareTc();
						}
					}
					// 计算提成金额
					Double returnMoneyTc = CommonUtil.rebateCompute(or.getCommission() - or.getReturnMoney(), returnMoneyShareTc);
					MoneyAccount pma = new MoneyAccount();
					pma.setWxUserId(pwu.getId());
					pma.setChangeType(changeType);
					pma.setChangeMoney(returnMoneyTc);
					pma.setTargetJson(JSON.toJSONString(wu));
					// 发送推荐人入账请求
					R<Boolean> pr = moneyAccountClient.addOrReduce(pma, "您邀请的 " + wu.getNickName() + " 完成订单结算，" + changeTypeName + "已入账");
					if(pr.getCode() == 200 && pr.getData() == true){
						log.info("发送推荐人入账请求成功，推荐人信息：" + pma.getTargetJson());
					}else{
						log.error("发送推荐人入账请求失败，推荐人信息：" + pma.getTargetJson());
					}
				}else{
					log.error("获取推荐人用户信息失败，订单信息：" + JSON.toJSONString(or));
				}
			}
		}else{
			log.error("获取购买人用户信息失败，订单信息：" + JSON.toJSONString(or));
		}
	}

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
		ReturnMoneyScaleVo rmsVo = getReturnScaleByExt1(ext1);

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
			og.setReturnScale(rmsVo.getReturnScale());

			double estimateFee = si.getEstimateFee();
			orderEstimateFeeCount += estimateFee;
			// 计算返现
			Double returnMoney = CommonUtil.rebateCompute(estimateFee, rmsVo.getReturnScale());
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

		// 设置订单所属用户
		orderRecord.setWxUserId(rmsVo.getWxUserId());
		// 设置订单所属租户
		orderRecord.setTenantId(rmsVo.getParentTenantId());

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
			int goodsReturnScale = systemReturnMoneyShareDefault;
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
	 * 根据商品扩展字段获取用户返现比例和推荐人租户ID
	 *
	 * @param ext1 扩展字段
	 * @return
	 */
	private ReturnMoneyScaleVo getReturnScaleByExt1(String ext1){
		log.info("根据商品扩展字段获取用户返现比例，扩展字段：" + ext1);
		ReturnMoneyScaleVo rmsVo = new ReturnMoneyScaleVo();
		// 设置返现比例为系统缺省值
		rmsVo.setReturnScale(systemReturnMoneyShareDefault);
		// 设置返现接受用户为系统缺省值
		rmsVo.setWxUserId(returnMoneyWxUserIdDefault);
		// 设置返现接受租户为系统缺省值
		rmsVo.setParentTenantId(returnMoneyTenantIdDefault);
		if(StringUtils.isBlank(ext1)){
			log.info("根据商品扩展字段获取用户返现比例，最终返现比例：" + rmsVo.getReturnScale() + "%");
			return rmsVo;
		}
		// 请求用户接口
		Long wxUserId = Long.valueOf(ext1);
		rmsVo.setWxUserId(wxUserId);
		R<WxUser> wxUserR = wxUserClient.findWxUserById(wxUserId);
		if(wxUserR.getCode() == 200){
			WxUser wu = wxUserR.getData();
			if(wu != null && wu.getId() != null){
				// 查询推荐人租户信息
				if(wu.getParentWxUserId() != null){
					R<WxUser> parentWxUserR = wxUserClient.findWxUserById(wu.getParentWxUserId());
					if(parentWxUserR.getCode() == 200 && parentWxUserR.getData() != null && StringUtils.isNotBlank(parentWxUserR.getData().getTenantId())){
						WxUser pwu = parentWxUserR.getData();
						// 设置所属租户为推荐人租户
						rmsVo.setParentTenantId(pwu.getTenantId());
						if(pwu.getTenantReturnMoneyShare() != null && pwu.getTenantReturnMoneyShare() > 0){
							// 按照租户自定义的默认返利比例
							rmsVo.setReturnScale(pwu.getTenantReturnMoneyShare());
						}
					}
				}
				if(wu.getReturnMoneyShare() != null && wu.getReturnMoneyShare() > 0){
					rmsVo.setReturnScale(wu.getReturnMoneyShare());
				}
			}
		}
		log.info("根据商品扩展字段获取用户返现比例，最终返现比例：" + rmsVo.getReturnScale() + "%");
		return rmsVo;
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
