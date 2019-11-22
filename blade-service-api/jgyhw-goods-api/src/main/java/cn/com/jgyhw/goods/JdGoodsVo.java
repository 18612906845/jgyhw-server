package cn.com.jgyhw.goods;

import cn.com.jgyhw.goods.entity.JdGoods;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 京东商品Vo对象
 *
 * Created by WangLei on 2019/11/23 0023 00:19
 */
@Data
@ApiModel(value = "京东商品Vo对象", description = "京东商品Vo对象")
public class JdGoodsVo extends JdGoods {

	/**
	 * 推广连接
	 */
	@ApiModelProperty(value = "推广连接")
	private String cpsUrl;
}
