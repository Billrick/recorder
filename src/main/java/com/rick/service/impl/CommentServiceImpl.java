package com.rick.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rick.entity.Comment;
import com.rick.mapper.CommentMapper;
import com.rick.service.ICommentService;
import org.springframework.stereotype.Service;

@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper,Comment> implements ICommentService {
}
