package cn.com.jgyhw.order.enums;

/**
 * 订单服务枚举类
 *
 * Created by WangLei on 2019/11/24 0024 10:54
 */
public enum OrderEnum {

	JD_VALID_CODE_WXCD(2, "无效拆单"),
	JD_VALID_CODE_WXQX(3, "无效取消"),
	JD_VALID_CODE_DFK(15, "待付款"),
	JD_VALID_CODE_YFK(16, "已付款"),
	JD_VALID_CODE_YWC(17, "已完成"),
	ORDER_PLATFORM_JD(1, "京东平台"),
	ORDER_STATUS_DFK(1, "待付款"),
	ORDER_STATUS_YFK(2, "已付款"),
	ORDER_STATUS_YQX(3, "已取消"),
	ORDER_STATUS_YCT(4, "已成团"),
	ORDER_STATUS_YWC(5, "已完成"),
	ORDER_STATUS_YRZ(6, "已入账"),
	ORDER_STATUS_WX(7, "无效");

	private int key;

	private String text;

	private OrderEnum(int key, String text) {
		this.key = key;
		this.text = text;
	}

	public int getKey() {
		return key;
	}

	public String getText() {
		return text;
	}
}
