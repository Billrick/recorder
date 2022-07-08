package com.rick.entity;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.rick.controller.ImgController;
import com.rick.utils.StringUtils;
import lombok.Data;
import java.util.Date;
import java.util.List;
import java.io.Serializable;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
/**
 * @description t_img
 * @author rick
 * @date 2022-07-07
 */
@Data
public class Img implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    /**
    * id
    */
    private Long id;

    /**
    * 记录id
    */
    private Long recordId;

    /**
    * 图片地址
    */
    private String imgUrl;

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

    private String sha;

    @TableField(exist = false)
    private String imgData;

    public String getImgData() {
        if(StringUtils.isEmpty(this.imgData)){
            String res = HttpRequest.get(this.imgUrl).headerMap(ImgController.headers,true).execute().body();
            JSONObject jsonObject = JSONObject.parseObject(res);
            this.imgData = jsonObject.getString("content");
        }
        return imgData;
    }

    public Img() {}
}