package org.springblade.common.constant;

/**
 * 京东通用参数常量
 *
 * Created by WangLei on 2019/11/24 0024 10:41
 */
public interface JdParamConstant {

	/**
	 * 京东推广网站ID
	 */
	String JD_WEB_ID = "1537716146";

	/**
	 * 京东联盟ID
	 */
	String JD_UNION_ID = "1000455975";

	/**
	 * 京东推广Key
	 */
	String APP_KEY = "9ff8aec6cc17485c82523851a18d48bd";

	/**
	 * 京东推广密匙
	 */
	String APP_SECRET = "5978bfdc4d0c47cb8e03420cc7cdf7da";

	/**
	 * 京东推广API服务地址
	 */
	String API_SERVER_URL = "https://router.jd.com/api";

	/**
	 * 京东商品Redis Key前缀
	 */
	String JD_GOODS_KEY_PREFIX = "jdGoodsKey:";

	/**
	 * 第三方接口地址
	 */
	String JD_TRANSITION_CPS_URL_OTHER = "https://jd.vip.apith.cn/unionv2/getByUnionidPromotion?materialId=MATERIAL_ID&unionId=UNION_ID&positionId=POSITION_ID";

	/**
	 * 第三方接口权限ID
	 */
	String APITH_SECRET_ID = "AKIDmyhsw3cTB6fSV9lrY461L1xh60NHQM7p80JI";

	/**
	 * 第三方接口权限Key
	 */
	String APITH_SECRET_KEY = "ibvdorgx4lD5oddzkz8cbCDs4w5a1d4ceW6iyc72";

	/**
	 * 推广链接类型-小程序
	 */
	String SPS_TYPE_GZH = "gzh";

	/**
	 * 推广链接类型-小程序
	 */
	String SPS_TYPE_XCX = "xcx";
}
