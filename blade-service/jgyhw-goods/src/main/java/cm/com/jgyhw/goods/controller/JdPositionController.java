package cm.com.jgyhw.goods.controller;

import cm.com.jgyhw.goods.service.IJdPositionService;
import cn.com.jgyhw.goods.entity.JdPosition;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiOperationSupport;
import io.swagger.annotations.ApiParam;
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
@Api(value = "京东推广位", tags = "京东推广位")
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
	@ApiOperationSupport(order = 1)
	@ApiOperation(value = "根据京东推广位ID查询京东推广位", notes = "")
	public R<JdPosition> findJdPositionByPositionId(@ApiParam(value = "推广位ID", required = true) Long positionId){
		if(positionId == null){
			return R.data(null);
		}
		JdPosition jp = jdPositionService.getById(positionId);
		return R.data(jp);
	}
}
