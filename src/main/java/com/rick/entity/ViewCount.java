package com.rick.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.util.Date;
import java.util.List;
import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

/**
 * @author rick
 * @description view_count
 * @date 2022-08-04
 */
@Data
public class ViewCount implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    /**
     * id
     */
    private Long id;

    /**
     * topic
     */
    private String topic;

    private Long recordId;

    /**
     * 点赞量
     */
    private Long likeIt = 0L;


    /**
     * 浏览量
     */
    private Long see = 0L;

    /**
     * 收藏量
     */
    private Long collection = 0L;



    /**
     * create_time
     */
    private Date createTime;

    /**
     * sync_time
     */
    private Date syncTime;

    @TableField(exist = false)
    private Boolean iLikeIt;

    @TableField(exist = false)
    private Boolean iCollectionIt;

    @TableField(exist = false)
    private Long comment = 0L;

    public ViewCount() {
    }
}