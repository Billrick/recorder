package com.rick.controller;

import com.rick.base.controller.BaseController;
import com.rick.domain.R;
import com.rick.domain.RecordDTO;
import com.rick.domain.SquareDTO;
import com.rick.service.IPublicService;
import com.rick.service.IRecordService;
import com.rick.utils.redis.RedissonUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/public")
@RequiredArgsConstructor
@Slf4j
public class PublicController extends BaseController {

    public final RedissonUtils redissonUtils;

    public final IPublicService publicService;

    @RequestMapping("/get")
    public R<Void> get(String key){
        RLock lock = redissonUtils.lock(key);
        boolean b = false;
        try{
            b = lock.tryLock(10, 20, TimeUnit.SECONDS);
            if(b){
                log.info("thread {} - {} get lock",Thread.currentThread().getName(),Thread.currentThread().getId());
                Thread.sleep(30000);
            }else{
                log.info("thread {} - {} give up lock",Thread.currentThread().getName(),Thread.currentThread().getId());
                return R.ok("数据已被锁定");
            }
        } catch (Exception e){
            log.error("thread get lock error");
        } finally{
            if (lock.isLocked() && b) {
                log.info("thread {} - {} unlock",Thread.currentThread().getName(),Thread.currentThread().getId());
                lock.unlock();
            }
        }
        return R.ok();
    }

    @PostMapping("/square")
    public R<SquareDTO> square(@RequestBody RecordDTO recordDTO) {
        return R.ok(publicService.square(recordDTO));
    }

}
