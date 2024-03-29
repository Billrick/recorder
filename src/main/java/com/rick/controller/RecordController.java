package com.rick.controller;

import com.rick.base.controller.BaseController;
import com.rick.domain.R;
import com.rick.domain.RecordDTO;
import com.rick.domain.page.TableDataInfo;
import com.rick.entity.Record;
import com.rick.entity.RecordCategory;
import com.rick.service.IRecordCategoryService;
import com.rick.service.IRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/record")
@RequiredArgsConstructor
public class RecordController extends BaseController {

    public final IRecordService recordService;

    @PostMapping("/save")
    public R<Void> save(@Validated @RequestBody RecordDTO record){
        return toAjax(recordService.saveRecordAndImg(record));
    }

    @PostMapping("/delete/{id}")
    public R<Void> delete(@PathVariable Integer id){
        return toAjax(recordService.deleteRecordAndImg(id));
    }

    @PostMapping("/list")
    public TableDataInfo list(@RequestBody(required = false) RecordDTO record) {
        return recordService.getList(record,false);
    }
}
