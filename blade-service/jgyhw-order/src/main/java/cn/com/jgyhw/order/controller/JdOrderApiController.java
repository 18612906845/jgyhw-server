package cn.com.jgyhw.order.controller;

import cn.com.jgyhw.order.service.IJdOrderApiService;
import cn.com.jgyhw.order.vo.UpdateOrderRespVo;
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

/**
 * 京东订单Api控制器
 *
 * Created by WangLei on 2019/11/24 0024 15:17
 */
@Slf4j
@RefreshScope
@RestController
@RequestMapping("/jdOrderApi")
@Api(value = "京东订单Api", tags = "京东订单Api")
public class JdOrderApiController {

	@Autowired
	private IJdOrderApiService jdOrderApiService;

	/**
	 * 更新京东订单
	 *
	 * @param queryTimeStr 查询时间字符串，查询时间,输入格式必须为yyyyMMddHHmm,yyyyMMddHHmmss或者yyyyMMddHH格式之一
	 * @param queryTimeType 查询时间类型，1：下单时间，2：完成时间，3：更新时间
	 * @param isUnfreeze 是否解冻
	 * @param pageNum 页码
	 * @param pageSize 每页条数
	 * @return
	 */
	@GetMapping("/updateJdOrderInfoByTime")
	@ApiOperationSupport(order = 1)
	@ApiOperation(value = "进/出账操作", notes = "")
	public R<UpdateOrderRespVo> updateJdOrderInfoByTime(@ApiParam(value = "查询时间字符串，查询时间,输入格式必须为yyyyMMddHHmm,yyyyMMddHHmmss或者yyyyMMddHH格式之一", required = true) String queryTimeStr,
														@ApiParam(value = "查询时间类型，1：下单时间，2：完成时间，3：更新时间", required = true) int queryTimeType,
														@ApiParam(value = "是否解冻，默认值false") Boolean isUnfreeze,
														@ApiParam(value = "页码，默认值1") Integer pageNum,
														@ApiParam(value = "每页条数，默认值100") Integer pageSize){
		if(isUnfreeze == null){
			isUnfreeze = false;
		}
		if(pageNum == null){
			pageNum = 1;
		}
		if(pageSize == null){
			pageSize = 100;
		}
		return R.data(jdOrderApiService.updateJdOrderInfoByTime(queryTimeStr, queryTimeType, isUnfreeze, pageNum, pageSize));
	}

}
