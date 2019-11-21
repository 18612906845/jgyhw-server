/**
 * Copyright (c) 2018-2028, Chill Zhuang 庄骞 (smallchill@163.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springblade.common.tool;

import java.text.DecimalFormat;

/**
 * 通用工具类
 *
 * @author Chill
 */
public class CommonUtil {

	/**
	 * 计算联盟返利
	 *
	 * @param commision 佣金
	 * @param commisionRatio 返利比例
	 * @return
	 */
	public static Double rebateCompute(Double commision, Double commisionRatio){
		if(commision == null || commisionRatio == null){
			return 0.0;
		}
		Double rebateDouble = commision * (commisionRatio / 100.00);
		//保留2位小数
		DecimalFormat df = new DecimalFormat("#.00");
		Double rebate = Double.valueOf(df.format(rebateDouble));
		return rebate;
	}

	/**
	 * 计算用户返利
	 *
	 * @param unitPrice 商品单价
	 * @param commisionRatio 佣金比例
	 * @param rebateScale 返利比例
	 * @return
	 */
	public static Double rebateCompute(Double unitPrice, Double commisionRatio, Integer rebateScale){
		if(unitPrice == null || commisionRatio == null || rebateScale == null){
			return 0.0;
		}
		Double rebateDouble = (commisionRatio / 100.00) * unitPrice * (rebateScale / 100.00);
		//保留2位小数
		DecimalFormat df = new DecimalFormat("#.00");
		Double rebate = Double.valueOf(df.format(rebateDouble));
		return rebate;
	}

	/**
	 * 格式化双精度数据
	 *
	 * @param source 元数据
	 * @return
	 */
	public static Double formatDouble(Double source){
		if(source == null){
			source = 0.00;
		}
		//保留2位小数
		DecimalFormat df = new DecimalFormat("#.00");
		return Double.valueOf(df.format(source));
	}

}
