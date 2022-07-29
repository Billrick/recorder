package com.rick.controller;

import com.rick.base.controller.BaseController;
import com.rick.domain.R;
import com.rick.domain.page.TableDataInfo;
import com.rick.entity.Record;
import com.rick.entity.RecordCategory;
import com.rick.service.IRecordCategoryService;
import com.rick.service.IRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/record")
@RequiredArgsConstructor
public class RecordController extends BaseController {

    public final IRecordService recordService;

    @PostMapping("/save")
    public R<Void> save(@Validated @RequestBody Record record){

        return toAjax(recordService.saveRecordAndImg(record));
    }

    @PostMapping("/delete")
    public R<Void> delete(Integer id){
        return toAjax(recordService.deleteRecordAndImg(id));
    }

    @PostMapping("/list")
    public TableDataInfo list(@RequestBody(required = false) Record record){
        startPage();
        List<Record> list = recordService.getList(record);
        return getDataTable(list);
    }
}
