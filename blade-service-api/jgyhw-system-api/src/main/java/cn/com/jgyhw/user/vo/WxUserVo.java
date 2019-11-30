package cn.com.jgyhw.user.vo;

import cn.com.jgyhw.user.entity.WxUser;
import lombok.Data;

/**
 * 微信用户Vo对象
 *
 * Created by WangLei on 2019/11/21 0021 19:34
 */
@Data
public class WxUserVo extends WxUser {

	/**
	 * 用户性别汉字，1：男性，2：女性，0：未知
	 */
	private String sexName;
}
