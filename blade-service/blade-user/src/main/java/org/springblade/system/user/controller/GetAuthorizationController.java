package org.springblade.system.user.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springblade.core.tool.api.R;
import org.springblade.system.user.entity.WxUser;
import org.springblade.system.user.service.IWxUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
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
@RestController
@RequestMapping("/getAuthorization")
@Api(value = "获取授权，取得用户信息", tags = "获取授权，取得用户信息")
public class GetAuthorizationController {

	@Value("${jgyhw.wxGzh.getAccessTokenOpenIdReqUrl:}")
	private String wxGzhGetAccessTokenOpenIdReqUrl;

	@Value("${jgyhw.wxGzh.getUserInfoReqUrl:}")
	private String wxGzhGetUserInfoReqUrl;

	@Value("${jgyhw.wxGzh.appId:}")
	private String wxGzhAppId;

	@Value("${jgyhw.wxGzh.appSecret:}")
	private String wxGzhAppSecret;

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
    public R getWxGzhUserInfoByCode(HttpServletRequest request){
        String code = request.getParameter("code");
        Map<String, String> accessTokenOpenIdMap = getAccessTokenAndOpenIdByCode(code);
        if(accessTokenOpenIdMap != null) {
			String url = wxGzhGetUserInfoReqUrl;
			if (StringUtils.isBlank(url)) {
				return R.status(false);
			}
			url = url.replaceAll("ACCESS_TOKEN", accessTokenOpenIdMap.get("accessToken"));
			url = url.replaceAll("OPENID", accessTokenOpenIdMap.get("openId"));

			String resp = restTemplate.getForObject(url, String.class);
			log.info("通过临时凭证Code获取微信公众号用户信息结果：" + resp);

			if (StringUtils.isNotBlank(resp)) {
				JSONObject respJsonObj = JSONObject.parseObject(resp);
				if (StringUtils.isNotBlank(respJsonObj.getString("errcode"))) {
					log.error("通过临时凭证Code获取微信公众号用户信息，错误编号：" + respJsonObj.getString("errcode") + "；错误描述：" + respJsonObj.getString("errmsg"));
					return R.status(false);
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
					// TODO 跳转登陆成功页面

					// resultPageUrl = "redirect:/wxGzhMessageSubscribe/openMessageSubscribeSuccessPage";
					return R.status(true);
				}
			} else {
				log.error("通过临时凭证Code获取微信公众号用户信息结果为空");
				return R.status(false);
			}
		}else{
        	return R.status(false);
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
        String url = wxGzhGetAccessTokenOpenIdReqUrl;
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
			if(StringUtils.isNotBlank(respJsonObj.getString("errcode"))){
				log.error("根据临时凭证Code获取微信公众号access_token和openid，错误编号：" + respJsonObj.getString("errcode") + "；错误描述：" + respJsonObj.getString("errmsg"));
				resultMap = null;
			}else{
				resultMap.put("accessToken", respJsonObj.getString("access_token"));
				resultMap.put("openId", respJsonObj.getString("openid"));
			}
		}else{
			log.error("根据临时凭证Code获取微信公众号access_token和openid结果为空");
			resultMap = null;
		}
		return resultMap;
    }

}
