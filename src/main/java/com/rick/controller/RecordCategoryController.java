package com.rick.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.rick.base.controller.BaseController;
import com.rick.domain.R;
import com.rick.domain.page.TableDataInfo;
import com.rick.entity.RecordCategory;
import com.rick.service.IRecordCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/category")
@RequiredArgsConstructor
public class RecordCategoryController extends BaseController {

    public final IRecordCategoryService recordCategoryService;


    @PostMapping("/save")
    public R<Void> save(@Validated @RequestBody RecordCategory recordCategory){
        return toAjax(recordCategoryService.save(recordCategory));
    }

    @PostMapping("/update")
    public R<Void> update(@Validated @RequestBody RecordCategory recordCategory){
        return toAjax(recordCategoryService.updateById(recordCategory));
    }

    @PostMapping("/list")
    public TableDataInfo list(){
        startPage();
        List<RecordCategory> list = recordCategoryService.list(new LambdaQueryWrapper<RecordCategory>().eq(RecordCategory::getCreateBy,getLoginUser().getId()));
        return getDataTable(list);
    }

    @PostMapping("/remove/{id}")
    public R<Void> remove(@PathVariable Integer id){
        return toAjax(recordCategoryService.removeById(id));
    }
}
