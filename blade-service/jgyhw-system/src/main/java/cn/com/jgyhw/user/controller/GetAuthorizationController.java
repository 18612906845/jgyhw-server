package cn.com.jgyhw.user.controller;

import cn.com.jgyhw.user.entity.WxUser;
import cn.com.jgyhw.user.entity.WxXcxSessionKey;
import cn.com.jgyhw.user.service.IWxUserService;
import cn.com.jgyhw.user.service.IWxXcxSessionKeyService;
import cn.com.jgyhw.user.util.WxXcxUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springblade.common.constant.WxGzhParamConstant;
import org.springblade.common.constant.WxXcxParamConstant;
import org.springblade.core.tool.api.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
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

	@Autowired
	private IWxXcxSessionKeyService wxXcxSessionKeyService;

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

	/**
	 * 通过临时凭证Code获取微信小程序OpenId和SessionKey
	 *
	 * @param code 临时凭证
	 * @return
	 */
	@GetMapping("/getXcxOpenIdAndSessionKeyByCode")
	@ResponseBody
	public R<String> getXcxOpenIdAndSessionKeyByCode(String code){

		String url = WxXcxParamConstant.WX_XCX_GET_OPENID_REQ_URL;
		url = url.replaceAll("APPID", WxXcxParamConstant.APP_ID);
		url = url.replaceAll("SECRET", WxXcxParamConstant.APP_SECRET);
		url = url.replaceAll("JSCODE", code);

		String resp = restTemplate.getForObject(url, String.class);
		log.info("通过临时凭证Code获取微信小程序OpenId和SessionKey结果：" + resp);
		if(StringUtils.isNotBlank(resp)){
			JSONObject respJsonObj = JSONObject.parseObject(resp);
			if(respJsonObj.containsKey("openid") && respJsonObj.containsKey("session_key")){
				String openId = respJsonObj.getString("openid");
				WxXcxSessionKey wxsk = wxXcxSessionKeyService.getOne(Wrappers.<WxXcxSessionKey>lambdaQuery().eq(WxXcxSessionKey::getOpenId, openId));
				if(wxsk == null){
					wxsk = new WxXcxSessionKey();
				}
				wxsk.setOpenId(openId);
				wxsk.setSessionKey(respJsonObj.getString("session_key"));
				wxXcxSessionKeyService.saveOrUpdate(wxsk);
				return R.data(wxsk.getId().toString());
			}else{
				log.error("通过临时凭证Code获取微信小程序OpenId和SessionKey，错误编号：" + respJsonObj.getString("errcode") + "；错误描述：" + respJsonObj.getString("errmsg"));
				return R.status(false);
			}
		}else{
			log.error("通过临时凭证Code获取微信小程序OpenId和SessionKey结果为空");
			return R.status(false);
		}
	}

	/**
	 * 获取微信用户UnionId
	 *
	 * @param encryptedData 加密数据字符串
	 * @param tempId 会话密匙临时表id
	 * @param iv 自定义对称解密算法初始向量
	 * @return
	 */
	@GetMapping("/getWxUserUnionId")
	@ResponseBody
	public R<String> getWxUserUnionId(String tempId, String encryptedData, String iv){

		WxXcxSessionKey wxsk = wxXcxSessionKeyService.getById(tempId);
		if(wxsk == null){
			log.error("会话密匙数据不存在，会话密匙临时表id：" + tempId);
			return R.status(false);
		}

		if(StringUtils.isBlank(wxsk.getSessionKey())){
			log.error("会话密匙为空");
			return R.status(false);
		}

		String encryptedDataResult = WxXcxUtil.decryptData(encryptedData, wxsk.getSessionKey(), iv);

		if(StringUtils.isBlank(encryptedDataResult)){
			log.error("msg", "解析失败");
			return R.status(false);
		}
		log.info("解析微信小程序用户信息结果：" + encryptedDataResult);
		JSONObject jsonObj = JSONObject.parseObject(encryptedDataResult);

		String unionId = jsonObj.getString("unionId");
		WxUser wu = wxUserService.getOne(Wrappers.<WxUser>lambdaQuery().eq(WxUser::getUnionId, unionId));
		if (wu == null) {
			wu = new WxUser();
			wu.setCreateTime(new Date());
		}
		wu.setOpenIdXcx(jsonObj.getString("openId"));
		wu.setSessionKeyXcx(wxsk.getSessionKey());
		wu.setUnionId(unionId);
		wu.setNickName(jsonObj.getString("nickName"));
		wu.setSex(jsonObj.getInteger("gender"));
		wu.setProvince(jsonObj.getString("province"));
		wu.setCity(jsonObj.getString("city"));
		wu.setCountry(jsonObj.getString("country"));
		wu.setHeadImgUrl(jsonObj.getString("avatarUrl"));
		wu.setUpdateTime(new Date());
		wxUserService.saveOrUpdate(wu);

		return R.data(wu.getId().toString());
	}

	/**
	 * 同步微信用户公共信息
	 *
	 * @param wxUser 微信用户信息
	 * @return
	 */
	@PostMapping("/syncWxUserCommonInfo")
	@ResponseBody
	public R syncWxUserCommonInfo(@RequestBody WxUser wxUser){
		// 查询用户信息
		WxUser wu = wxUserService.getById(wxUser.getId());
		if(wu != null){
			wu.setNickName(wxUser.getNickName());
			wu.setSex(wxUser.getSex());
			wu.setProvince(wxUser.getProvince());
			wu.setCity(wxUser.getCity());
			wu.setCountry(wxUser.getCountry());
			wu.setHeadImgUrl(wxUser.getHeadImgUrl());
			wu.setUpdateTime(new Date());
			wxUserService.updateById(wu);
		}
		return R.status(true);
	}

}
