package cn.com.jgyhw.account.vo;

import cn.com.jgyhw.account.entity.MoneyAccount;
import lombok.Data;

/**
 * 流水账本Vo对象
 *
 * Created by WangLei on 2019/11/30 0030 15:20
 */
@Data
public class MoneyAccountVo extends MoneyAccount {

	/**
	 * 变更类型，1：购物返现；2：余额提现；3：推广提成；4：邀新奖励；5：推广收益
	 */
	private String changeTypeName;

}
