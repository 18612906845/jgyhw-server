package cn.com.jgyhw.order.job;

import cn.com.jgyhw.order.service.IJdOrderApiService;
import cn.com.jgyhw.order.vo.UpdateOrderRespVo;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import lombok.extern.slf4j.Slf4j;
import org.springblade.common.constant.JdParamConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * 更新京东订单定时任务处理器
 *
 * Created by WangLei on 2019/11/28 0028 19:37
 */
@JobHandler(value="updateJdOrderJobHandler")
@Component
@Slf4j
public class UpdateJdOrderJobHandler extends IJobHandler {

	private static final SimpleDateFormat SDF_YYYYMMDDHH = new SimpleDateFormat("yyyyMMddHH");

	@Autowired
	private IJdOrderApiService jdOrderApiService;

	@Override
	public ReturnT<String> execute(String isExamine) throws Exception {
		log.info("更新京东订单-开始");
		updateJdOrder(isExamine);
		log.info("更新京东订单-结束");
		return SUCCESS;
	}

	/**
	 * 更新京东订单
	 */
	private void updateJdOrder(String isExamine) {
		//获取当前时间
		Calendar cl = Calendar.getInstance();
		//查询京东当前小时订单
		String queryTimeStr = SDF_YYYYMMDDHH.format(cl.getTime());
		log.info("更新京东订单信息--间隔1分钟定时任务--> 时间条件：更新时间，时间值：" + queryTimeStr);
		int pageNumber = 1;
		boolean hasMore = false;
		do {
			log.info("更新京东订单信息--间隔1分钟定时任务--> 查询页数：" + pageNumber);
			UpdateOrderRespVo uorVo = jdOrderApiService.updateJdOrderInfoByTime(queryTimeStr, JdParamConstant.JD_ORDER_QUERY_TIME_TYPE_GXSJ, false, pageNumber, 100);
			if (uorVo.isStatus() == false) {
				log.info("更新京东订单信息--间隔1分钟定时任务--> 查询页数：" + pageNumber + "；异常，重试一次");
				uorVo = jdOrderApiService.updateJdOrderInfoByTime(queryTimeStr, JdParamConstant.JD_ORDER_QUERY_TIME_TYPE_GXSJ, false, pageNumber, 100);
				log.info("更新京东订单信息--间隔1分钟定时任务--> 查询页数：" + pageNumber + "；重试一次，结果：" + uorVo.isStatus());
			}
			hasMore = uorVo.isMore();
			pageNumber++;
		} while (hasMore);

		// 订单解冻
		pageNumber = 1;
		hasMore = false;
		cl.add(Calendar.DATE, -8);
		cl.add(Calendar.HOUR, -1);
		queryTimeStr = SDF_YYYYMMDDHH.format(cl.getTime());
		log.info("解冻京东订单--间隔1分钟定时任务--> 时间条件：完成时间，时间值：" + queryTimeStr);
		do {
			log.info("解冻京东订单--间隔1分钟定时任务--> 查询页数：" + pageNumber);
			UpdateOrderRespVo uorVo = jdOrderApiService.updateJdOrderInfoByTime(queryTimeStr, JdParamConstant.JD_ORDER_QUERY_TIME_TYPE_WCSJ, true, pageNumber, 100);
			if (uorVo.isStatus() == false) {
				log.info("解冻京东订单--间隔1分钟定时任务--> 查询页数：" + pageNumber + "；异常，重试一次");
				uorVo = jdOrderApiService.updateJdOrderInfoByTime(queryTimeStr, JdParamConstant.JD_ORDER_QUERY_TIME_TYPE_WCSJ, true, pageNumber, 100);
				log.info("解冻京东订单--间隔1分钟定时任务--> 查询页数：" + pageNumber + "；重试一次，结果：" + uorVo.isStatus());
			}
			hasMore = uorVo.isMore();
			pageNumber++;
		} while (hasMore);

		// 验证京东订单是否遗漏
		if("true".equals(isExamine)){
			pageNumber = 1;
			hasMore = false;
			//计算查询时间
			Calendar c2 = Calendar.getInstance();
			c2.add(Calendar.HOUR, -2);
			queryTimeStr = SDF_YYYYMMDDHH.format(cl.getTime());
			do {
				log.info("验证京东订单是否有遗漏定时任务--> 查询页数：" + pageNumber);
				UpdateOrderRespVo uorVo = jdOrderApiService.updateJdOrderInfoByTime(queryTimeStr, JdParamConstant.JD_ORDER_QUERY_TIME_TYPE_XDSJ, false, pageNumber, 100);
				if (uorVo.isStatus() == false) {
					log.info("验证京东订单是否有遗漏定时任务--> 查询页数：" + pageNumber + "；异常，重试一次");
					uorVo = jdOrderApiService.updateJdOrderInfoByTime(queryTimeStr, JdParamConstant.JD_ORDER_QUERY_TIME_TYPE_XDSJ, false, pageNumber, 100);
					log.info("验证京东订单是否有遗漏定时任务--> 查询页数：" + pageNumber + "；重试一次，结果：" + uorVo.isStatus());
				}
				hasMore = uorVo.isMore();
				pageNumber++;
			} while (hasMore);
		}
	}
}
