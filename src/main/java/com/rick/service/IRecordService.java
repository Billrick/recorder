package com.rick.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.rick.entity.Record;

import java.util.List;

public interface IRecordService extends IService<Record> {

    boolean saveRecordAndImg(Record record);

    boolean deleteRecordAndImg(Integer id);

    List<Record> getList(Record record);
}
