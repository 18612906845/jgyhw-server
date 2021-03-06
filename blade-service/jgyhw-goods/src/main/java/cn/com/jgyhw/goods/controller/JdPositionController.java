package cn.com.jgyhw.goods.controller;

import cn.com.jgyhw.goods.entity.JdPosition;
import cn.com.jgyhw.goods.service.IJdPositionService;
import lombok.extern.slf4j.Slf4j;
import org.springblade.core.tool.api.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 京东推广位控制器
 *
 * Created by WangLei on 2019/11/24 0024 23:59
 */
@Slf4j
@RestController
@RequestMapping("/jdPosition")
public class JdPositionController {

	@Autowired
	private IJdPositionService jdPositionService;

	/**
	 * 根据京东推广位ID查询京东推广位
	 *
	 * @param positionId 推广位ID
	 * @return
	 */
	@GetMapping("/findJdPositionByPositionId")
	public R<JdPosition> findJdPositionByPositionId(Long positionId){
		if(positionId == null){
			return R.data(null);
		}
		JdPosition jp = jdPositionService.getById(positionId);
		return R.data(jp);
	}
}
