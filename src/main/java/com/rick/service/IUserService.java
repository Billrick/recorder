package com.rick.service;

import cn.dev33.satoken.stp.SaTokenInfo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.rick.domain.WebUserDTO;
import com.rick.entity.User;

import java.util.List;

public interface IUserService extends IService<User> {

    SaTokenInfo login(String username, String password, String code, String uuid);

    List<WebUserDTO> userInfoList(QueryWrapper queryWrapper);


    Boolean setPassword(Integer id,String username,String curPassword,String password);
}
