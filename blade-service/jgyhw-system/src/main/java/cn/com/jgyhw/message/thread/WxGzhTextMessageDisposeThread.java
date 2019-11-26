package cn.com.jgyhw.message.thread;

import cn.com.jgyhw.goods.feign.IJdGoodsClient;
import cn.com.jgyhw.goods.vo.JdGoodsVo;
import cn.com.jgyhw.message.service.IWxGzhMessageService;
import cn.com.jgyhw.message.vo.ArticleVo;
import cn.com.jgyhw.user.entity.WxUser;
import cn.com.jgyhw.user.service.IWxUserService;
import cn.com.jgyhw.user.vo.WxUserReturnMoneyScaleVo;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springblade.core.tool.api.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 微信公众号文字消息处理线程
 *
 * Created by WangLei on 2019/11/23 0023 02:01
 */
@Slf4j
@Data
public class WxGzhTextMessageDisposeThread implements Runnable {

	/**
	 * 商品编号
	 */
	private String goodsId;

	/**
	 * 接收者微信公众号OpenId
	 */
	private String receiveGzhOpenId;

	/**
	 * 登陆消息内容
	 */
	private String loginMessageContent;

	/**
	 * 系统全局返现比例
	 */
	private Integer systemReturnMoneyShareDefault;

	/**
	 * 微信用户接口
	 */
	private IWxUserService wxUserService;

	/**
	 * 京东商品Feign接口
	 */
	private IJdGoodsClient jdGoodsClient;

	/**
	 * 微信消息接口
	 */
	private IWxGzhMessageService wxGzhMessageService;

	@Override
	public void run() {
		log.info("开启线程处理，商品编号：" + goodsId);
		// 判断用户是否登录（是否有对应的UnionID）
		WxUser wu = wxUserService.getOne(Wrappers.<WxUser>lambdaQuery().eq(WxUser::getOpenIdGzh, receiveGzhOpenId));
		if(wu == null || wu.getId() == null || StringUtils.isBlank(wu.getUnionId())){// 无登录信息
			// 调用消息发送接口，发送登陆链接文字消息
			wxGzhMessageService.sendTextMessage(receiveGzhOpenId, loginMessageContent);
			log.info("用户【" + receiveGzhOpenId + "】未登录，发送登陆链接消息");
		}else{// 有登陆信息
			// 获取用户的返现比例
			Integer returnMoneyShare = systemReturnMoneyShareDefault;
			WxUserReturnMoneyScaleVo wurmsVo = wxUserService.findWxUserReturnMoneyScaleVoById(wu.getId());
			if(wurmsVo != null && wurmsVo.getReturnScale() > 0){
				returnMoneyShare = wurmsVo.getReturnScale();
			}
			// 查询商品信息
			R<JdGoodsVo> jdGoodsVoR = jdGoodsClient.findJdCpsInfo(goodsId, String.valueOf(wu.getId()), returnMoneyShare);
			if(jdGoodsVoR.getCode() == 200){
				JdGoodsVo jgVo = jdGoodsVoR.getData();
				if(jgVo == null || StringUtils.isBlank(jgVo.getGoodsId())){
					wxGzhMessageService.sendTextMessage(receiveGzhOpenId, "未查询到商品编号【" + goodsId + "】相关优惠信息");
					log.info("用户【" + receiveGzhOpenId + "】未查询到商品编号【" + goodsId + "】相关优惠信息");
				}else{
					// 发送优惠图文消息
					List<ArticleVo> avs = new ArrayList<>();
					ArticleVo av = new ArticleVo();
					av.setTitle("约返现:" + jgVo.getReturnMoney() + "元" + ";编号:" + jgVo.getGoodsId());
					av.setDescription(jgVo.getGoodsName());
					av.setPicUrl(jgVo.getGoodsImgUrl());
					av.setUrl(jgVo.getCpsUrl());
					avs.add(av);
					wxGzhMessageService.sendNewsMessage(receiveGzhOpenId, avs);
					log.info("用户【" + receiveGzhOpenId + "】查询到商品编号【" + goodsId + "】优惠信息，并完成图文消息发送");
				}
			}else{
				log.error("获取京东商品Feign错误，错误编号：" + jdGoodsVoR.getCode() + "；错误描述：" + jdGoodsVoR.getMsg());
			}
		}
	}
}
