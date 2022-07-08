package com.rick.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.util.Date;
import java.util.List;
import java.io.Serializable;

/**
 * @description t_comment
 * @author rick
 * @date 2022-07-07
 */
@Data
public class Comment implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    /**
    * id
    */
    private Long id;

    /**
    * 评论主体
    */
    private String content;

    /**
    * @
    */
    private String at;

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

    /**
    * 父评论id
    */
    private Long commentId;

    /**
    * 记录id
    */
    private Long recordId;

    public Comment() {}
}