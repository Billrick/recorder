package com.rick.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.rick.domain.WebUserDTO;
import com.rick.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UserMapper extends BaseMapper<User> {

    @Select("select ${ew.sqlSelect} from t_user ${ew.customSqlSegment}")
    public List<WebUserDTO> userInfoList(@Param("ew")QueryWrapper queryWrapper);
}
