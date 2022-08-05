package com.rick.domain;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.rick.entity.Comment;
import com.rick.framework.satoken.LoginHelper;
import com.rick.utils.IdToNameUtil;
import com.rick.utils.StringUtils;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @description t_comment
 * @author rick
 * @date 2022-07-07
 */
@Data
public class CommentDTO extends Comment implements Serializable {

    private static final long serialVersionUID = 1L;

    private String createByName;

    public String getCreateByName() {
        if(StringUtils.isEmpty(this.createByName) && this.getCreateBy() != null){
            String otherLabel = (StpUtil.isLogin()? this.getCreateBy().intValue() == LoginHelper.getUserId() ? "<span>（我）</span>":"":"");
            this.createByName = IdToNameUtil.getUserName(this.getCreateBy()) + otherLabel;
        }

        return createByName;
    }

    public CommentDTO() {}
}