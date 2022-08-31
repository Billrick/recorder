package com.rick.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.rick.domain.RecordDTO;
import com.rick.domain.page.TableDataInfo;
import com.rick.entity.Record;
import com.rick.entity.ViewCount;

import java.util.List;

public interface IRecordService extends IService<Record> {

    boolean saveRecordAndImg(RecordDTO record);

    boolean deleteRecordAndImg(Integer id);

    TableDataInfo getList(RecordDTO record, Boolean isPublic);

    ViewCount getViewInfo(String id,Boolean notJob);
}
