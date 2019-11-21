package org.springblade.system.user.service.impl;


import org.springblade.core.mp.base.BaseServiceImpl;
import org.springblade.system.user.entity.WxUser;
import org.springblade.system.user.mapper.WxUserMapper;
import org.springblade.system.user.service.IWxUserService;
import org.springframework.stereotype.Service;

/**
 * 微信用户服务实现类
 *
 * Created by WangLei on 2019/11/19 0019 19:26
 */
@Service
public class WxUserServiceImpl extends BaseServiceImpl<WxUserMapper, WxUser> implements IWxUserService {

}
