package cn.com.jgyhw.account.feign;

import cn.com.jgyhw.account.entity.MoneyAccount;
import org.springblade.core.tool.api.R;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Feign失败配置
 *
 * Created by WangLei on 2019/11/24 0024 12:12
 */
@Component
public class IMoneyAccountClientFallback implements IMoneyAccountClient {
	/**
	 * 进/出账操作
	 *
	 * @param moneyAccount 账目对象
	 * @param describe     操作描述
	 * @return
	 */
	@Override
	public R<Boolean> addOrReduce(@RequestBody MoneyAccount moneyAccount, @RequestParam("describe") String describe) {
		return R.fail(400,"进/出账操作Feign失败");
	}
}
