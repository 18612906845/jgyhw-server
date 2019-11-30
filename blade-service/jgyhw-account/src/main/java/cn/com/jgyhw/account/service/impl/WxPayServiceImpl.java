package cn.com.jgyhw.account.service.impl;

import cn.com.jgyhw.account.service.IWxPayService;
import com.jd.open.api.sdk.internal.util.CodecUtil;
import com.jd.open.api.sdk.internal.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springblade.common.constant.WxXcxParamConstant;
import org.springblade.common.tool.CommonUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.net.ssl.SSLContext;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.security.KeyStore;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by WangLei on 2019/11/30 0030 16:19
 */
@Slf4j
@RefreshScope
@Service
public class WxPayServiceImpl implements IWxPayService {

	@Value("${jgyhw.wxXcx.wxPayServerIp}")
	private String wxPayServerIp;

	@Value("${jgyhw.wxXcx.wxPayCertPath}")
	private String certPath;

	/**
	 * 企业支付到微信零钱
	 *
	 * @param partnerTradeNo 商户订单号，需保持唯一性(只能是字母或者数字，不能包含有其他字符)
	 * @param openid         收款用户标识
	 * @param amount         企业付款金额，单位为分
	 * @param desc           企业付款备注，必填
	 * @return
	 */
	@Override
	public Map<String, String> enterprisePayToWxWallet(String partnerTradeNo, String openid, int amount, String desc) {
		log.info("企业支付到微信零钱Service--> 开始，参数，商户订单号：" + partnerTradeNo + "；收款用户标识：" + openid + "；企业付款金额：" + amount + "；企业付款备注：" + desc);
		CloseableHttpClient httpClient = getSSLHttpclient();
		String url = WxXcxParamConstant.WX_PAY_BY_API_URL;

		HttpPost httpPost = new HttpPost(url);
		CloseableHttpResponse response = null;

		String check_name = "NO_CHECK";
		String nonce_str = CommonUtil.getRandomString();
		String mch_appid = WxXcxParamConstant.APP_ID;

		Map<String, Object> map = new TreeMap<>();
		map.put("mch_appid", mch_appid);
		map.put("mchid", WxXcxParamConstant.WX_PAY_BY_MCHID);
		map.put("nonce_str", nonce_str);
		map.put("partner_trade_no", partnerTradeNo);
		map.put("openid", openid);
		map.put("check_name", check_name);
		map.put("amount", amount);
		map.put("desc", desc);
		map.put("spbill_create_ip", wxPayServerIp);

		String sign = createWxPaySign(map, WxXcxParamConstant.WX_PAY_BY_KEY);
		String requestParamXml = mapToXml(map, false, sign);
		log.info("企业支付到微信零钱Service--> Xml参数：" + requestParamXml);

		Map<String, String> resultMap = new HashMap<>();
		try {
			httpPost.addHeader("Content-Type", "text/xml");
			httpPost.setEntity(new StringEntity(requestParamXml, "UTF-8"));
			response = httpClient.execute(httpPost);
			HttpEntity entity = null;
			if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
				entity = response.getEntity();
				String respContent = EntityUtils.toString(entity, Charset.forName("UTF-8"));
				log.info("企业支付到微信零钱Service--> 结果：" + respContent);
				resultMap = xmlToMap(respContent);
			}
			if(entity != null){
				EntityUtils.consume(entity);
			}
		} catch (IOException e) {
			log.error("企业支付到微信零钱Service--> 异常", e);
		} finally {
			try {
				if(response != null){
					response.close();
				}
				if(httpClient != null){
					httpClient.close();
				}
			} catch (IOException e) {
				log.error("关闭请求企业支付到微信零钱Service--> 异常", e);
			}
			return resultMap;
		}
	}

	/**
	 * 生成微信支付签名
	 *
	 * @param map </br>
	 *          .mch_appid 商户账号appid
	 *          .mchid 商户号
	 *          .nonce_str 随机字符串
	 *          .sign 签名
	 *          .partner_trade_no 商户订单号
	 *          .openid 微信用户标识
	 *          .check_name 校验用户姓名选项
	 *          .amount 支付金额
	 *          .desc 企业付款备注
	 *          .spbill_create_ip Ip地址
	 * @param wxPaykey 支付密匙
	 * @return
	 * @throws Exception
	 */
	private String createWxPaySign(Map<String, Object> map, String wxPaykey){
		log.info("生成微信支付Sign--> 开始，参数：" + map.toString());
		StringBuilder sb = new StringBuilder();
		//按照规则拼成字符串
		for (Map.Entry entry : map.entrySet()) {
			String name = String.valueOf(entry.getKey());
			String value = String.valueOf(entry.getValue());
			//检测参数是否为空
			if (StringUtil.areNotEmpty(new String[]{name, value})) {
				if(sb.length() < 1){
					sb.append(name).append("=").append(value);
				}else{
					sb.append("&").append(name).append("=").append(value);
				}
			}
		}
		String sign = null;
		sb.append("&").append("key").append("=").append(wxPaykey);
		try {
			//计算MD5
			log.error("生成微信支付Sign--> 排序字符串：" + sb.toString());
			sign = CodecUtil.md5(sb.toString());
		}catch (Exception e){
			log.error("生成微信支付Sign--> 异常", e);
		}finally {
			log.info("生成微信支付Sign--> 结束，结果：" + sign);
			return sign;
		}
	}

	/**
	 * 获取带证书SSLHttpclient
	 *
	 * @return
	 */
	private CloseableHttpClient getSSLHttpclient(){
		log.info("获取带证书SSLHttpclient--> 开始");
		CloseableHttpClient httpclient = null;

		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
		String certFilePath = request.getSession().getServletContext().getRealPath("/cert/apiclient_cert.p12");
		try {
			KeyStore keyStore  = KeyStore.getInstance("PKCS12");
			FileInputStream instream = new FileInputStream(new File(certPath));
			keyStore.load(instream, WxXcxParamConstant.WX_PAY_BY_MCHID.toCharArray());
			SSLContext sslcontext = SSLContexts.custom().loadKeyMaterial(keyStore, WxXcxParamConstant.WX_PAY_BY_MCHID.toCharArray()).build();
			SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslcontext,new String[] { "TLSv1" },null,SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
			httpclient = HttpClients.custom().setSSLSocketFactory(sslsf).build();
		} catch (Exception e) {
			e.printStackTrace();
			log.error("获取带证书SSLHttpclient--> 异常", e);
		}finally {
			log.info("获取带证书SSLHttpclient--> 结束");
			return httpclient;
		}
	}

	/**
	 * xml字符串转Map
	 *
	 * @param xmlStr xml字符串
	 * @return
	 */
	private Map<String, String> xmlToMap(String xmlStr){
		log.info("微信支付结果XmlToMap--> 开始，参数XmlStr：" + xmlStr);
		if(StringUtils.isBlank(xmlStr)){
			return null;
		}
		Map<String, String> map = new HashMap<>();
		try {
			Document document = DocumentHelper.parseText(xmlStr);
			Element resultElem = document.getRootElement();
			Iterator it = resultElem.elementIterator();
			while (it.hasNext()) {
				Element element = (Element) it.next();
				map.put(element.getName(), element.getStringValue());
			}
		} catch (Exception e) {
			log.error("微信支付结果XmlToMap--> 异常", e);
		} finally {
			log.info("微信支付结果XmlToMap--> 结束，结果：" + map.toString());
			return map;
		}
	}

	/**
	 * map转Xml字符串
	 *
	 * @param map map元数据
	 * @return
	 */
	public static String mapToXml(Map<String, Object> map, boolean isCDATA, String sign){
		StringBuilder sb = new StringBuilder();
		if(map == null){
			return null;
		}
		sb.append("<xml>");
		for (Map.Entry entry : map.entrySet()) {
			String key = String.valueOf(entry.getKey());
			String value = String.valueOf(entry.getValue());
			//检测参数是否为空
			if (StringUtil.areNotEmpty(new String[]{key, value})) {
				if(isCDATA){
					sb.append("<").append(key).append("><![CDATA[").append(value).append("]]></").append(key).append(">");
				}else{
					sb.append("<").append(key).append(">").append(value).append("</").append(key).append(">");
				}
			}
		}
		if(isCDATA){
			sb.append("<sign><![CDATA[" + sign + "]]></sign>");
		}else{
			sb.append("<sign>" + sign + "</sign>");
		}
		sb.append("</xml>");
		return sb.toString();
	}
}
