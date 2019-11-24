package org.springblade.system.user.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springblade.common.constant.WxGzhParamConstant;
import org.springblade.system.user.entity.WxUser;
import org.springblade.system.user.service.IWxUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 获取授权，取得用户信息控制器
 *
 * Created by WangLei on 2019/11/19 0019 19:26
 */
@Slf4j
@RefreshScope
@Controller
@RequestMapping("/getAuthorization")
@Api(value = "获取授权，取得用户信息", tags = "获取授权，取得用户信息")
public class GetAuthorizationController {

	@Value("${jgyhw.wxGzh.appId:}")
	private String wxGzhAppId;

	@Value("${jgyhw.wxGzh.appSecret:}")
	private String wxGzhAppSecret;

	@Value("${jgyhw.wxGzh.loginResultPageUrl:}")
	private String loginResultPageUrl;

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private IWxUserService wxUserService;

	/**
	 * 通过临时凭证Code获取微信公众号用户信息
	 *
	 * @param request
	 * @return
	 */
	@RequestMapping("/getWxGzhUserInfoByCode")
	public String getWxGzhUserInfoByCode(HttpServletRequest request){
		String resultPageUrl = loginResultPageUrl;

		String code = request.getParameter("code");
		Map<String, String> accessTokenOpenIdMap = getAccessTokenAndOpenIdByCode(code);
		if(accessTokenOpenIdMap != null) {
			String url = WxGzhParamConstant.GET_USER_INFO_REQ_URL;
			if (StringUtils.isBlank(url)) {
				resultPageUrl = resultPageUrl.replaceAll("NIKE_NAME", "");
				resultPageUrl = resultPageUrl.replaceAll("HEAD_IMG", "");
				resultPageUrl = resultPageUrl.replaceAll("STATUS", "false");
				return resultPageUrl;
			}
			url = url.replaceAll("ACCESS_TOKEN", accessTokenOpenIdMap.get("accessToken"));
			url = url.replaceAll("OPENID", accessTokenOpenIdMap.get("openId"));

			String resp = restTemplate.getForObject(url, String.class);
			log.info("通过临时凭证Code获取微信公众号用户信息结果：" + resp);

			if (StringUtils.isNotBlank(resp)) {
				JSONObject respJsonObj = JSONObject.parseObject(resp);
				if (StringUtils.isNotBlank(respJsonObj.getString("errcode"))) {
					log.error("通过临时凭证Code获取微信公众号用户信息，错误编号：" + respJsonObj.getString("errcode") + "；错误描述：" + respJsonObj.getString("errmsg"));
					resultPageUrl = resultPageUrl.replaceAll("NIKE_NAME", "");
					resultPageUrl = resultPageUrl.replaceAll("HEAD_IMG", "");
					resultPageUrl = resultPageUrl.replaceAll("STATUS", "false");
					return resultPageUrl;
				} else {
					// 保存用户信息
					String unionId = respJsonObj.getString("unionid");
					WxUser wu = wxUserService.getOne(Wrappers.<WxUser>lambdaQuery().eq(WxUser::getUnionId, unionId));
					if (wu == null) {
						wu = new WxUser();
						wu.setCreateTime(new Date());
					}
					wu.setUnionId(unionId);
					wu.setOpenIdGzh(respJsonObj.getString("openid"));
					wu.setNickName(respJsonObj.getString("nickname"));
					wu.setSex(Integer.valueOf(respJsonObj.getString("sex")));
					wu.setProvince(respJsonObj.getString("province"));
					wu.setCity(respJsonObj.getString("city"));
					wu.setHeadImgUrl(respJsonObj.getString("headimgurl"));
					wu.setUpdateTime(new Date());
					wxUserService.saveOrUpdate(wu);
					log.info("用户登陆成功，创建/更新用户信息：" + wu.toString());
					String nickName = "";
					try {
						nickName = URLEncoder.encode(wu.getNickName(), "utf-8");
					} catch (UnsupportedEncodingException e) {
						log.error("登陆结果跳转链接用户昵称Encode编码异常", e);
					}
					resultPageUrl = resultPageUrl.replaceAll("NIKE_NAME", nickName);
					resultPageUrl = resultPageUrl.replaceAll("HEAD_IMG", wu.getHeadImgUrl());
					resultPageUrl = resultPageUrl.replaceAll("STATUS", "true");
					return resultPageUrl;
				}
			} else {
				log.error("通过临时凭证Code获取微信公众号用户信息结果为空");
				resultPageUrl = resultPageUrl.replaceAll("NIKE_NAME", "");
				resultPageUrl = resultPageUrl.replaceAll("HEAD_IMG", "");
				resultPageUrl = resultPageUrl.replaceAll("STATUS", "false");
				return resultPageUrl;
			}
		}else{
			resultPageUrl = resultPageUrl.replaceAll("NIKE_NAME", "");
			resultPageUrl = resultPageUrl.replaceAll("HEAD_IMG", "");
			resultPageUrl = resultPageUrl.replaceAll("STATUS", "false");
			return resultPageUrl;
		}
	}

	/**
	 * 根据临时凭证Code获取微信公众号access_token和openid
	 *
	 * @param code 临时凭证
	 * @return
	 */
	private Map<String, String> getAccessTokenAndOpenIdByCode(String code){
		Map<String, String> resultMap = new HashMap<>();
		if(StringUtils.isBlank(code)){
			return null;
		}
		String url = WxGzhParamConstant.GET_ACCESS_TOKEN_OPEN_ID_REQ_URL;
		if(StringUtils.isBlank(url)){
			return null;
		}
		url = url.replaceAll("APPID", wxGzhAppId);
		url = url.replaceAll("SECRET", wxGzhAppSecret);
		url = url.replaceAll("CODE", code);

		String resp = restTemplate.getForObject(url, String.class);
		log.info("根据临时凭证Code获取微信公众号access_token和openid结果：" + resp);
		if(StringUtils.isNotBlank(resp)){
			JSONObject respJsonObj = JSONObject.parseObject(resp);
			if(respJsonObj.containsKey("access_token") && respJsonObj.containsKey("openid")){
				resultMap.put("accessToken", respJsonObj.getString("access_token"));
				resultMap.put("openId", respJsonObj.getString("openid"));
			}else{
				log.error("根据临时凭证Code获取微信公众号access_token和openid，错误编号：" + respJsonObj.getString("errcode") + "；错误描述：" + respJsonObj.getString("errmsg"));
				resultMap = null;
			}
		}else{
			log.error("根据临时凭证Code获取微信公众号access_token和openid结果为空");
			resultMap = null;
		}
		return resultMap;
	}

}
