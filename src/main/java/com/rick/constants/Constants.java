package com.rick.constants;

/**
 * 通用常量信息
 *
 * @author whty
 */
public class Constants {
    /**
     * UTF-8 字符集
     */
    public static final String UTF8 = "UTF-8";

    /**
     * GBK 字符集
     */
    public static final String GBK = "GBK";

    /**
     * http请求
     */
    public static final String HTTP = "http://";

    /**
     * https请求
     */
    public static final String HTTPS = "https://";

    /**
     * 通用成功标识
     */
    public static final String SUCCESS = "0";

    /**
     * 通用失败标识
     */
    public static final String FAIL = "1";

    /**
     * 登录成功
     */
    public static final String LOGIN_SUCCESS = "Success";

    /**
     * 注销
     */
    public static final String LOGOUT = "Logout";

    /**
     * 注册
     */
    public static final String REGISTER = "Register";

    /**
     * 登录失败
     */
    public static final String LOGIN_FAIL = "Error";

    public static final String TOKEN = "token";

    public static final int ROLE_ADMIN = 1;
    public static final int ROLE_NORMAL = 0;

    /**
     * 校验返回结果码
     */
    public static final String UNIQUE = "0";
    public static final String NOT_UNIQUE = "1";

    /**
     * 用户名长度限制
     */
    public static final int USERNAME_MIN_LENGTH = 2;
    public static final int USERNAME_MAX_LENGTH = 20;

    /**
     * 密码长度限制
     */
    public static final int PASSWORD_MIN_LENGTH = 5;
    public static final int PASSWORD_MAX_LENGTH = 20;

    //启用
    public static final String STATUS_ON = "0";
    //停用
    public static final String STATUS_DISABLED = "1";
    //删除
    public static final String STATUS_OFF = "3";
    //系统删除
    public static final String STATUS_SYS_OFF = "4";
    //暂存
    public static final String STATUS_TEMP = "5";


}