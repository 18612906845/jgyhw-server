package org.springblade.common.tool;

import io.micrometer.core.instrument.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springblade.common.constant.CommonConstant;

import java.security.MessageDigest;

/**
 * MD5计算工具
 *
 * Created by WangLei on 2019/11/30 0030 22:16
 */
@Slf4j
public class MD5Util {

	/**
	 * 获取MD5值
	 *
	 * @param dataStr
	 * @return
	 */
	public static String stringToMD5(String dataStr) {
		if(StringUtils.isBlank(dataStr)){
			return "";
		}
		try {
			dataStr = dataStr + CommonConstant.MD5_SALT;
			MessageDigest m = MessageDigest.getInstance("MD5");
			m.update(dataStr.getBytes("UTF8"));
			byte s[] = m.digest();
			String result = "";
			for (int i = 0; i < s.length; i++) {
				result += Integer.toHexString((0x000000FF & s[i]) | 0xFFFFFF00).substring(6);
			}
			return result.toUpperCase();
		} catch (Exception e) {
			log.error("MD5加密计算异常", e);
		}
		return "";
	}
}
