package org.springblade.common.constant;

/**
 * 通用常量
 *
 * @author Chill
 */
public interface CommonConstant {

	/**
	 * MD5加密，盐
	 */
	String MD5_SALT = "!qazxcde32";

	/**
	 * 鉴权随机值Redis Key前缀
	 */
	String AUTH_NONCE_KEY_PREFIX = "authNonce:";


}
