package com.rick.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @description tmp_img
 * @author rick
 * @date 2022-08-01
 */
@Data
public class TmpImg implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
    * sha
    */
    @TableId
    private Long id;

    private String sha;

    /**
    * url
    */
    private String url;

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

    private String remark;

    public TmpImg() {}

    public TmpImg(String sha, String url, String status) {
        this.sha = sha;
        this.url = url;
        this.status = status;
    }

    public TmpImg(String sha, String url, String status,String remark) {
        this.sha = sha;
        this.url = url;
        this.status = status;
        this.remark = remark;
    }
}