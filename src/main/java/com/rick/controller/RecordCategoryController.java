package com.rick.controller;

import com.rick.base.controller.BaseController;
import com.rick.domain.R;
import com.rick.domain.page.TableDataInfo;
import com.rick.entity.RecordCategory;
import com.rick.service.IRecordCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
        List<RecordCategory> list = recordCategoryService.list();
        return getDataTable(list);
    }
}
