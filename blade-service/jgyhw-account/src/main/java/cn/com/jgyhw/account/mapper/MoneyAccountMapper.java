package cn.com.jgyhw.account.mapper;

import cn.com.jgyhw.account.entity.MoneyAccount;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * 流水账目 Mapper 接口
 *
 * Created by WangLei on 2019/11/23 0023 23:58
 */
public interface MoneyAccountMapper extends BaseMapper<MoneyAccount> {

	/**
	 * 查询最新流水账目记录
	 *
	 * @param wxUserId 微信用户标识
	 * @return
	 */
	MoneyAccount selectNewMoneyAccount(@Param("wxUserId") Long wxUserId);
}
