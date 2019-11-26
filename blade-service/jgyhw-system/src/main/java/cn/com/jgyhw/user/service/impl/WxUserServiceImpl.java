package cn.com.jgyhw.user.service.impl;


import cn.com.jgyhw.user.entity.WxUser;
import cn.com.jgyhw.user.mapper.WxUserMapper;
import cn.com.jgyhw.user.service.IWxUserService;
import org.springblade.core.mp.base.BaseServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 微信用户服务实现类
 *
 * Created by WangLei on 2019/11/19 0019 19:26
 */
@Service
public class WxUserServiceImpl extends BaseServiceImpl<WxUserMapper, WxUser> implements IWxUserService {

}
