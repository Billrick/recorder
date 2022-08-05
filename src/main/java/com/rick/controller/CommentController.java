package com.rick.controller;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.rick.base.controller.BaseController;
import com.rick.constants.Constants;
import com.rick.domain.CommentDTO;
import com.rick.domain.R;
import com.rick.domain.page.TableDataInfo;
import com.rick.entity.Comment;
import com.rick.service.ICommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/c")
@RequiredArgsConstructor
public class CommentController extends BaseController {

    public final ICommentService commentService;

    @PostMapping("/save")
    public R<Boolean> save(@RequestBody Comment comment){
        return R.ok(commentService.saveOrUpdate(comment));
    }

    @PostMapping("/del")
    public R<Boolean> del(Long id){
        return R.ok(commentService.update(new LambdaUpdateWrapper<Comment>().set(Comment::getStatus, Constants.STATUS_OFF).eq(Comment::getId,id)));
    }

    @PostMapping("/comments/{id}")
    public TableDataInfo comments(@PathVariable("id") Long id){
        return getDataTable(BeanUtil.copyToList(commentService.list(new LambdaQueryWrapper<Comment>().eq(Comment::getRecordId,id).eq(Comment::getStatus, Constants.STATUS_ON)),CommentDTO.class));
    }

}
