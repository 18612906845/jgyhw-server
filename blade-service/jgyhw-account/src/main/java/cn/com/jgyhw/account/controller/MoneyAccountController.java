package cn.com.jgyhw.account.controller;

import cn.com.jgyhw.account.entity.MoneyAccount;
import cn.com.jgyhw.account.enums.AccountEnum;
import cn.com.jgyhw.account.service.IMoneyAccountService;
import cn.com.jgyhw.account.vo.MoneyAccountVo;
import cn.com.jgyhw.message.feign.IWxGzhMessageClient;
import cn.com.jgyhw.user.entity.WxUser;
import cn.com.jgyhw.user.feign.IWxUserClient;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiOperationSupport;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.mp.support.Query;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.CollectionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.beans.BeanCopier;
import org.springframework.cloud.context.config.annotation.RefreshScope;
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
@Api(value = "流水账目", tags = "流水账目")
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
	@ApiOperationSupport(order = 1)
	@ApiOperation(value = "进/出账操作", notes = "")
	public R<Boolean> addOrReduce(MoneyAccount moneyAccount, @ApiParam(value = "操作描述", required = true) String describe){
		Map<String, Object> resultMap = moneyAccountService.saveMoneyAccount(moneyAccount);
		if(moneyAccount.getChangeMoney() <= 0){
			log.info("流水变更小于等于0，不发送通知消息，流水账目对象：" + JSON.toJSONString(moneyAccount));
			return R.data((boolean)resultMap.get("status"));
		}
		// 查询用户信息
		R<WxUser> wxUserR = wxUserClient.findWxUserById(moneyAccount.getWxUserId());
		if(wxUserR.getCode() == 200 && wxUserR.getData() != null && StringUtils.isNotBlank(wxUserR.getData().getOpenIdGzh())){
			// 发送消息
			wxGzhMessageClient.sendRebateWxMessage(wxUserR.getData().getOpenIdGzh(), describe, moneyAccount.getChangeMoney());
		}
		return R.data((boolean)resultMap.get("status"));
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
				maVo.setChangeTypeName(changeTypeToChanageTypeName(ma.getChangeType()));
				maVoList.add(maVo);
			}
			resultMap.put("recordList", maVoList);
		}
		return R.data(resultMap);
	}

	/**
	 * 申请提现
	 *
	 * @param money 申请金额
	 * @return
	 */
	@GetMapping("/applyWithdrawCash")
	public R<Map<String, Object>> applyWithdrawCash(Long loginKey, Double money){
		Map<String, Object> resultMap = new HashMap<>();
		resultMap.put("status", false);
		resultMap.put("msg", "未知错误");

		//判断取现金额是否正确
		if(money == null || money < 1){
			resultMap.put("msg", "1元起提");
			return R.data(resultMap);
		}
		// 保存出账操作
		MoneyAccount playMa = new MoneyAccount();
		playMa.setWxUserId(loginKey);
		playMa.setChangeType(AccountEnum.CHANGE_TYPE_YETX.getKey());
		playMa.setChangeMoney(money);
		resultMap = moneyAccountService.saveMoneyAccount(playMa);
		return R.data(resultMap);
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
				maVo.setChangeTypeName(changeTypeToChanageTypeName(ma.getChangeType()));
				maVoList.add(maVo);
			}
			resultMap.put("recordList", maVoList);
		}
		return R.data(resultMap);
	}

	/**
	 * 变更类型编码转名称
	 *
	 * @param changeType 变更类型编码
	 * @return
	 */
	private String changeTypeToChanageTypeName(Integer changeType){
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
}
