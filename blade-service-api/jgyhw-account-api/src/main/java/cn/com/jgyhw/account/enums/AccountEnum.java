package cn.com.jgyhw.account.enums;

/**
 * 流水账目服务枚举
 *
 * Created by WangLei on 2019/11/24 0024 00:12
 */
public enum AccountEnum {

	CHANGE_TYPE_GWFX(1, "购物返现"),
	CHANGE_TYPE_YETX(2, "余额提现"),
	CHANGE_TYPE_TGTC(3, "推广提成"),
	CHANGE_TYPE_YXJL(4, "邀新奖励"),
	CHANGE_TYPE_TGSY(5, "推广收益"),
	CHANGE_TYPE_CCSY(6, "抽成收益");

	private int key;

	private String text;

	private AccountEnum(int key, String text) {
		this.key = key;
		this.text = text;
	}

	public int getKey() {
		return key;
	}

	public String getText() {
		return text;
	}

}
