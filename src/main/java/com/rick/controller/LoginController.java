package com.rick.controller;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import com.rick.base.controller.BaseController;
import com.rick.constants.Constants;
import com.rick.domain.LoginBody;
import com.rick.domain.R;
import com.rick.entity.User;
import com.rick.framework.satoken.LoginHelper;
import com.rick.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class LoginController extends BaseController {

    public final IUserService userService;

    @PostMapping("/login")
    public R<SaTokenInfo> login(@Validated @RequestBody LoginBody loginBody) {
        SaTokenInfo token = userService.login(loginBody.getUsername(), loginBody.getPassword(), loginBody.getCode(), loginBody.getUuid());
        return R.ok(token);
    }

    @GetMapping("/getUserInfo")
    public R<User> getUserInfo(){
        return R.ok(LoginHelper.getLoginUser());
    }

    @PostMapping("/logout")
    public R<Void> logout() {
        try {
            StpUtil.logout();
        } catch (NotLoginException e) {
        }
        return R.ok("退出成功");
    }
}
