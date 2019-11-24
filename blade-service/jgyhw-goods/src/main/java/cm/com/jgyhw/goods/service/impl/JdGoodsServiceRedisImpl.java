package cm.com.jgyhw.goods.service.impl;

import cm.com.jgyhw.goods.service.IJdGoodsService;
import cn.com.jgyhw.goods.entity.JdGoods;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springblade.common.constant.JdParamConstant;
import org.springblade.common.constant.JgyhwConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * 京东商品信息实现类-Redis
 *
 * Created by WangLei on 2019/11/21 0021 23:04
 */
@Slf4j
@RefreshScope
@Service
public class JdGoodsServiceRedisImpl implements IJdGoodsService {

	@Value("${jgyhw.redis.jdGoodsPastDay:30}")
	private int jdGoodsPastDay;

	@Autowired
	private StringRedisTemplate stringRedisTemplate;

	/**
	 * 保存京东商品信息
	 *
	 * @param jdGoods 京东商品信息对象
	 * @return
	 */
	@Override
	public boolean saveJdGoods(JdGoods jdGoods) {
		if(jdGoods != null){
			stringRedisTemplate.opsForValue().set(JdParamConstant.JD_GOODS_KEY_PREFIX.concat(jdGoods.getGoodsId()), JSON.toJSONString(jdGoods),jdGoodsPastDay, TimeUnit.DAYS);
			log.info("京东商品信息保存Redis成功：" + jdGoods.toString());
			return true;
		}else{
			log.warn("京东商品信息保存Redis失败：" + jdGoods.toString());
			return false;
		}
	}

	/**
	 * 查询京东商品信息
	 *
	 * @param goodsId 商品编号
	 * @return
	 */
	@Override
	public JdGoods queryJdGoodsByGoodsId(String goodsId) {
		String jsonStr = stringRedisTemplate.opsForValue().get(JdParamConstant.JD_GOODS_KEY_PREFIX.concat(goodsId));
		if(StringUtils.isBlank(jsonStr)){
			return null;
		}else{
			JdGoods jdGoods = JSON.parseObject(jsonStr, JdGoods.class);
			return jdGoods;
		}
	}
}
