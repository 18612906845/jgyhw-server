package cn.com.jgyhw.token.service.impl;

import cn.com.jgyhw.token.service.IWxTokenService;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springblade.common.constant.WxGzhParamConstant;
import org.springblade.common.constant.WxXcxParamConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.TimeUnit;

/**
 * Created by WangLei on 2019/11/22 0022 22:00
 */
@Slf4j
@RefreshScope
@Service
public class WxTokenServiceImpl implements IWxTokenService {

	@Value("${jgyhw.wxGzh.appId:}")
	private String wxGzhAppId;

	@Value("${jgyhw.wxGzh.appSecret:}")
	private String wxGzhAppSecret;

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private StringRedisTemplate stringRedisTemplate;

	/**
	 * 定时更新微信公众号服务Api令牌
	 */
	@Override
	public void timingUpdateWxGzhServiceApiToken() {
		String url = WxGzhParamConstant.WX_UPDATE_SERVICE_API_TOKEN_REQ_URL;
		if(!StringUtils.isBlank(url)){
			url = url.replaceAll("APPID", wxGzhAppId);
			url = url.replaceAll("APPSECRET", wxGzhAppSecret);
		}else{
			log.warn("定时更新微信公众号服务Api令牌，请求地址为空");
			return;
		}
		log.info("定时更新微信公众号服务Api令牌，URL：" + url);

		JSONObject respJsonObj = restTemplate.getForObject(url, JSONObject.class);
		log.info("定时更新微信公众号服务Api令牌结果：" + respJsonObj);
		if(respJsonObj != null){
			if(respJsonObj.containsKey("access_token")){
				String accessToken = respJsonObj.getString("access_token");
				stringRedisTemplate.opsForValue().set(WxGzhParamConstant.WX_GZH_SERVICE_API_TOKEN_KEY_PREFIX.concat(wxGzhAppId), accessToken,2, TimeUnit.HOURS);
				log.info("定时更新微信公众号服务Api令牌，保存Redis成功：" + accessToken);
			}else{
				log.error("定时更新微信公众号服务Api令牌，错误编号：" + respJsonObj.getString("errcode") + "；错误描述：" + respJsonObj.getString("errmsg"));
			}
		}else{
			log.error("定时更新微信公众号服务Api令牌结果为空");
		}
	}

	/**
	 * 定时更新微信小程序服务Api令牌
	 */
	@Override
	public void timingUpdateWxXcxServiceApiToken() {
		String url = WxGzhParamConstant.WX_UPDATE_SERVICE_API_TOKEN_REQ_URL;
		if(!StringUtils.isBlank(url)){
			url = url.replaceAll("APPID", WxXcxParamConstant.APP_ID);
			url = url.replaceAll("APPSECRET", WxXcxParamConstant.APP_SECRET);
		}else{
			log.warn("定时更新微信小程序服务Api令牌，请求地址为空");
			return;
		}
		log.info("定时更新微信小程序服务Api令牌，URL:" + url);

		JSONObject respJsonObj = restTemplate.getForObject(url, JSONObject.class);
		log.info("定时更新微信小程序服务Api令牌结果：" + respJsonObj);
		if(respJsonObj != null){
			if(respJsonObj.containsKey("access_token")){
				String accessToken = respJsonObj.getString("access_token");
				stringRedisTemplate.opsForValue().set(WxXcxParamConstant.WX_XCX_SERVICE_API_TOKEN_KEY_PREFIX.concat(WxXcxParamConstant.APP_ID), accessToken,2, TimeUnit.HOURS);
				log.info("定时更新微信小程序服务Api令牌，保存Redis成功：" + accessToken);
			}else{
				log.error("定时更新微信小程序服务Api令牌，错误编号：" + respJsonObj.getString("errcode") + "；错误描述：" + respJsonObj.getString("errmsg"));
			}
		}else{
			log.error("定时更新微信小程序服务Api令牌结果为空");
		}
	}
}
