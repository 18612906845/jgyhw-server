package cn.com.jgyhw.mesage.service.impl;

import cn.com.jgyhw.mesage.service.IWxGzhMessageService;
import cn.com.jgyhw.message.constant.MessageConstant;
import cn.com.jgyhw.message.vo.ArticleVo;
import cn.com.jgyhw.message.vo.TemplateMessageVo;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springblade.common.constant.WxGzhParamConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * Created by WangLei on 2019/11/22 0022 21:29
 */
@Slf4j
@RefreshScope
@Service
public class WxGzhMessageServiceImpl implements IWxGzhMessageService {

	@Value("${jgyhw.wxGzh.appId:}")
	private String wxGzhAppId;

	@Autowired
	private StringRedisTemplate stringRedisTemplate;

	@Autowired
	private RestTemplate restTemplate;

	/**
	 * 发送文本客服消息
	 *
	 * @param toUser
	 * @param content
	 */
	@Override
	public void sendTextMessage(String toUser, String content) {
		if(StringUtils.isBlank(toUser) || StringUtils.isBlank(content)){
			return ;
		}
		String accessToken = stringRedisTemplate.opsForValue().get(WxGzhParamConstant.WX_GZH_SERVICE_API_TOKEN_KEY_PREFIX.concat(wxGzhAppId));
		log.info("发送文本客服消息，微信ServiceApiToken：" + accessToken);
		if(StringUtils.isBlank(accessToken)){
			log.warn("微信ServiceApiToken为空，未执行文本客服消息发送操作");
			return ;
		}
		String url = WxGzhParamConstant.SEND_CUSTOM_MESSAGE_URL;
		if(!StringUtils.isBlank(url)){
			url = url.replaceAll("ACCESS_TOKEN", accessToken);
		}
		// 设置请求头
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);
		// 设置请求参数
		JSONObject textJsonObj = new JSONObject();
		textJsonObj.put("content", content);

		JSONObject postDataJson = new JSONObject();
		postDataJson.put("touser", toUser);
		postDataJson.put("msgtype", MessageConstant.ANSWER_MSG_TYPE_TEXT);
		postDataJson.put("text", textJsonObj);
		// 将请求头和请求参数设置到HttpEntity中
		HttpEntity httpEntity = new HttpEntity(postDataJson, httpHeaders);

		JSONObject respJsonObj = restTemplate.postForObject(url, httpEntity, JSONObject.class);
		log.info("文本客服消息发送结果：" + respJsonObj);
	}

	/**
	 * 发送图文客服消息
	 *
	 * @param toUser        接收者微信标识
	 * @param articleVoList 图文消息对象集合
	 */
	@Override
	public void sendNewsMessage(String toUser, List<ArticleVo> articleVoList) {
		if(StringUtils.isBlank(toUser) || CollectionUtils.isEmpty(articleVoList)){
			return ;
		}
		String accessToken = stringRedisTemplate.opsForValue().get(WxGzhParamConstant.WX_GZH_SERVICE_API_TOKEN_KEY_PREFIX.concat(wxGzhAppId));
		log.info("发送图文客服消息，微信ServiceApiToken：" + accessToken);
		if(StringUtils.isBlank(accessToken)){
			log.warn("微信ServiceApiToken为空，未执行图文客服消息发送操作");
			return ;
		}
		String url = WxGzhParamConstant.SEND_CUSTOM_MESSAGE_URL;
		if(!StringUtils.isBlank(url)){
			url = url.replaceAll("ACCESS_TOKEN", accessToken);
		}
		// 设置请求头
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);
		// 设置请求参数
		JSONArray articlesJsonArray = new JSONArray();
		for(ArticleVo av : articleVoList){
			JSONObject itemJsonObj = new JSONObject();
			itemJsonObj.put("title", av.getTitle());
			itemJsonObj.put("description", av.getDescription());
			itemJsonObj.put("url", av.getUrl());
			itemJsonObj.put("picurl", av.getPicUrl());
			articlesJsonArray.add(itemJsonObj);
		}
		JSONObject newsJsonObj = new JSONObject();
		newsJsonObj.put("articles", articlesJsonArray);
		JSONObject postDataJson = new JSONObject();
		postDataJson.put("touser", toUser);
		postDataJson.put("msgtype", MessageConstant.ANSWER_MSG_TYPE_NEWS);
		postDataJson.put("news", newsJsonObj);
		// 将请求头和请求参数设置到HttpEntity中
		HttpEntity httpEntity = new HttpEntity(postDataJson, httpHeaders);

		JSONObject respJsonObj = restTemplate.postForObject(url, httpEntity, JSONObject.class);
		log.info("图文客服消息发送结果：" + respJsonObj);
	}

	/**
	 * 发送模版消息到公众号
	 *
	 * @param templateMessageVo 模版消息对象
	 */
	@Override
	public void sendTemplateMessage(TemplateMessageVo templateMessageVo) {
		if(templateMessageVo == null){
			return ;
		}
		String accessToken = stringRedisTemplate.opsForValue().get(WxGzhParamConstant.WX_GZH_SERVICE_API_TOKEN_KEY_PREFIX.concat(wxGzhAppId));
		log.info("发送模版消息，微信ServiceApiToken：" + accessToken);
		if(StringUtils.isBlank(accessToken)){
			log.warn("微信ServiceApiToken为空，未执行模版消息发送操作");
			return ;
		}
		String url = WxGzhParamConstant.SEND_TEMPLATE_MESSAGE_URL;
		if(!StringUtils.isBlank(url)){
			url = url.replaceAll("ACCESS_TOKEN", accessToken);
		}
		// 设置请求头
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);
		// 将请求头和请求参数设置到HttpEntity中
		HttpEntity httpEntity = new HttpEntity(templateMessageVo, httpHeaders);

		JSONObject respJsonObj = restTemplate.postForObject(url, httpEntity, JSONObject.class);
		log.info("模版消息发送结果：" + respJsonObj);
	}


}
