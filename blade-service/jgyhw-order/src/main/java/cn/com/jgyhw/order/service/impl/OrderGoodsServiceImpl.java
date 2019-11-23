package cn.com.jgyhw.order.service.impl;

import cn.com.jgyhw.order.entity.OrderGoods;
import cn.com.jgyhw.order.mapper.OrderGoodsMapper;
import cn.com.jgyhw.order.service.IOrderGoodsService;
import lombok.extern.slf4j.Slf4j;
import org.springblade.core.mp.base.BaseServiceImpl;
import org.springframework.stereotype.Service;

/**
 * Created by WangLei on 2019/11/23 0023 18:52
 */
@Slf4j
@Service
public class OrderGoodsServiceImpl extends BaseServiceImpl<OrderGoodsMapper, OrderGoods> implements IOrderGoodsService {
}
