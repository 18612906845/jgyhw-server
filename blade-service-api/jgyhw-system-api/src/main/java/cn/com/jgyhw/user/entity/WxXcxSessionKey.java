package cn.com.jgyhw.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springblade.core.mp.base.BaseEntity;

/**
 * 微信小程序会话密匙
 *
 * Created by WangLei on 2019/11/29 0029 22:49
 */
@Data
@TableName("jgyhw_wx_xcx_session_key")
public class WxXcxSessionKey extends BaseEntity {

	/**
	 * 主键id
	 */
	@JsonSerialize(using = ToStringSerializer.class)
	@TableId(value = "id", type = IdType.ID_WORKER)
	@ApiModelProperty(value = "主键id")
	private Long id;

	/**
	 * 微信小程序标识
	 */
	private String openId;

	/**
	 * 微信会话密钥
	 */
	private String sessionKey;
}
