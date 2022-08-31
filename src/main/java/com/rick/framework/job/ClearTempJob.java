package com.rick.framework.job;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import cn.hutool.http.Method;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.rick.constants.Constants;
import com.rick.entity.TmpImg;
import com.rick.framework.config.RepositoryConfig;
import com.rick.service.ITmpImgService;
import com.rick.utils.DateUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@MyJob
@Slf4j
public class ClearTempJob  implements BaseJob{

    public final ITmpImgService tmpImgService;

    private final RepositoryConfig config;

    public Map<String, String> headers = null;

    public synchronized Map<String, String> getRepositoryHeaders() {
        if (headers == null) {
            headers = new HashMap<>();
            headers.put("Accept", "application/vnd.github.v3+json");
            headers.put("Authorization", "token " + config.getAccessToken());
        }
        return headers;
    }

    @Override
    public void run() {
        log.info("start do {} !", this.getClass().getSimpleName());
        Calendar instance = Calendar.getInstance();
        instance.setTime(DateUtils.getNowDate());
        instance.add(Calendar.DAY_OF_WEEK,-1);
        List<TmpImg> list = tmpImgService.list(new LambdaQueryWrapper<TmpImg>().eq(TmpImg::getStatus, Constants.STATUS_TEMP).le(TmpImg::getCreateTime, instance.getTime()));
        try{
            for (int i = 0; i < list.size(); i++) {
                clearRepositoryFile(list.get(i));
            }
        }catch (Exception e){
            e.printStackTrace();
            log.info("do clear error:",e.getMessage());
        }
        log.info("end do {} !", this.getClass().getSimpleName());
    }


    public void clearRepositoryFile(TmpImg img){
        JSONObject params = new JSONObject();
        params.put("message", "remove code");
        params.put("sha", img.getSha());
        HttpRequest request = HttpUtil.createRequest(Method.DELETE, img.getUrl());
        if (config.isGithub()) {
            request.addHeaders(getRepositoryHeaders());
        } else {
            params.put("access_token", config.getAccessToken());
        }
        String body = request.body(params.toJSONString()).execute().body();
        JSONObject jsonObject = JSONObject.parseObject(body);
        String remark = null;
        String status = Constants.STATUS_SYS_OFF;
        //如果没有commit信息 ， 表明文件删除失败
        if(!jsonObject.containsKey("commit")){
            remark = "remove file failed";
            status = Constants.STATUS_DISABLED;
        }
        tmpImgService.update(new LambdaUpdateWrapper<TmpImg>()
                .set(TmpImg::getStatus,status)
                .set(remark != null ,TmpImg::getRemark,remark)
                .eq(TmpImg::getSha, img.getSha()));
    }

}
