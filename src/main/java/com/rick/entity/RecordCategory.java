package com.rick.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @description t_record_category
 * @author rick
 * @date 2022-07-07
 */
@Data
public class RecordCategory implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    /**
    * id
    */
    private Integer id;

    /**
    * 记录分类名称
    */
    private String title;

    /**
    * 描述
    */
    private String categoryDesc;

    /**
    * 地点
    */
    private String locale;

    /**
    * 地图点位
    */
    private String mapPoint;

    /**
    * 开始时间
    */
    private Date startDate;

    /**
    * 结束时间
    */
    private Date endDate;

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

    private String isPrivate;
    public RecordCategory() {}
}