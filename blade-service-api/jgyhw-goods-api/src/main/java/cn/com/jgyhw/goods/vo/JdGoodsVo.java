package cn.com.jgyhw.goods.vo;

import cn.com.jgyhw.goods.entity.JdGoods;
import lombok.Data;

/**
 * 京东商品Vo对象
 *
 * Created by WangLei on 2019/11/23 0023 00:19
 */
@Data
public class JdGoodsVo extends JdGoods {

	/**
	 * 推广连接
	 */
	private String cpsUrl;
}
