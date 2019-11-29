package cn.com.jgyhw.order.controller;

import cn.com.jgyhw.order.entity.OrderRecord;
import cn.com.jgyhw.order.enums.OrderEnum;
import cn.com.jgyhw.order.service.IOrderRecordService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.springblade.common.tool.CommonUtil;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.CollectionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

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
}
