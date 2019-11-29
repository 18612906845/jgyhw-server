package cn.com.jgyhw.account.service;

import cn.com.jgyhw.account.entity.MoneyAccount;
import org.springblade.core.mp.base.BaseService;

/**
 * 流水账目服务类
 *
 * Created by WangLei on 2019/11/23 0023 23:56
 */
public interface IMoneyAccountService extends BaseService<MoneyAccount> {

	/**
	 * 保存流水账目
	 *
	 * @param moneyAccount 流水账目对象
	 */
	boolean saveMoneyAccount(MoneyAccount moneyAccount);

	/**
	 * 根据用户标识查询可提现金额
	 *
	 * @param wxUserId 用户标识
	 * @return
	 */
	Double queryMoneyAccountBalance(Long wxUserId);
}
