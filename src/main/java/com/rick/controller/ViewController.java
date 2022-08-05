package com.rick.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.rick.base.controller.BaseController;
import com.rick.constants.Constants;
import com.rick.domain.R;
import com.rick.entity.View;
import com.rick.service.IViewService;
import com.rick.utils.DateUtils;
import com.rick.utils.StringUtils;
import com.rick.utils.redis.RedisService;
import com.rick.utils.redis.RedisUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
@RequestMapping("/v")
@RequiredArgsConstructor
public class ViewController extends BaseController {

    public final IViewService viewService;

    public final RedisService redisService;

    //点赞, 取消点赞
    @PostMapping("/like")
    public R<Void> operateLike(@RequestBody View view) {
        Integer userId = getUserId();
        //1.记录操作人和记录到redis sort set
        Object userRecord = redisService.hget(StringUtils.format(Constants.LIKE_RECORD_AND_RANK, view.getTopic(), view.getRecordId()), userId + "");
        //已存在
        Integer score = 1;
        JSONObject info = null;
        String currentDate = DateUtils.dateTimeNow(DateUtils.YYYY_MM_DD_HH_MM_SS);
        //先从redis中读取此人是否点赞过该记录
        if (userRecord != null) {
            info = JSONObject.parseObject((String) userRecord);
            String status = info.getString("status");
            if (status.equals(Constants.STATUS_ON)) {//如果缓存中的数据为已点赞 调整redis中的数据状态
                score = -1;
            }
        } else {//从数据库中读取 此人是否点赞过该记录
            info = new JSONObject();
            View one = viewService.getOne(
                    new LambdaQueryWrapper<View>()
                            .eq(View::getTopic, view.getTopic())
                            .eq(View::getRecordId,view.getRecordId())
                            .eq(View::getFromUserId, userId)
            );
            //已点赞 , 进行取消 减一分;  如果没有进行过点赞操作 按加分处理
            if (one != null && one.getStatus().equals(Constants.STATUS_ON)) {
                score = -1;
            }else if(one == null){
                info.put("createTime",currentDate);
            }
            info.put("fromUserId", userId);
            info.put("recordId", view.getRecordId());
            info.put("topic", view.getTopic());
            info.put("createBy", -1);
            info.put("updateBy", -1);
        }
        info.put("status", score == -1 ? 1 : 0);//如果已点赞 保存一条取消的数据到redis中
        info.put("updateTime", currentDate);
        //新增或修改 记录的用户信息
        redisService.sadd(StringUtils.format(Constants.LIKE_RECORD_USER, view.getTopic(), view.getRecordId()), userId);//用户操作记录 , 方便后续批量扫入数据库, 根据此遍历LIKE_RECORD_AND_RANK 操作人详情
        redisService.hset(StringUtils.format(Constants.LIKE_RECORD_AND_RANK, view.getTopic(), view.getRecordId()), userId + "", info);
        //2.对redis中记录的点赞数量计算 TODO: 系统启动时会把点赞数据同步到Redis中
        redisService.hIncrBy(StringUtils.format(Constants.LIKE_RECORD_COUNTER, view.getRecordId()), view.getTopic(), score);
        //3.异步线程同步点赞人和点赞数量到库中
        return R.ok();
    }
}
