package com.rick.framework.job;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.rick.constants.Constants;
import com.rick.entity.View;
import com.rick.entity.ViewCount;
import com.rick.service.IRecordService;
import com.rick.service.IViewCountService;
import com.rick.service.IViewService;
import com.rick.utils.ObjUtils;
import com.rick.utils.StringUtils;
import com.rick.utils.redis.RedisService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
@MyJob
public class ViewJob implements BaseJob {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    public final RedisService redisService;

    public final IViewService viewService;

    public final IViewCountService viewCountService;

    public final IRecordService recordService;

    @Override
    public void run() {
        logger.info("start do {} !", this.getClass().getSimpleName());
        //获取文章记录
        Set<String> keys = redisService.keys(StringUtils.format(Constants.LIKE_RECORD_USER, "*", "*"));
        Set<String> recordIds = new HashSet<>();
        //获取操作类型
        for (String key : keys) {
            String[] keyArr = key.split(":");
            String recordId = keyArr[4];
            String topic = keyArr[3];
            recordIds.add(recordId);
            Object member = null;
            //有过操作的userId
            while ((member = redisService.spop(key)) != null) {
                Object userInfo = redisService.hget(StringUtils.format(Constants.LIKE_RECORD_AND_RANK, topic, recordId), member);
                JSONObject userJson = JSONObject.parseObject((String) userInfo);
                View view = BeanUtil.toBean(userJson, View.class);
                saveView(view);
                redisService.hdel(StringUtils.format(Constants.LIKE_RECORD_AND_RANK, topic, recordId), member);
            }
        }
        //处理计数操作
        for(String id:recordIds){
            //计数获取
            ViewCount viewInfo = recordService.getViewInfo(id, false);
            saveViewCount(viewInfo);
        }
        logger.info("end do {} !", this.getClass().getSimpleName());
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveView(View view) {
        viewService.saveOrUpdate(view,
                new LambdaUpdateWrapper<View>()
                        .set(View::getCreateBy, -1)
                        .set(View::getUpdateBy, -1)
                        .eq(View::getFromUserId, view.getFromUserId())
                        .eq(View::getTopic, view.getTopic())
                        .eq(View::getRecordId, view.getRecordId()));
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveViewCount(ViewCount viewCount) {
        Boolean flag = viewCount.getId() != null;
        if (flag) {
            viewCountService.update(new LambdaUpdateWrapper<ViewCount>()
                    .eq(ViewCount::getTopic, viewCount.getTopic())
                    .eq(ViewCount::getRecordId, viewCount.getRecordId())
                    .set(ViewCount::getLikeIt, viewCount.getLikeIt())
                    .set(ViewCount::getCollection, viewCount.getCollection())
            );
        } else {
            viewCountService.save(viewCount);
        }
    }
}
