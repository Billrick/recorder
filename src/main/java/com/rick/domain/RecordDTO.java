package com.rick.domain;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.rick.entity.Img;
import com.rick.entity.User;
import com.rick.entity.View;
import com.rick.entity.ViewCount;
import com.rick.utils.IdToNameUtil;
import com.rick.utils.StringUtils;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @description t_record
 * @author rick
 * @date 2022-07-07
 */
@Data
public class RecordDTO implements Serializable {

    private static final long serialVersionUID = 1L;

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

    private String isPrivate;

    private WebUserDTO user;

    private List<Img> imgs;

    private List<String> removeIds;

    private ViewCount viewCount;

    private String categoryTitle;

    public String getCategoryTitle() {
        if(this.getCategoryId() != null && StringUtils.isEmpty(this.categoryTitle)){
            this.categoryTitle = IdToNameUtil.getRecordCategoryTitle(this.getCategoryId());
        }
        return categoryTitle;
    }

    public List<CommentDTO> comments;

    public List<Long> categoryIds;

    private String orderField;

    private Integer current;

    private Integer pageSize = 8;

    public RecordDTO() {}
}