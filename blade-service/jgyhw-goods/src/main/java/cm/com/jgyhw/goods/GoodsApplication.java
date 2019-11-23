package cm.com.jgyhw.goods;

import org.springblade.common.constant.JgyhwConstant;
import org.springblade.core.launch.BladeApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 商品服务启动类
 *
 * Created by WangLei on 2019/11/21 0021 22:32
 */
@SpringBootApplication
public class GoodsApplication {

	public static void main(String[] args) {
		BladeApplication.run(JgyhwConstant.APPLICATION_ACCOUNT_NAME, GoodsApplication.class, args);
	}
}
