package com.rick.framework.satoken;

import cn.dev33.satoken.stp.StpUtil;
import com.rick.constants.Constants;
import com.rick.entity.User;
import com.rick.enums.DeviceType;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * 登录鉴权助手
 * 
 * deivce 为 设备类型 同一个用户类型 可以有 多种设备类型 例如 web,ios
 * 可以组成 用户类型与设备类型多对多的 权限灵活控制
 *
 * 多用户体系 针对 多种用户类型 但权限控制不一致
 * 可以组成 多用户类型表与多设备类型 分别控制权限
 *
 * @author Lion Li
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LoginHelper {

    public static final String JOIN_CODE = ":";
    public static final String LOGIN_USER_KEY = "loginUser";

    private static final ThreadLocal<User> LOGIN_CACHE = new ThreadLocal<>();

    /**
     * 登录系统
     *
     * @param loginUser 登录用户信息
     */
    public static void login(User loginUser) {
        LOGIN_CACHE.set(loginUser);
        StpUtil.login(loginUser.getId());
        setLoginUser(loginUser);
    }

    /**
     * 登录系统 基于 设备类型
     * 针对相同用户体系不同设备
     *
     * @param loginUser 登录用户信息
     */
    public static void loginByDevice(User loginUser, DeviceType deviceType) {
        LOGIN_CACHE.set(loginUser);
        StpUtil.login(loginUser.getId(), deviceType.getDevice());
        setLoginUser(loginUser);
    }

    /**
     * 设置用户数据(多级缓存)
     */
    public static void setLoginUser(User loginUser) {
        StpUtil.getTokenSession().set(LOGIN_USER_KEY, loginUser);
    }

    /**
     * 获取用户(多级缓存)
     */
    public static User getLoginUser() {
        User loginUser = LOGIN_CACHE.get();
        if (loginUser != null) {
            return loginUser;
        }
        return (User) StpUtil.getTokenSession().get(LOGIN_USER_KEY);
    }

    /**
     * 清除一级缓存 防止内存问题
     */
    public static void clearCache() {
        LOGIN_CACHE.remove();
    }

    /**
     * 获取用户id
     */
    public static Integer getUserId() {
        User loginUser = getLoginUser();
        if (loginUser != null) {
            return loginUser.getId();
        }
        return null;
    }


    /**
     * 获取用户账户
     */
    public static String getUsername() {
        return getLoginUser().getUsername();
    }

    /**
     * 获取用户昵称
     */
    public static String getNickName() {
        return getLoginUser().getNickName();
    }

    /**
     * 是否为管理员
     *
     * @param userId 用户ID
     * @return 结果
     */
    public static boolean isAdmin(Long userId) {
        return getLoginUser().getRole() == Constants.ROLE_ADMIN;
    }

}
