package com.rick.utils;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.rick.domain.WebUserDTO;
import com.rick.entity.RecordCategory;
import com.rick.entity.User;
import com.rick.service.IRecordCategoryService;
import com.rick.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class IdToNameUtil {

    private static ConcurrentMap<Integer,WebUserDTO> userMap = new ConcurrentHashMap<>();
    private static ConcurrentMap<Integer,String> recordCategoryMap = new ConcurrentHashMap<>();

    public final IUserService userService;

    public final IRecordCategoryService recordCategoryService;

    @PostConstruct
    public void initData(){
        userMap = userService.userInfoList(new QueryWrapper<User>().select("id", "nick_name","avatar","locale")).stream().collect(Collectors.toConcurrentMap(WebUserDTO::getId, Function.identity()));
        recordCategoryMap = recordCategoryService.list(new QueryWrapper<RecordCategory>().select("id","title")).stream().collect(Collectors.toConcurrentMap(RecordCategory::getId, RecordCategory::getTitle));
    }

    public static void modifyUserName(Integer id,WebUserDTO user){
        if(user == null){
            return;
        }
        userMap.put(id,user);
    }

    public static void modifyRecordCategory(Integer id,String title){
        if(StringUtils.isEmpty(title)){
            return;
        }
        if(recordCategoryMap.containsKey(id) && recordCategoryMap.get(id).equals(title)){
            return;
        }
        recordCategoryMap.put(id,title);
    }


    public static WebUserDTO getUserInfo(Integer id){
        return userMap.get(id);
    }

    public static String getRecordCategoryTitle(Integer id){
        return recordCategoryMap.get(id);
    }

    public static Map getCategoryMap(){
        return recordCategoryMap;
    }

}
