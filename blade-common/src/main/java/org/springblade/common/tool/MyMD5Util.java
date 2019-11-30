package org.springblade.common.tool;

import java.security.MessageDigest;

/**
 * MD5计算工具
 *
 * Created by WangLei on 2019/11/30 0030 22:16
 */
public class MyMD5Util {

	//盐，用于混交md5
	private static final String SLAT = "!qazxcde32";

	public static String getMd5(String dataStr) {
		try {
			dataStr = dataStr + SLAT;
			MessageDigest m = MessageDigest.getInstance("MD5");
			m.update(dataStr.getBytes("UTF8"));
			byte s[] = m.digest();
			String result = "";
			for (int i = 0; i < s.length; i++) {
				result += Integer.toHexString((0x000000FF & s[i]) | 0xFFFFFF00).substring(6);
			}
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
}
