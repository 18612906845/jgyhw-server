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

import io.micrometer.core.instrument.util.StringUtils;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 通用工具类
 *
 * @author Chill
 */
public class CommonUtil {

	protected static final String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";

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
	 * 计算用户返利
	 *
	 * @param unitPrice 商品单价
	 * @param rebateScale 返利比例
	 * @return
	 */
	public static Double rebateCompute(Double unitPrice, Integer rebateScale){
		if(unitPrice == null || rebateScale == null){
			return 0.0;
		}
		Double rebateDouble = unitPrice * (rebateScale / 100.00);
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

	/**
	 * 日期格式化成字符串
	 *
	 * @param date 日期
	 * @param format 格式
	 * @return
	 */
	public static String dateToStringByFormat(Date date, String format){
		String dateStr = "";
		if(date == null){
			return dateStr;
		}
		if(StringUtils.isBlank(format)){
			format = YYYY_MM_DD_HH_MM_SS;
		}
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		dateStr = sdf.format(date);
		return dateStr;
	}

	/**
	 * 获取随机字符串
	 * @return
	 */
	public static String getRandomString() {
		int machineId = 1;//最大支持1-9个集群机器部署
		int hashCodeV = UUID.randomUUID().toString().hashCode();
		if(hashCodeV < 0) {//有可能是负数
			hashCodeV = - hashCodeV;
		}
		String randomStr = machineId + String.format("%05d", hashCodeV);
		String timeStr = String.valueOf(System.currentTimeMillis());
		timeStr = timeStr.substring(3, timeStr.length());
		randomStr += timeStr;
		return randomStr;
	}

	/**
	 * 获取商户订单号
	 * @return
	 */
	public static String getShopOrderString() {
		int machineId = 1;//最大支持1-9个集群机器部署
		int hashCodeV = UUID.randomUUID().toString().hashCode();
		if(hashCodeV < 0) {//有可能是负数
			hashCodeV = - hashCodeV;
		}
		String randomStr = "JGYHWDH" + machineId + String.format("%05d", hashCodeV);
		String timeStr = String.valueOf(System.currentTimeMillis());
		timeStr = timeStr.substring(3, timeStr.length());
		randomStr += timeStr;
		return randomStr;
	}

}
