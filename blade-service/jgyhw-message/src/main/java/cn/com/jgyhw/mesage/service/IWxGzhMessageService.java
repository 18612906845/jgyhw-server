package cn.com.jgyhw.mesage.service;

import cn.com.jgyhw.message.vo.ArticleVo;
import cn.com.jgyhw.message.vo.TemplateMessageVo;

import java.util.List;

/**
 * 微信公众号消息服务
 *
 * Created by WangLei on 2019/11/22 0022 21:18
 */
public interface IWxGzhMessageService {

	/**
	 * 发送文本客服消息
	 *
	 * @param toUser
	 * @param content
	 */
	void sendTextMessage(String toUser, String content);

	/**
	 * 发送图文客服消息
	 *
	 * @param toUser 接收者微信标识
	 * @param articleVoList 图文消息对象集合
	 */
	void sendNewsMessage(String toUser, List<ArticleVo> articleVoList);

	/**
	 * 发送模版消息到公众号
	 *
	 * @param templateMessageVo 模版消息对象
	 */
	void sendTemplateMessage(TemplateMessageVo templateMessageVo);
}
