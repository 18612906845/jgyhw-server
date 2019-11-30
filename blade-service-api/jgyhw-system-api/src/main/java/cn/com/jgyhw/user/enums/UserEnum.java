package cn.com.jgyhw.user.enums;

/**
 * 用户枚举类
 *
 * Created by WangLei on 2019/11/30 0030 20:26
 */
public enum UserEnum {

	WX_USER_STATUS_WZGZH(0, "未关注"),
	WX_USER_STATUS_GZGZH(1, "关注");

	private int key;

	private String text;

	private UserEnum(int key, String text) {
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
