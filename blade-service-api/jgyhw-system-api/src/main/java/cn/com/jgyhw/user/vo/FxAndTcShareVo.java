package cn.com.jgyhw.user.vo;

import lombok.Data;

/**
 * 用户返现提成比例
 *
 * Created by WangLei on 2019/11/30 0030 20:42
 */
@Data
public class FxAndTcShareVo {

	/**
	 * 用户返现比例
	 */
	private int fxShare;

	/**
	 * 用户提成比例
	 */
	private int tcShare;
}
