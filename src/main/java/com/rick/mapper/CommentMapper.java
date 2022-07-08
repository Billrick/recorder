package com.rick.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.rick.entity.Comment;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CommentMapper extends BaseMapper<Comment> {
}
