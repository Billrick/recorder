package com.rick.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @description t_user
 * @author rick
 * @date 2022-07-07
 */
@Data
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    /**
    * id
    */
    private Integer id;

    /**
    * 昵称
    */
    private String nickName;

    /**
    * 用户名
    */
    private String username;

    /**
    * 密码
    */
    private String password;

    /**
    * 手机号
    */
    private String phone;

    /**
    * 性别
    */
    private String gender;

    /**
    * 状态
    */
    private String status;

    /**
     * create_time
     */
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    /**
     * create_by
     */
    @TableField(fill = FieldFill.INSERT)
    private Integer createBy;

    /**
     * update_time
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    /**
     * update_by
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Integer updateBy;

    private Integer role;

    private String locale;

    private String avatar;

    public User() {}
}