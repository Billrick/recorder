package com.rick.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import java.util.Date;
import java.util.List;
import java.io.Serializable;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
/**
 * @description t_record
 * @author rick
 * @date 2022-07-07
 */
@Data
public class Record implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    /**
    * id
    */
    private Long id;

    /**
    * 记录描述
    */
    private String recordDesc;

    /**
    * 状态
    */
    private String status;

    /**
    * category_id
    */
    private Integer categoryId;

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

    private String isPrivate;

    @TableField(exist = false)
    private User user;

    @TableField(exist = false)
    private List<Img> imgs;

    public Record() {}
}