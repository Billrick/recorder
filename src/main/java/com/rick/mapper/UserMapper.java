package com.rick.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.rick.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
