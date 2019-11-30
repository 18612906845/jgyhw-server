package cn.com.jgyhw.account.service.impl;

import cn.com.jgyhw.account.entity.MoneyAccount;
import cn.com.jgyhw.account.enums.AccountEnum;
import cn.com.jgyhw.account.mapper.MoneyAccountMapper;
import cn.com.jgyhw.account.service.IMoneyAccountService;
import cn.com.jgyhw.account.service.IWxPayService;
import cn.com.jgyhw.message.feign.IWxGzhMessageClient;
import cn.com.jgyhw.user.entity.WxUser;
import cn.com.jgyhw.user.feign.IWxUserClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springblade.common.tool.CommonUtil;
import org.springblade.core.mp.base.BaseServiceImpl;
import org.springblade.core.tool.api.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.beans.Transient;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by WangLei on 2019/11/23 0023 23:57
 */
@Slf4j
@Service
public class MoneyAccountServiceImpl extends BaseServiceImpl<MoneyAccountMapper, MoneyAccount> implements IMoneyAccountService {

	@Autowired
	private IWxUserClient wxUserClient;

	@Autowired
	private IWxPayService wxPayService;

	@Autowired
	private IWxGzhMessageClient wxGzhMessageClient;

	/**
	 * 保存流水账目
	 *
	 * @param moneyAccount 流水账目对象
	 */
	@Transient
	@Override
	public synchronized Map<String, Object> saveMoneyAccount(MoneyAccount moneyAccount) {
		Map<String, Object> resultMap = new HashMap<>();
		// 查询最新值
		MoneyAccount ma = baseMapper.selectNewMoneyAccount(moneyAccount.getWxUserId());
		Double balance = 0D;
		Double returnMoneySum = 0D;
		if(ma != null){
			balance = ma.getBalance();
			returnMoneySum = ma.getReturnMoneySum();
		}
		if(moneyAccount.getChangeType().equals(AccountEnum.CHANGE_TYPE_YETX.getKey())){
			resultMap = playUserWithdrawCash(moneyAccount);
			if(moneyAccount.getPayStatus().equals(AccountEnum.PLAY_STATUS_YZF.getKey())){
				// 支付成功
				balance -= moneyAccount.getChangeMoney();
				returnMoneySum += moneyAccount.getChangeMoney();
			}
		}else{
			// 其他都是收入
			balance = balance + moneyAccount.getChangeMoney();
		}
		moneyAccount.setBalance(CommonUtil.formatDouble(balance));
		moneyAccount.setReturnMoneySum(returnMoneySum);
		moneyAccount.setCreateTime(new Date());
		moneyAccount.setUpdateTime(new Date());
		int size = baseMapper.insert(moneyAccount);
		if(size > 0){
			resultMap.put("status", true);
			resultMap.put("msg", "");
		}
		return resultMap;
	}

	/**
	 * 根据用户标识查询最新流水账目记录
	 *
	 * @param wxUserId 用户标识
	 * @return
	 */
	@Override
	public MoneyAccount queryNewMoneyAccount(Long wxUserId) {
		MoneyAccount moneyAccount = baseMapper.selectNewMoneyAccount(wxUserId);
		return moneyAccount;
	}

	/**
	 * 支付用户提现
	 *
	 * @param moneyAccount 流水账目对象
	 * @return
	 */
	private Map<String, Object> playUserWithdrawCash(MoneyAccount moneyAccount){
		Map<String, Object> resultMap = new HashMap<>();
		//验证金额是否够
		Double remainingMoney = 0D;
		MoneyAccount ma = baseMapper.selectNewMoneyAccount(moneyAccount.getWxUserId());
		if(ma != null){
			remainingMoney = ma.getBalance();
		}
		if(moneyAccount.getChangeMoney() > remainingMoney){
			resultMap.put("msg", "余额不足");
			resultMap.put("status", false);
			return resultMap;
		}

		// 查询用户信息
		R<WxUser> wxUserR = wxUserClient.findWxUserById(moneyAccount.getWxUserId());
		if(wxUserR.getCode() == 200 && wxUserR.getData() != null && StringUtils.isNotBlank(wxUserR.getData().getOpenIdXcx())){
			WxUser wu = wxUserR.getData();
			//发起企业付款
			Double amountDouble = moneyAccount.getChangeMoney() * 100 ;
			int amount = amountDouble.intValue();
			String partnerTradeNo = CommonUtil.getShopOrderString();

			Map<String, String> payResultMap = wxPayService.enterprisePayToWxWallet(partnerTradeNo, wu.getOpenIdXcx(), amount, "京东购物返利提现");

			moneyAccount.setPayTime(new Date());
			moneyAccount.setPartnerTradeNo(partnerTradeNo);

			if(payResultMap == null){//解析XML错误
				moneyAccount.setPayStatus(AccountEnum.PLAY_STATUS_YZF.getKey());
				log.error("支付结果XML解析失败，商户订单号：" + partnerTradeNo + "；微信小程序OpenId：" + wu.getOpenIdXcx());

				resultMap.put("status", true);
				resultMap.put("msg", "支付成功");
			}else{
				if("SUCCESS".equals(payResultMap.get("result_code"))){//支付成功
					moneyAccount.setPayStatus(AccountEnum.PLAY_STATUS_YZF.getKey());
					moneyAccount.setPaymentNo(payResultMap.get("payment_no"));
					if(moneyAccount.getChangeMoney() >= 0){
						// 查询用户信息
						if(StringUtils.isNotBlank(wu.getOpenIdGzh())){
							// 发送消息
							wxGzhMessageClient.sendRebateWxMessage(wu.getOpenIdGzh(), "", moneyAccount.getChangeMoney());
						}
					}
					resultMap.put("status", true);
					resultMap.put("msg", "支付成功");
				}else{//支付失败
					moneyAccount.setPayStatus(AccountEnum.PLAY_STATUS_ZFSB.getKey());
					moneyAccount.setErrCode(payResultMap.get("err_code"));
					moneyAccount.setErrCodeDes(payResultMap.get("err_code_des"));
					moneyAccount.setPaymentNo(payResultMap.get("payment_no"));

					resultMap.put("status", false);
					resultMap.put("msg", "支付失败");
				}
			}
		}else{
			resultMap.put("status", false);
			resultMap.put("msg", "获取用户账户信息失败");
		}
		return resultMap;
	}
}
