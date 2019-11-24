package cn.com.jgyhw.account.feign;

import cn.com.jgyhw.account.entity.MoneyAccount;
import org.springblade.common.constant.JgyhwConstant;
import org.springblade.core.tool.api.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 流水账目 Feign接口类
 *
 * Created by WangLei on 2019/11/24 0024 12:12
 */
@FeignClient(
	value = JgyhwConstant.APPLICATION_ACCOUNT_NAME,
	fallback = IMoneyAccountClientFallback.class
)
public interface IMoneyAccountClient {

	String API_PREFIX = "/moneyAccount";

	/**
	 * 进/出账操作
	 *
	 * @param moneyAccount 账目对象
	 * @param describe 操作描述
	 * @return
	 */
	@PostMapping(API_PREFIX + "/addOrReduce")
	R<Boolean> addOrReduce(@RequestParam("moneyAccount") MoneyAccount moneyAccount, @RequestParam("describe") String describe);
}
