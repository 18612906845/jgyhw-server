package cn.com.jgyhw.mesage.controller;

import cn.com.jgyhw.goods.feign.IJdGoodsClient;
import cn.com.jgyhw.mesage.service.IWxGzhMessageService;
import cn.com.jgyhw.mesage.thread.WxGzhTextMessageDisposeThread;
import cn.com.jgyhw.mesage.util.CheckoutUtil;
import cn.com.jgyhw.mesage.util.WxGzhMessageUtil;
import cn.com.jgyhw.message.vo.TemplateMessageVo;
import cn.com.jgyhw.message.vo.TextMessageVo;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiOperationSupport;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springblade.common.tool.CommonUtil;
import org.springblade.core.tool.api.R;
import org.springblade.system.user.feign.IWxUserClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 微信公众号消息处理
 *
 * Created by WangLei on 2019/11/19 0019 19:26
 */
@Slf4j
@RefreshScope
@RestController
@RequestMapping("/wxGzhMessage")
public class WxGzhMessageController {

	@Value("${jgyhw.wxGzh.attentionGreeting:感谢您的关注}")
	private String attentionGreeting;

	@Value("${jgyhw.wxGzh.loginMessageContent:}")
	private String loginMessageContent;

	@Value("${jgyhw.jd.regexpVerifyJdGoodsPageUrl}")
	private String regexpVerifyJdGoodsPageUrl;

	@Value("${jgyhw.jd.regexpAllNumber}")
	private String regexpAllNumber;

	@Value("${jgyhw.jd.regexpExtractUrlJdGoodsId}")
	private String regexpExtractUrlJdGoodsId;

	@Value("${jgyhw.system.returnMoneyShareDefault}")
	private Integer systemReturnMoneyShareDefault;

	@Value("${jgyhw.wxGzh.templateMessageIdDdqr}")
	private String wxGzhTemplateMessageIdDdqr;

	@Value("${jgyhw.wxGzh.templateMessageIdDdwc}")
	private String wxGzhTemplateMessageIdDdwc;

	@Value("${jgyhw.wxGzh.templateMessageIdHdfx}")
	private String wxGzhTemplateMessageIdHdfx;

	@Value("${jgyhw.wxGzh.templateMessageIdTxcg}")
	private String wxGzhTemplateMessageIdTxcg;

	@Autowired
	private CheckoutUtil checkoutUtil;

	@Autowired
	private IWxUserClient wxUserClient;

	@Autowired
	private IJdGoodsClient jdGoodsClient;

	@Autowired
	private IWxGzhMessageService wxGzhMessageService;

	/**
	 * 接收微信公众号消息
	 *
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping("/receiveMessage")
	@ApiOperationSupport(order = 1)
	@ApiOperation(value = "接收微信公众号消息", notes = "")
	public void receiveWxMessage(HttpServletRequest request, HttpServletResponse response) throws IOException {
		log.info("接收微信公众号消息开始--> 开始时间毫秒：" + System.currentTimeMillis());
		boolean isGet = request.getMethod().toLowerCase().equals("get");
		PrintWriter print;

		if (isGet) {
			// 微信加密签名
			String signature = request.getParameter("signature");
			// 时间戳
			String timestamp = request.getParameter("timestamp");
			// 随机数
			String nonce = request.getParameter("nonce");
			// 随机字符串
			String echostr = request.getParameter("echostr");
			log.info("微信公众号Get请求-->signature：" + signature + "；timestamp：" + timestamp + "；nonce：" + nonce + "；echostr：" + echostr);
			// 通过检验signature对请求进行校验，若校验成功则原样返回echostr，表示接入成功，否则接入失败
			if (signature != null && checkoutUtil.checkSignature(signature, timestamp, nonce)) {
				try {
					print = response.getWriter();
					print.write(echostr);
					print.flush();
				} catch (IOException e) {
					e.printStackTrace();
					log.info("微信消息验证IO异常：", e);
				}
			}
		} else {
			log.info("微信公众号Post请求");
			print = response.getWriter();
			// 接收消息并返回消息，调用核心服务类接收处理请求
			String respXml = new String(this.processRequest(request).getBytes("UTF-8"), "UTF-8");// ISO-8859-1 UTF-8
			print.print(respXml);
			print.flush();
			print.close();
		}
		log.info("接收微信公众号消息结束--> 结束时间毫秒：" + System.currentTimeMillis());
	}

	/**
	 * 发送文本消息
	 *
	 * @param toUser 接收者微信公众号标识
	 * @param content 消息内容
	 * @return
	 */
	@GetMapping("/sendTextMessage")
	@ApiOperationSupport(order = 2)
	@ApiOperation(value = "发送文本消息", notes = "")
	public R sendTextMessage(@ApiParam(value = "商品编号", required = true) String toUser,
							 @ApiParam(value = "商品编号", required = true) String content){
		wxGzhMessageService.sendTextMessage(toUser, content);
		return R.status(true);
	}

	/**
	 * 发送确认订单微信消息
	 *
	 * @param openIdGzh 微信用户公众号标识
	 * @param orderId 订单编号
	 * @param orderStatus 订单状态
	 * @return
	 */
	@GetMapping("/sendAffirmOrderWxMessage")
	@ApiOperationSupport(order = 3)
	@ApiOperation(value = "发送模版消息到公众号", notes = "")
	public R sendAffirmOrderWxMessage(@ApiParam(value = "微信用户公众号标识", required = true) String openIdGzh,
									  @ApiParam(value = "订单编号", required = true) String orderId,
									  @ApiParam(value = "订单状态", required = true) String orderStatus){
		TemplateMessageVo tmVo = new TemplateMessageVo();

		tmVo.setTouser(openIdGzh);
		tmVo.setTemplate_id(wxGzhTemplateMessageIdDdqr);
		Map<String, Object> dataMap = new HashMap<>();
		Map<String, Object> firstMap = new HashMap<>();
		firstMap.put("value", "您好，已确认新订单");
		dataMap.put("first", firstMap);

		Map<String, Object> keyword1Map = new HashMap<>();
		keyword1Map.put("value", orderId);
		dataMap.put("keyword1", keyword1Map);

		Map<String, Object> keyword2Map = new HashMap<>();
		keyword2Map.put("value", orderStatus);
		keyword2Map.put("color", "#008000");
		dataMap.put("keyword2", keyword2Map);

		Map<String, Object> keyword3Map = new HashMap<>();
		keyword3Map.put("value", CommonUtil.dateToStringByFormat(new Date(), "yyyy-MM-dd HH:mm:ss"));
		dataMap.put("keyword3", keyword3Map);

		Map<String, Object> remarkMap = new HashMap<>();
		remarkMap.put("value", "请持续关注后续完成订单通知");
		dataMap.put("remark", remarkMap);
		tmVo.setData(dataMap);
		wxGzhMessageService.sendTemplateMessage(tmVo);
		return R.status(true);
	}

	/**
	 * 发送完成订单微信消息
	 *
	 * @param openIdGzh 微信用户公众号标识
	 * @param orderId 订单编号
	 * @param orderFinishTime 订单完成时间
	 */
	@GetMapping("/sendFinishOrderWxMessage")
	@ApiOperationSupport(order = 4)
	@ApiOperation(value = "发送完成订单微信消息", notes = "")
	public R sendFinishOrderWxMessage(@ApiParam(value = "微信用户公众号标识", required = true) String openIdGzh,
									  @ApiParam(value = "订单编号", required = true) String orderId,
									  @ApiParam(value = "订单完成时间", required = true) Date orderFinishTime){
		TemplateMessageVo tmVo = new TemplateMessageVo();
		tmVo.setTouser(openIdGzh);
		tmVo.setTemplate_id(wxGzhTemplateMessageIdDdwc);
		Map<String, Object> dataMap = new HashMap<>();
		Map<String, Object> firstMap = new HashMap<>();
		firstMap.put("value", "您好，订单已完成");
		dataMap.put("first", firstMap);

		Map<String, Object> keyword1Map = new HashMap<>();
		keyword1Map.put("value", orderId);
		dataMap.put("keyword1", keyword1Map);

		Map<String, Object> keyword2Map = new HashMap<>();
		keyword2Map.put("value", CommonUtil.dateToStringByFormat(orderFinishTime, "yyyy-MM-dd HH:mm:ss"));
		dataMap.put("keyword2", keyword2Map);

		Map<String, Object> remarkMap = new HashMap<>();
		remarkMap.put("value", "请持续关注后续解冻返现入账通知");
		dataMap.put("remark", remarkMap);
		tmVo.setData(dataMap);
		wxGzhMessageService.sendTemplateMessage(tmVo);
		return R.status(true);
	}

	/**
	 * 发送获得返现微信消息
	 *
	 * @param openIdGzh 微信用户公众号标识
	 * @param returnMoneyCause 返现原因
	 * @param returnMoney 返现金额
	 */
	@GetMapping("/sendRebateWxMessage")
	@ApiOperationSupport(order = 5)
	@ApiOperation(value = "发送获得返现微信消息", notes = "")
	public R sendRebateWxMessage(@ApiParam(value = "微信用户公众号标识", required = true) String openIdGzh,
								 @ApiParam(value = "返现原因", required = true) String returnMoneyCause,
								 @ApiParam(value = "返现金额", required = true) double returnMoney){
		TemplateMessageVo tmVo = new TemplateMessageVo();
		tmVo.setTouser(openIdGzh);
		tmVo.setTemplate_id(wxGzhTemplateMessageIdHdfx);
		Map<String, Object> dataMap = new HashMap<>();
		Map<String, Object> firstMap = new HashMap<>();
		firstMap.put("value", "您好，新返现已入账");
		dataMap.put("first", firstMap);

		Map<String, Object> keyword1Map = new HashMap<>();
		keyword1Map.put("value", returnMoneyCause);
		dataMap.put("keyword1", keyword1Map);

		Map<String, Object> keyword2Map = new HashMap<>();
		keyword2Map.put("value", CommonUtil.formatDouble(returnMoney) + "元");
		keyword2Map.put("color", "#008000");
		dataMap.put("keyword2", keyword2Map);

		Map<String, Object> keyword3Map = new HashMap<>();
		keyword3Map.put("value", "入账到个人中心，可取现金额");
		dataMap.put("keyword3", keyword3Map);

		Map<String, Object> remarkMap = new HashMap<>();
		remarkMap.put("value", "进入个人中心->自助提现申请，提现到微信零钱，欢迎再次光顾");
		dataMap.put("remark", remarkMap);
		tmVo.setData(dataMap);
		wxGzhMessageService.sendTemplateMessage(tmVo);
		return R.status(true);
	}

	/**
	 * 发送支付成功消息
	 *
	 * @param openIdGzh 微信用户公众号标识
	 * @param payMoney 支付金额
	 * @param payTime 支付时间
	 */
	@GetMapping("/sendPaySuccessWxMessage")
	@ApiOperationSupport(order = 6)
	@ApiOperation(value = "发送支付成功消息", notes = "")
	public R sendPaySuccessWxMessage(@ApiParam(value = "微信用户公众号标识", required = true) String openIdGzh,
									 @ApiParam(value = "支付金额", required = true) Double payMoney,
									 @ApiParam(value = "支付时间", required = true) Date payTime){
		TemplateMessageVo tmVo = new TemplateMessageVo();
		tmVo.setTouser(openIdGzh);
		tmVo.setTemplate_id(wxGzhTemplateMessageIdTxcg);
		Map<String, Object> dataMap = new HashMap<>();
		Map<String, Object> firstMap = new HashMap<>();
		firstMap.put("value", "恭喜您，提现成功");
		dataMap.put("first", firstMap);

		Map<String, Object> keyword1Map = new HashMap<>();
		keyword1Map.put("value", payMoney + "元");
		keyword1Map.put("color", "#e4393c");
		dataMap.put("keyword1", keyword1Map);

		Map<String, Object> keyword2Map = new HashMap<>();
		keyword2Map.put("value", CommonUtil.dateToStringByFormat(payTime, "yyyy-MM-dd HH:mm:ss"));
		dataMap.put("keyword2", keyword2Map);

		Map<String, Object> remarkMap = new HashMap<>();
		remarkMap.put("value", "提现金额已到账微信钱包，请及时查收");
		dataMap.put("remark", remarkMap);
		tmVo.setData(dataMap);
		wxGzhMessageService.sendTemplateMessage(tmVo);
		return R.status(true);
	}

	/**
	 * 处理消息分发
	 *
	 * @param request 请求参数
	 * @return
	 */
	private String processRequest(HttpServletRequest request) {
		// xml格式的消息数据
		String respXml = null;
		try {
			// 调用parseXml方法解析请求消息
			Map requestMap = WxGzhMessageUtil.parseXml(request);
			// 发送方帐号
			String fromUserName = (String) requestMap.get("FromUserName");
			// 开发者微信号
			String toUserName = (String) requestMap.get("ToUserName");
			// 消息类型
			String msgType = (String) requestMap.get("MsgType");

			log.info("FromUserName：" + fromUserName + "；ToUserName：" + toUserName + "；MsgType：" + msgType);

			// 文本消息
			TextMessageVo tmVo = new TextMessageVo();
			tmVo.setToUserName(fromUserName);
			tmVo.setFromUserName(toUserName);
			tmVo.setCreateTime(System.currentTimeMillis());
			tmVo.setMsgType(WxGzhMessageUtil.RESP_MESSAGE_TYPE_TEXT);

			if (msgType.equals(WxGzhMessageUtil.REQ_MESSAGE_TYPE_TEXT)) {// 文字消息
				String content = (String) requestMap.get("Content");
				content.trim();
				log.info("处理文字消息，文字内容：" + content);
				//验证文字是否是全部数字
				String goodsId = "";
				Pattern numberPattern = Pattern.compile(regexpAllNumber);
				//验证文字是否是网址
				Pattern urlPattern = Pattern.compile(regexpVerifyJdGoodsPageUrl);
				if(numberPattern.matcher(content).matches()){
					goodsId = content;
				}else if(urlPattern.matcher(content).matches()){
					// 提取URL里的商品编号
					Pattern pattern = Pattern.compile(regexpExtractUrlJdGoodsId);
					Matcher m = pattern.matcher(content);
					while (m.find()) {
						goodsId += m.group(1);
					}
				}
				if(StringUtils.isNotBlank(goodsId)){
					// 开启线程处理消息
					WxGzhTextMessageDisposeThread wgtmdt = new WxGzhTextMessageDisposeThread();
					wgtmdt.setGoodsId(goodsId);
					wgtmdt.setReceiveGzhOpenId(fromUserName);
					wgtmdt.setLoginMessageContent(loginMessageContent);
					wgtmdt.setSystemReturnMoneyShareDefault(systemReturnMoneyShareDefault);
					wgtmdt.setWxUserClient(wxUserClient);
					wgtmdt.setJdGoodsClient(jdGoodsClient);
					wgtmdt.setWxGzhMessageService(wxGzhMessageService);
					new Thread(wgtmdt, "文字消息处理线程" + System.currentTimeMillis()).start();
					// 直接返回成功
					return "success";
				}else{
					tmVo.setContent("无法识别【" + content + "】，请发送正确的京东商品编号或商品链接获取优惠信息");
					// 将文本消息对象转换成xml
					respXml = WxGzhMessageUtil.textMessageVoToXml(tmVo);
					return respXml;
				}
			}else if (msgType.equals(WxGzhMessageUtil.REQ_MESSAGE_TYPE_IMAGE)) {// 图片消息
				tmVo.setContent("敢发张自拍照让我看看嘛/::P");
				// 将文本消息对象转换成xml
				respXml = WxGzhMessageUtil.textMessageVoToXml(tmVo);
				return respXml;
			}else if (msgType.equals(WxGzhMessageUtil.REQ_MESSAGE_TYPE_VOICE)) {// 语音消息
				tmVo.setContent("哎呀，普通话还不如我呢[Smart]");
				// 将文本消息对象转换成xml
				respXml = WxGzhMessageUtil.textMessageVoToXml(tmVo);
				return respXml;
			}else if (msgType.equals(WxGzhMessageUtil.REQ_MESSAGE_TYPE_VIDEO)) {// 视频消息
				tmVo.setContent("好厉害的样子，可是我看不懂/::O");
				// 将文本消息对象转换成xml
				respXml = WxGzhMessageUtil.textMessageVoToXml(tmVo);
				return respXml;
			}else if (msgType.equals(WxGzhMessageUtil.REQ_MESSAGE_TYPE_LOCATION)) {// 地理位置消息
				tmVo.setContent("你就不怕我过去打劫嘛\uE14C");
				// 将文本消息对象转换成xml
				respXml = WxGzhMessageUtil.textMessageVoToXml(tmVo);
				return respXml;
			}else if (msgType.equals(WxGzhMessageUtil.REQ_MESSAGE_TYPE_LINK)) {// 链接消息
				tmVo.setContent("我不喝鸡汤，谢谢/:share");
				// 将文本消息对象转换成xml
				respXml = WxGzhMessageUtil.textMessageVoToXml(tmVo);
				return respXml;
			}else if (msgType.equals(WxGzhMessageUtil.REQ_MESSAGE_TYPE_EVENT)) {// 事件推送
				// 事件类型
				String eventType = (String) requestMap.get("Event");
				if (eventType.equals(WxGzhMessageUtil.EVENT_TYPE_SUBSCRIBE)) {// 关注
					String[] contentArray = attentionGreeting.split("；");
					String contentStr = "";
					for(int i=0; i<contentArray.length; i++){
						contentStr += contentArray[i] + "\n";
						if(i < contentArray.length - 1 && !StringUtils.isBlank(contentStr)){
							contentStr += "\n";
						}
					}
					tmVo.setContent(contentStr);
					// 将文本消息对象转换成xml
					respXml = WxGzhMessageUtil.textMessageVoToXml(tmVo);
					return respXml;
				} else if (eventType.equals(WxGzhMessageUtil.EVENT_TYPE_UNSUBSCRIBE)) {// 取消关注
					log.info("用户取消关注");
				} else if (eventType.equals(WxGzhMessageUtil.EVENT_TYPE_SCAN)) {// 扫描带参数二维码
					String eventKey = (String) requestMap.get("EventKey");
					log.info("二维码参数：" + eventKey);
				} else if (eventType.equals(WxGzhMessageUtil.EVENT_TYPE_LOCATION)) {// 上报地理位置

				} else if (eventType.equals(WxGzhMessageUtil.EVENT_TYPE_CLICK)) {// 自定义菜单
					String eventKey = (String) requestMap.get("EventKey");
					log.info("自定义菜单：" + eventKey);
				}
			}
			return "success";
		} catch (Exception e) {
			log.error("微信公众号消息处理异常", e);
			return "success";
		}
	}


}
