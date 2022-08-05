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
 * @description t_view
 * @author rick
 * @date 2022-07-07
 */
@Data
public class View implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    /**
    * id
    */
    private Long id;

    /**
    * 话题类型
    */
    private String topic;

    /**
    * 记录id
    */
    private Long recordId;

    /**
    * 操作用户id
    */
    private Integer fromUserId;

    /**
    * 状态
    */
    private String status;

    /**
     * create_time
     */
    private Date createTime;

    /**
     * create_by
     */
    private Integer createBy;

    /**
     * update_time
     */
    private Date updateTime;

    /**
     * update_by
     */
    private Integer updateBy;

    public View() {}
}