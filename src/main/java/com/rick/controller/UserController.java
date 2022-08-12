package com.rick.controller;

import cn.dev33.satoken.secure.BCrypt;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.rick.base.controller.BaseController;
import com.rick.domain.R;
import com.rick.domain.WebUserDTO;
import com.rick.domain.request.SetPasswordReq;
import com.rick.entity.User;
import com.rick.exception.UserException;
import com.rick.framework.satoken.LoginHelper;
import com.rick.service.IUserService;
import com.rick.utils.IdToNameUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.function.Function;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController extends BaseController {

    public final IUserService userService;

    @PostMapping("/get/{id}")
    public R<WebUserDTO> get(@PathVariable("id") Integer userId){
        return R.ok(BeanUtil.toBean(userService.getById(userId),WebUserDTO.class));
    }

    @PostMapping("/set")
    public R<Void> set(@RequestBody User user){
        IdToNameUtil.modifyUserName(user.getId(),BeanUtil.toBean(user,WebUserDTO.class));
        return toAjax(userService.updateById(user));
    }

    @PostMapping("/setPassword")
    public R<Void> setPassword(@RequestBody SetPasswordReq req){
        User loginUser = getLoginUser();
        return toAjax(userService.setPassword(loginUser.getId(),loginUser.getUsername(),req.getCurrentPassword(),req.getPassword()));
    }
}
