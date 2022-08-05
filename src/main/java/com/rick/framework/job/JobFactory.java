package com.rick.framework.job;

import com.rick.utils.SpringUtils;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;

@Component
public class JobFactory {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final ConcurrentMap<String, ScheduledFuture> timingTaskMap = new ConcurrentHashMap<>();
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    /**
     * 创建一个定时任务
     *
     * @param taskName     任务名称
     * @param initialDelay 延时开始
     * @param period       执行周期
     * @param command      任务线程
     * @param mandatory    (存在)强制创建
     */
    public static boolean CreateTimingTask(String taskName, long initialDelay,
                                           long period, Runnable command, boolean mandatory) {
        if (FindTimingTask(taskName)) {
            System.out.println("定时任务存在。");
            if (mandatory) {
                System.out.println("强制创建");
                CancelTimingTask(taskName);
                ScheduledFuture future = scheduler.scheduleAtFixedRate(command, initialDelay, period, TimeUnit.MINUTES);
                timingTaskMap.put(taskName, future);
                return  true;
            } else {
                return false;
            }
        }
        ScheduledFuture future = scheduler.scheduleAtFixedRate(command, initialDelay, period, TimeUnit.MINUTES);
        timingTaskMap.put(taskName, future);
        return true;
    }

    /**
     * 创建一个一次性的延迟任务
     *
     * @param initialDelay 延时开始
     * @param command      任务线程
     */
    public static void CreateTask(String name, long initialDelay, Runnable command) {
        try {
            scheduler.schedule(command, initialDelay, TimeUnit.SECONDS);
        } catch (Exception e) {
            System.out.println("任务执行失败");
        }
    }

    /**
     * 根据名称查询定时任务
     *
     * @param taskName 任务名称
     * @return true任务存在  false任务取消
     */
    public static boolean FindTimingTask(String taskName) {

        try {
            ScheduledFuture scheduledFuture = timingTaskMap.get(taskName);
            if (scheduledFuture != null) {
                return true;
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    /**
     * 查询定时任务列表
     *
     * @return 任务名称集合
     */
    public static Set<String> FindListTimingTask() {
        return timingTaskMap.keySet();
    }

    /**
     * 根据名称取消定时任务
     *
     * @param taskName 任务名称
     */
    public static boolean CancelTimingTask(String taskName) {
        try {
            if (FindTimingTask(taskName)) {
                boolean b = timingTaskMap.get(taskName).cancel(true);
                timingTaskMap.remove(taskName);
                return b;
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }


    @PostConstruct
    public void initJob(){
        logger.info("start init job factory!");
        Map<String, Object> beans = SpringUtils.getApplicationContext().getBeansWithAnnotation(MyJob.class);
        for(String key: beans.keySet()){
            Object o = beans.get(key);
            if(o instanceof BaseJob){
                JobFactory.CreateTimingTask(key,0,5, (BaseJob)o,false);
                logger.info("create job {} success!",key);
            }
        }
        logger.info("init job factory success!");
    }
}
