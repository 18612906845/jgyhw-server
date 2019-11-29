package cn.com.jgyhw.account.service.impl;

import cn.com.jgyhw.account.entity.MoneyAccount;
import cn.com.jgyhw.account.enums.AccountEnum;
import cn.com.jgyhw.account.mapper.MoneyAccountMapper;
import cn.com.jgyhw.account.service.IMoneyAccountService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.springblade.common.tool.CommonUtil;
import org.springblade.core.mp.base.BaseServiceImpl;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.mp.support.Query;
import org.springframework.stereotype.Service;

import java.beans.Transient;
import java.util.Date;

/**
 * Created by WangLei on 2019/11/23 0023 23:57
 */
@Slf4j
@Service
public class MoneyAccountServiceImpl extends BaseServiceImpl<MoneyAccountMapper, MoneyAccount> implements IMoneyAccountService {

	/**
	 * 保存流水账目
	 *
	 * @param moneyAccount 流水账目对象
	 */
	@Transient
	@Override
	public synchronized boolean saveMoneyAccount(MoneyAccount moneyAccount) {
		// 查询最新值
		Query query = new Query();
		query.setCurrent(1);
		query.setSize(1);
		IPage<MoneyAccount> page = baseMapper.selectPage(Condition.getPage(query), Wrappers.<MoneyAccount>lambdaQuery()
			.eq(MoneyAccount::getWxUserId,moneyAccount.getWxUserId())
			.orderByDesc(MoneyAccount::getCreateTime, MoneyAccount::getId));
		Double balance = 0D;
		if(page != null && CollectionUtils.isNotEmpty(page.getRecords())){
			balance = page.getRecords().get(0).getBalance();
		}
		if(moneyAccount.getChangeType().equals(AccountEnum.CHANGE_TYPE_YETX.getKey())){
			// 余额提现支出
			balance = balance - moneyAccount.getChangeMoney();
		}else{
			// 其他都是收入
			balance = balance + moneyAccount.getChangeMoney();
		}
		moneyAccount.setBalance(CommonUtil.formatDouble(balance));
		moneyAccount.setCreateTime(new Date());
		moneyAccount.setUpdateTime(new Date());
		int size = baseMapper.insert(moneyAccount);
		return size > 0;
	}

	/**
	 * 根据用户标识查询可提现金额
	 *
	 * @param wxUserId 用户标识
	 * @return
	 */
	@Override
	public Double queryMoneyAccountBalance(Long wxUserId) {
		Double balance = baseMapper.selectMoneyAccountBalance(wxUserId);
		return balance == null ? 0D : balance;
	}
}
