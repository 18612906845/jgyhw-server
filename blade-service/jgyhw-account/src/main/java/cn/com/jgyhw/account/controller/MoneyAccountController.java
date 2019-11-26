package cn.com.jgyhw.account.controller;

import cn.com.jgyhw.account.entity.MoneyAccount;
import cn.com.jgyhw.account.service.IMoneyAccountService;
import cn.com.jgyhw.message.feign.IWxGzhMessageClient;
import cn.com.jgyhw.user.entity.WxUser;
import cn.com.jgyhw.user.feign.IWxUserClient;
import com.alibaba.fastjson.JSON;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiOperationSupport;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springblade.core.tool.api.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 流水账目控制器
 *
 * Created by WangLei on 2019/11/24 0024 00:03
 */
@Slf4j
@RefreshScope
@RestController
@RequestMapping("/moneyAccount")
@Api(value = "流水账目", tags = "流水账目")
public class MoneyAccountController {

	@Autowired
	private IMoneyAccountService moneyAccountService;

	@Autowired
	private IWxGzhMessageClient wxGzhMessageClient;

	@Autowired
	private IWxUserClient wxUserClient;

	/**
	 * 进/出账操作
	 *
	 * @param moneyAccount 账目对象
	 * @param describe 操作描述
	 * @return
	 */
	@PostMapping("/addOrReduce")
	@ApiOperationSupport(order = 1)
	@ApiOperation(value = "进/出账操作", notes = "")
	public R<Boolean> addOrReduce(MoneyAccount moneyAccount, @ApiParam(value = "操作描述", required = true) String describe){
		boolean flag = moneyAccountService.saveMoneyAccount(moneyAccount);
		if(moneyAccount.getChangeMoney() <= 0){
			log.info("流水变更小于等于0，不发送通知消息，流水账目对象：" + JSON.toJSONString(moneyAccount));
			return R.data(flag);
		}
		// 查询用户信息
		R<WxUser> wxUserR = wxUserClient.findWxUserById(moneyAccount.getWxUserId());
		if(wxUserR.getCode() == 200 && wxUserR.getData() != null && StringUtils.isNotBlank(wxUserR.getData().getOpenIdGzh())){
			// 发送消息
			wxGzhMessageClient.sendRebateWxMessage(wxUserR.getData().getOpenIdGzh(), describe, moneyAccount.getChangeMoney());
		}
		return R.data(flag);
	}
}
