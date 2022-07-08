package com.rick.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rick.entity.RecordCategory;
import com.rick.mapper.RecordCategoryMapper;
import com.rick.service.IRecordCategoryService;
import org.springframework.stereotype.Service;

@Service
public class RecordCategoryServiceImpl extends ServiceImpl<RecordCategoryMapper,RecordCategory> implements IRecordCategoryService {
}
