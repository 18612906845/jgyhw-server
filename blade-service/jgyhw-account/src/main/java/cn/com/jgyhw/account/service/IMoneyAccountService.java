package cn.com.jgyhw.account.service;

import cn.com.jgyhw.account.entity.MoneyAccount;
import org.springblade.core.mp.base.BaseService;

import java.util.Map;

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
	Map<String, Object> saveMoneyAccount(MoneyAccount moneyAccount);

	/**
	 * 根据用户标识查询最新流水账目记录
	 *
	 * @param wxUserId 用户标识
	 * @return
	 */
	MoneyAccount queryNewMoneyAccount(Long wxUserId);
}
