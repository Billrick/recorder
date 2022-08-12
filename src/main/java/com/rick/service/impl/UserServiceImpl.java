package com.rick.service.impl;

import cn.dev33.satoken.secure.BCrypt;
import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rick.constants.Constants;
import com.rick.domain.WebUserDTO;
import com.rick.entity.User;
import com.rick.enums.DeviceType;
import com.rick.exception.UserException;
import com.rick.framework.satoken.LoginHelper;
import com.rick.mapper.UserMapper;
import com.rick.service.IUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper,User> implements IUserService {

    @Override
    public SaTokenInfo login(String username, String password, String code, String uuid){
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<User>().eq(User::getUsername,username);
        User user = getOne(wrapper);
        if (ObjectUtil.isNull(user)) {
            log.info("登录用户：{} 不存在.", username);
            throw new UserException("user.not.exists", username);
        } else if (Constants.STATUS_OFF.equals(user.getStatus())) {
            log.info("登录用户：{} 已被删除.", username);
            throw new UserException("user.password.delete", username);
        } else if (Constants.STATUS_DISABLED.equals(user.getStatus())) {
            log.info("登录用户：{} 已被停用.", username);
            throw new UserException("user.blocked", username);
        }

        if(!BCrypt.checkpw(password, user.getPassword())){
            throw new UserException("user.password.not.match", username);
        }
        // 生成token
        LoginHelper.loginByDevice(user, DeviceType.PC);
        return StpUtil.getTokenInfo();
    }

    @Override
    public List<WebUserDTO> userInfoList(QueryWrapper queryWrapper) {
        return getBaseMapper().userInfoList(queryWrapper);
    }

    @Override
    public Boolean setPassword(Integer id, String username, String curPassword, String password) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<User>().eq(User::getId,id);
        User user = getOne(wrapper);
        if(!BCrypt.checkpw(curPassword, user.getPassword())){
            throw new UserException("user.password.not.match", username);
        }
        return update(new LambdaUpdateWrapper<User>().set(User::getPassword,BCrypt.hashpw(password)).eq(User::getId,id));
    }


}
