package com.rick.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.rick.entity.Record;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface RecordMapper extends BaseMapper<Record> {
}
