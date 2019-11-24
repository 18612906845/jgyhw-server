package cn.com.jgyhw.account.controller;

import cn.com.jgyhw.account.entity.MoneyAccount;
import cn.com.jgyhw.account.service.IMoneyAccountService;
import cn.com.jgyhw.message.feign.IWxGzhMessageClient;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiOperationSupport;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springblade.core.tool.api.R;
import org.springblade.system.user.entity.WxUser;
import org.springblade.system.user.feign.IWxUserClient;
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
	public R addOrReduce(MoneyAccount moneyAccount, String describe){
		boolean flag = moneyAccountService.saveMoneyAccount(moneyAccount);
		// 查询用户信息
		R<WxUser> wxUserR = wxUserClient.findWxUserById(moneyAccount.getWxUserId());
		if(wxUserR.getCode() == 200 && wxUserR.getData() != null && StringUtils.isNotBlank(wxUserR.getData().getOpenIdGzh())){
			// 发送消息
			wxGzhMessageClient.sendRebateWxMessage(wxUserR.getData().getOpenIdGzh(), describe, moneyAccount.getChangeMoney());
		}
		return R.status(flag);
	}
}