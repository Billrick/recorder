package com.rick.service;

import cn.dev33.satoken.stp.SaTokenInfo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.rick.entity.User;

public interface IUserService extends IService<User> {

    SaTokenInfo login(String username, String password, String code, String uuid);
}
