package cn.com.jgyhw.token.job;

import cn.com.jgyhw.token.service.IWxTokenService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 更新微信令牌定时任务处理器
 *
 * Created by WangLei on 2019/11/28 0028 19:37
 */
@JobHandler(value="updateWxTokenJobHandler")
@Component
@Slf4j
public class UpdateWxTokenJobHandler extends IJobHandler {

	@Autowired
	private IWxTokenService wxTokenService;

	@Override
	public ReturnT<String> execute(String s) throws Exception {
		log.info("更新微信令牌-开始");
		wxTokenService.timingUpdateWxGzhServiceApiToken();
		log.info("更新微信令牌-结束");
		return SUCCESS;
	}
}
