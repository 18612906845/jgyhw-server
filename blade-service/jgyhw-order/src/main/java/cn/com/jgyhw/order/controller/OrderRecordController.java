package cn.com.jgyhw.order.controller;

import cn.com.jgyhw.order.entity.OrderGoods;
import cn.com.jgyhw.order.entity.OrderRecord;
import cn.com.jgyhw.order.enums.OrderEnum;
import cn.com.jgyhw.order.service.IOrderGoodsService;
import cn.com.jgyhw.order.service.IOrderRecordService;
import cn.com.jgyhw.order.vo.OrderRecordVo;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springblade.common.tool.CommonUtil;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.CollectionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.beans.BeanCopier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 订单记录控制器
 *
 * Created by WangLei on 2019/11/29 0029 22:12
 */
@Slf4j
@RestController
@RequestMapping("/orderRecord")
public class OrderRecordController {

	@Autowired
	private IOrderRecordService orderRecordService;

	@Autowired
	private IOrderGoodsService orderGoodsService;

	/**
	 * 根据微信标识查询待支付订单总数
	 *
	 * @param loginKey 登陆标识
	 * @return
	 */
	@GetMapping("/findAwaitPayOrderSum")
	public R<Integer> findAwaitPayOrderSum(Long loginKey){
		int sum = orderRecordService.count(Wrappers.<OrderRecord>lambdaQuery().eq(OrderRecord::getStatus, OrderEnum.ORDER_STATUS_DFK.getKey()).eq(OrderRecord::getWxUserId, loginKey));
		return R.data(sum);
	}

	/**
	 * 查询等待入账订单总数
	 *
	 * @param loginKey 登陆标识
	 * @return
	 */
	@GetMapping("/findAwaitRzOrderSum")
	public R<Integer> findAwaitRzOrderSum(Long loginKey){
		int sum = orderRecordService.count(Wrappers.<OrderRecord>lambdaQuery().eq(OrderRecord::getStatus, OrderEnum.ORDER_STATUS_YWC.getKey()).eq(OrderRecord::getWxUserId, loginKey));
		return R.data(sum);
	}

	/**
	 * 查询订单返现信息
	 *
	 * @param loginKey 登陆标识
	 * @return
	 */
	@GetMapping("/findOrderReturnMoney")
	public R<Map<String, Object>> findOrderReturnMoney(Long loginKey){
		Map<String, Object> resultMap = new HashMap<>();
		// 查询预估返现
		List<OrderRecord> estimateOrList = orderRecordService.list(Wrappers.<OrderRecord>lambdaQuery().eq(OrderRecord::getWxUserId, loginKey).eq(OrderRecord::getStatus, OrderEnum.ORDER_STATUS_YFK.getKey()));
		double estimate = 0D;
		if(CollectionUtil.isNotEmpty(estimateOrList)){
			for(OrderRecord or : estimateOrList){
				estimate += or.getReturnMoney();
			}
		}
		resultMap.put("estimate", CommonUtil.formatDouble(estimate));
		// 查询待解冻
		List<OrderRecord> unfreezeOrList = orderRecordService.list(Wrappers.<OrderRecord>lambdaQuery().eq(OrderRecord::getStatus, OrderEnum.ORDER_STATUS_YWC.getKey()).eq(OrderRecord::getWxUserId, loginKey));
		double unfreeze = 0D;
		if(CollectionUtil.isNotEmpty(unfreezeOrList)){
			for(OrderRecord or : unfreezeOrList){
				unfreeze += or.getReturnMoney();
			}
		}
		resultMap.put("unfreeze", CommonUtil.formatDouble(unfreeze));
		return R.data(resultMap);
	}

	/**
	 * 根据登陆标识和类型查询订单集合
	 *
	 * @param loginKey 登陆标识
	 * @param type 订单状态
	 * @return
	 */
	@GetMapping("/findOrderRecordListByLoginKeyAndType")
	public R<List<OrderRecordVo>> findOrderRecordListByLoginKeyAndType(Long loginKey, String type){
		if(loginKey == null){
			return R.status(false);
		}
		List<OrderRecord> orderList = new ArrayList<>();
		if(StringUtils.isBlank(type)){// 查询全部订单
			orderList = orderRecordService.list(Wrappers.<OrderRecord>lambdaQuery().eq(OrderRecord::getWxUserId, loginKey));
		}else if("dfk".equals(type)){// 待付款订单
			orderList = orderRecordService.list(Wrappers.<OrderRecord>lambdaQuery().eq(OrderRecord::getWxUserId, loginKey).eq(OrderRecord::getStatus, OrderEnum.ORDER_STATUS_DFK.getKey()));
		}else if("yqr".equals(type)){// 已确认订单
			orderList = orderRecordService.list(Wrappers.<OrderRecord>lambdaQuery().eq(OrderRecord::getWxUserId, loginKey).eq(OrderRecord::getStatus, OrderEnum.ORDER_STATUS_YFK.getKey()));
		}else if("drz".equals(type) || "ywc".equals(type)){// 待入账/已完成订单
			orderList = orderRecordService.list(Wrappers.<OrderRecord>lambdaQuery().eq(OrderRecord::getWxUserId, loginKey).eq(OrderRecord::getStatus, OrderEnum.ORDER_STATUS_YWC.getKey()));
		}else if("yrz".equals(type)){// 已入账订单
			orderList = orderRecordService.list(Wrappers.<OrderRecord>lambdaQuery().eq(OrderRecord::getWxUserId, loginKey).eq(OrderRecord::getStatus, OrderEnum.ORDER_STATUS_YRZ.getKey()));
		}else if("wx".equals(type)){// 无效订单
			orderList = orderRecordService.list(Wrappers.<OrderRecord>lambdaQuery().eq(OrderRecord::getWxUserId, loginKey).and(i -> i.eq(OrderRecord::getStatus, OrderEnum.ORDER_STATUS_YQX.getKey())).or(i -> i.eq(OrderRecord::getStatus, OrderEnum.ORDER_STATUS_WX.getKey())));
		}
		List<OrderRecordVo> orVoList = new ArrayList<>();
		if(orderList != null && !orderList.isEmpty()){
			for(OrderRecord or : orderList){
				List<OrderGoods> ogList = orderGoodsService.list(Wrappers.<OrderGoods>lambdaQuery().eq(OrderGoods::getOrderRecordId, or.getId()));
				OrderRecordVo orVo = new OrderRecordVo();
				BeanCopier copier = BeanCopier.create(OrderRecord.class, OrderRecordVo.class, false);
				copier.copy(or, orVo, null);
				orVo.setOrderGoodsList(ogList);
				orVo.setStatusName(orderStatusToStatusName(orVo.getStatus()));
				orVo.setMultipartiteGoods(ogList.size() > 1);
				orVoList.add(orVo);
			}
		}
		return R.data(orVoList);
	}

	/**
	 * 订单状态装换
	 *
	 * @param status
	 * @return
	 */
	private String orderStatusToStatusName(Integer status){
		if(status.equals(OrderEnum.ORDER_STATUS_DFK.getKey())){
			return OrderEnum.ORDER_STATUS_DFK.getText();
		}
		if(status.equals(OrderEnum.ORDER_STATUS_YFK.getKey())){
			return OrderEnum.ORDER_STATUS_YFK.getText();
		}
		if(status.equals(OrderEnum.ORDER_STATUS_YQX.getKey())){
			return OrderEnum.ORDER_STATUS_YQX.getText();
		}
		if(status.equals(OrderEnum.ORDER_STATUS_YCT.getKey())){
			return OrderEnum.ORDER_STATUS_YCT.getText();
		}
		if(status.equals(OrderEnum.ORDER_STATUS_YWC.getKey())){
			return OrderEnum.ORDER_STATUS_YWC.getText();
		}
		if(status.equals(OrderEnum.ORDER_STATUS_YRZ.getKey())){
			return OrderEnum.ORDER_STATUS_YRZ.getText();
		}
		if(status.equals(OrderEnum.ORDER_STATUS_WX.getKey())){
			return OrderEnum.ORDER_STATUS_WX.getText();
		}
		return "";
	}
}
