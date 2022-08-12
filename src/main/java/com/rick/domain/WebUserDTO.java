package com.rick.domain;

import lombok.Data;

@Data
public class WebUserDTO {
    /**
     * id
     */
    private Integer id;

    /**
     * 昵称
     */
    private String nickName;
    /**
     * 所在地点
     */
    private String locale;
    /**
     * 头像
     */
    private String avatar;

    /**
     * 性别
     */
    private String gender;

    private String username;

    private String phone;

}
