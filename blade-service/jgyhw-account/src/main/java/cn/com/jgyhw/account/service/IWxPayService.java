package cn.com.jgyhw.account.service;

import java.util.Map;

/**
 * 微信支付服务类
 *
 * Created by WangLei on 2019/11/30 0014 16:19
 */
public interface IWxPayService {

    /**
     * 企业支付到微信零钱
     *
     * @param partnerTradeNo 商户订单号，需保持唯一性(只能是字母或者数字，不能包含有其他字符)
     * @param openid 收款用户标识
     * @param amount 企业付款金额，单位为分
     * @param desc 企业付款备注，必填
     * @return
     */
    Map<String, String> enterprisePayToWxWallet(String partnerTradeNo, String openid, int amount, String desc);
}
