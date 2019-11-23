package cn.com.jgyhw.order.service;

import cn.com.jgyhw.order.vo.UpdateOrderRespVo;

/**
 * 京东订单Api服务类
 *
 * Created by WangLei on 2019/11/23 0023 18:36
 */
public interface IJdOrderApiService {

	/**
	 * 更新京东订单信息
	 *
	 * @param queryTimeStr 查询时间字符串，查询时间,输入格式必须为yyyyMMddHHmm,yyyyMMddHHmmss或者yyyyMMddHH格式之一
	 * @param queryTimeType 查询时间类型，1：下单时间，2：完成时间，3：更新时间
	 * @param isUnfreeze    是否解冻
	 * @param pageNum      页码
	 * @param pageSize     每页条数
	 */
	UpdateOrderRespVo updateJdOrderInfoByTime(String queryTimeStr, int queryTimeType, boolean isUnfreeze, int pageNum, int pageSize);
}
