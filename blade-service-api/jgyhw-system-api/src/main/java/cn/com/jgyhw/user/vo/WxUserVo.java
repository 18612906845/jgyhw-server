package cn.com.jgyhw.user.vo;

import cn.com.jgyhw.user.entity.WxUser;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 微信用户Vo对象
 *
 * Created by WangLei on 2019/11/21 0021 19:34
 */
@Data
@ApiModel(value = "微信用户Vo对象", description = "微信用户Vo对象")
public class WxUserVo extends WxUser {

	/**
	 * 用户性别汉字，1：男性，2：女性，0：未知
	 */
	@ApiModelProperty(value = "用户性别汉字")
	private String sexName;

	/**
	 * 创建时间汉字
	 */
	@ApiModelProperty(value = "创建时间汉字")
	private String createTimeStr;

	/**
	 * 更新时间汉字
	 */
	@ApiModelProperty(value = "更新时间汉字")
	private String updateTimeStr;
}
