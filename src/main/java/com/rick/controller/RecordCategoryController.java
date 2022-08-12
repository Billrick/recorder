package com.rick.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.rick.base.controller.BaseController;
import com.rick.domain.R;
import com.rick.domain.page.TableDataInfo;
import com.rick.entity.RecordCategory;
import com.rick.service.IRecordCategoryService;
import com.rick.utils.IdToNameUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/category")
@RequiredArgsConstructor
public class RecordCategoryController extends BaseController {

    public final IRecordCategoryService recordCategoryService;


    @PostMapping("/save")
    public R<Void> save(@Validated @RequestBody RecordCategory recordCategory){
        boolean b = recordCategoryService.save(recordCategory);
        if(b){IdToNameUtil.modifyRecordCategory(recordCategory.getId(),recordCategory.getTitle());};
        return toAjax(b);
    }

    @PostMapping("/update")
    public R<Void> update(@Validated @RequestBody RecordCategory recordCategory){
        boolean b = recordCategoryService.updateById(recordCategory);
        if(b){IdToNameUtil.modifyRecordCategory(recordCategory.getId(),recordCategory.getTitle());};
        return toAjax(b);
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

    @PostMapping("/dict")
    public R<List> categoryDict(){
        return R.ok(recordCategoryService.listMaps(new QueryWrapper<RecordCategory>().select("id,title").eq("create_by",getLoginUser().getId())));
    }
}
