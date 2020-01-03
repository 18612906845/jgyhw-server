package cn.com.jgyhw.account.controller;

import cn.com.jgyhw.account.entity.MoneyAccount;
import cn.com.jgyhw.account.enums.AccountEnum;
import cn.com.jgyhw.account.service.IMoneyAccountService;
import cn.com.jgyhw.account.vo.MoneyAccountVo;
import cn.com.jgyhw.account.vo.PayResultVo;
import cn.com.jgyhw.message.feign.IWxGzhMessageClient;
import cn.com.jgyhw.user.entity.WxUser;
import cn.com.jgyhw.user.feign.IWxUserClient;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springblade.common.tool.MD5Util;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.mp.support.Query;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.CollectionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.beans.BeanCopier;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 流水账目控制器
 *
 * Created by WangLei on 2019/11/24 0024 00:03
 */
@Slf4j
@RestController
@RequestMapping("/moneyAccount")
public class MoneyAccountController {

	@Autowired
	private IMoneyAccountService moneyAccountService;

	@Autowired
	private IWxGzhMessageClient wxGzhMessageClient;

	@Autowired
	private IWxUserClient wxUserClient;

	/**
	 * 进/出账操作
	 *
	 * @param moneyAccount 账目对象
	 * @param describe 操作描述
	 * @return
	 */
	@PostMapping("/addOrReduce")
	public R<Boolean> addOrReduce(@RequestBody MoneyAccount moneyAccount, @RequestParam String describe){
		MoneyAccount oldMa = moneyAccountService.getOne(Wrappers.<MoneyAccount>lambdaQuery().eq(MoneyAccount::getMd5, moneyAccount.getMd5()));
		if(oldMa != null){
			return R.data(true);
		}
		Map<String, Object> resultMap = moneyAccountService.saveMoneyAccount(moneyAccount);
		boolean status = (boolean)resultMap.get("status");
		if(status){
			if(moneyAccount.getChangeMoney() <= 0){
				log.info("流水变更小于等于0，不发送通知消息，流水账目对象：" + JSON.toJSONString(moneyAccount));
				return R.data(status);
			}
			// 查询用户信息
			R<WxUser> wxUserR = wxUserClient.findWxUserById(moneyAccount.getWxUserId());
			if(wxUserR.getCode() == 200 && wxUserR.getData() != null && StringUtils.isNotBlank(wxUserR.getData().getOpenIdGzh())){
				// 发送消息
				wxGzhMessageClient.sendRebateWxMessage(wxUserR.getData().getOpenIdGzh(), describe, moneyAccount.getChangeMoney());
			}
		}
		return R.data(status);
	}

	/**
	 * 查询用户余额和累计提现金额
	 *
	 * @param loginKey 微信登陆标识
	 * @return
	 */
	@GetMapping("/findMoneyAccountBalanceAndReturnMoneySum")
	public R<Map<String, Object>> findMoneyAccountBalanceAndReturnMoneySum(Long loginKey){
		MoneyAccount ma = moneyAccountService.queryNewMoneyAccount(loginKey);
		Map<String, Object> resultMap = new HashMap<>();
		resultMap.put("balance", 0);
		resultMap.put("returnMoneySum", 0);
		if(ma != null){
			resultMap.put("balance", ma.getBalance());
			resultMap.put("returnMoneySum", ma.getReturnMoneySum());
		}
		return R.data(resultMap);
	}

	/**
	 * 查询进出账操作记录（分页）
	 *
	 * @param loginKey 微信登陆标识
	 * @param query 分页属性
	 * @return
	 */
	@GetMapping("/findMoneyAccountByPage")
	public R<Map<String, Object>> findMoneyAccountByPage(Long loginKey, Query query){
		IPage<MoneyAccount> page = moneyAccountService.page(Condition.getPage(query), Wrappers.<MoneyAccount>lambdaQuery().eq(MoneyAccount::getWxUserId, loginKey).orderByDesc(MoneyAccount::getCreateTime));
		return R.data(moneyAccountToVo(page, query));
	}

	/**
	 * 申请提现
	 *
	 * @param money 申请金额
	 * @return
	 */
	@GetMapping("/applyWithdrawCash")
	public R<PayResultVo> applyWithdrawCash(Long loginKey, Double money){
		PayResultVo prVo = new PayResultVo();
		prVo.setStatus(false);
		prVo.setMsg("未知错误");
		//判断取现金额是否正确
		if(money == null || money < 1){
			prVo.setMsg("1元起提");
			return R.data(prVo);
		}
		//验证金额是否够
		Double remainingMoney = 0D;
		MoneyAccount ma = moneyAccountService.queryNewMoneyAccount(loginKey);
		if(ma != null){
			remainingMoney = ma.getBalance();
		}
		if(money > remainingMoney){
			prVo.setStatus(false);
			prVo.setMsg("余额不足");
			return R.data(prVo);
		}
		// 保存出账操作
		MoneyAccount playMa = new MoneyAccount();
		playMa.setWxUserId(loginKey);
		playMa.setChangeType(AccountEnum.CHANGE_TYPE_YETX.getKey());
		playMa.setChangeMoney(money);
		String md5 = MD5Util.stringToMD5(loginKey.toString() + money + System.currentTimeMillis());
		playMa.setMd5(md5);
		Map<String, Object> resultMap = moneyAccountService.saveMoneyAccount(playMa);
		prVo.setMsg((String)resultMap.get("msg"));
		prVo.setStatus((boolean)resultMap.get("status"));
		return R.data(prVo);
	}

	/**
	 * 查询出账操作记录（分页）
	 *
	 * @param loginKey 登陆标识
	 * @param query 分页参数
	 * @return
	 */
	@RequestMapping("/findWithdrawCashRecordListByWxUserIdPage")
	public R<Map<String, Object>> findWithdrawCashRecordListByWxUserIdPage(Long loginKey, Query query){
		IPage<MoneyAccount> page = moneyAccountService.page(Condition.getPage(query), Wrappers.<MoneyAccount>lambdaQuery().eq(MoneyAccount::getWxUserId, loginKey).eq(MoneyAccount::getChangeType, AccountEnum.CHANGE_TYPE_YETX.getKey()).orderByDesc(MoneyAccount::getCreateTime));
		return R.data(moneyAccountToVo(page, query));
	}

	/**
	 * 流水账目分页记录装换
	 *
	 * @param page 分页对象
	 * @param query 查询参数对象
	 * @return
	 */
	private Map<String, Object> moneyAccountToVo(IPage<MoneyAccount> page, Query query){
		Map<String, Object> resultMap = new HashMap<>();
		resultMap.put("isMore", false);
		resultMap.put("recordList", new ArrayList<>());

		if(page != null && CollectionUtil.isNotEmpty(page.getRecords())){
			resultMap.put("isMore", query.getCurrent() * query.getSize() < page.getTotal());

			List<MoneyAccountVo> maVoList = new ArrayList<>();
			for(MoneyAccount ma : page.getRecords()){
				MoneyAccountVo maVo = new MoneyAccountVo();
				BeanCopier copier = BeanCopier.create(MoneyAccount.class, MoneyAccountVo.class, false);
				copier.copy(ma, maVo, null);
				maVo.setChangeTypeName(changeTypeToName(ma.getChangeType()));
				// 根据流水类型
				if(ma.getChangeType().equals(AccountEnum.CHANGE_TYPE_GWFX.getKey())){
					// 购物返现
					maVo.setTargetImageName("icon_rzjl_jd.jpg");
					if(StringUtils.isNotBlank(ma.getTargetJson())){
						JSONObject targetJson = JSON.parseObject(ma.getTargetJson());
						maVo.setTargetName(targetJson.getString("orderId"));
					}
				}else if(ma.getChangeType().equals(AccountEnum.CHANGE_TYPE_YETX.getKey())){
					// 余额提现
					maVo.setTargetImageName("icon_txjl.jpg");
					String targetName = playStatusToName(ma.getPayStatus());
					if(ma.getPayStatus() != null && ma.getPayStatus().equals(AccountEnum.PLAY_STATUS_ZFSB.getKey())){
						targetName += "；请联系客服处理";
					}
					maVo.setTargetName(targetName);
				}else{
					if(StringUtils.isNotBlank(ma.getTargetJson())){
						JSONObject targetJson = JSON.parseObject(ma.getTargetJson());
						maVo.setTargetImageName(targetJson.getString("headImgUrl"));
						maVo.setTargetName(targetJson.getString("nickName"));
					}
				}
				maVoList.add(maVo);
			}
			resultMap.put("recordList", maVoList);
		}
		return resultMap;
	}

	/**
	 * 变更类型编码转名称
	 *
	 * @param changeType 变更类型编码
	 * @return
	 */
	private String changeTypeToName(Integer changeType){
		if(changeType.equals(AccountEnum.CHANGE_TYPE_GWFX.getKey())){
			return AccountEnum.CHANGE_TYPE_GWFX.getText();
		}
		if(changeType.equals(AccountEnum.CHANGE_TYPE_YETX.getKey())){
			return AccountEnum.CHANGE_TYPE_YETX.getText();
		}
		if(changeType.equals(AccountEnum.CHANGE_TYPE_TGTC.getKey())){
			return AccountEnum.CHANGE_TYPE_TGTC.getText();
		}
		if(changeType.equals(AccountEnum.CHANGE_TYPE_YXJL.getKey())){
			return AccountEnum.CHANGE_TYPE_YXJL.getText();
		}
		if(changeType.equals(AccountEnum.CHANGE_TYPE_TGSY.getKey())){
			return AccountEnum.CHANGE_TYPE_TGSY.getText();
		}
		if(changeType.equals(AccountEnum.CHANGE_TYPE_CCSY.getKey())){
			return AccountEnum.CHANGE_TYPE_CCSY.getText();
		}
		return "";
	}

	/**
	 * 支付状态编码转名称
	 *
	 * @param playStatus 支付状态
	 * @return
	 */
	private String playStatusToName(Integer playStatus){
		if(playStatus == null){
			return "";
		}
		if(playStatus.equals(AccountEnum.PLAY_STATUS_DZF.getKey())){
			return AccountEnum.PLAY_STATUS_DZF.getText();
		}
		if(playStatus.equals(AccountEnum.PLAY_STATUS_YZF.getKey())){
			return AccountEnum.PLAY_STATUS_YZF.getText();
		}
		if(playStatus.equals(AccountEnum.PLAY_STATUS_ZFSB.getKey())){
			return AccountEnum.PLAY_STATUS_ZFSB.getText();
		}
		return "";
	}
}
