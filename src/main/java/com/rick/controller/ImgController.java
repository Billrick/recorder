package com.rick.controller;

import cn.hutool.core.codec.Base64Encoder;
import cn.hutool.core.io.FileTypeUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import cn.hutool.http.Method;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.rick.base.controller.BaseController;
import com.rick.constants.Constants;
import com.rick.domain.R;
import com.rick.domain.request.DelFileReq;
import com.rick.entity.Img;
import com.rick.entity.TmpImg;
import com.rick.framework.config.RepositoryConfig;
import com.rick.service.IImgService;
import com.rick.service.ITmpImgService;
import com.rick.utils.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/f")
@RequiredArgsConstructor
public class ImgController extends BaseController {


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

    public final IImgService imgService;

    public final ITmpImgService tmpImgService;

    @PostMapping("/upload")
    public R<JSONObject> upload(Integer categoryId, MultipartFile file) throws IOException {
        //用户id + 分类id
        String filePath = getLoginUser().getId() + "/" + categoryId;
        String type = FileTypeUtil.getType(file.getInputStream());
        String uuid = (file.getOriginalFilename().replace("." + type, "")) + "@" + IdUtil.fastSimpleUUID() + "." + type;
        String url = MessageFormat.format(config.getOpFileUrl(), config.getUser(), config.getRepository(), filePath, uuid);
        String base64 = Base64Encoder.encode(file.getBytes());
        JSONObject params = new JSONObject();
        params.put("message", "put code");
        params.put("content", base64);
        HttpRequest request = null;
        if (config.isGithub()) {
            request = HttpUtil.createRequest(Method.PUT, url).addHeaders(getRepositoryHeaders());
        } else {
            request = HttpUtil.createRequest(Method.POST, url);
            params.put("access_token", config.getAccessToken());
        }
        String body = request.body(params.toJSONString()).execute().body();
        JSONObject result = JSONObject.parseObject(body);
        if(result.containsKey("content")){
            JSONObject content = result.getJSONObject("content");
            TmpImg tmpImg = new TmpImg(content.getString("sha"),content.getString("url"), Constants.STATUS_TEMP);
            //暂存图片  如果明天没有被存储到t_img表中 表示是垃圾数据， 系统在后续异步追踪时 会清理
            tmpImgService.save(tmpImg);
        }
        return R.ok(result);
    }

    //删除暂存的文件
    @PostMapping("/delTmp")
    public R<JSONObject> del(@RequestBody DelFileReq req) {
        String filePath = getLoginUser().getId() + "/" + req.getCategoryId();
        String url = MessageFormat.format(config.getOpFileUrl(), config.getUser(), config.getRepository(), filePath, req.getFileName());
        JSONObject params = new JSONObject();
        params.put("message", "remove code");
        params.put("sha", req.getSha());
        HttpRequest request = HttpUtil.createRequest(Method.DELETE, url);
        if (config.isGithub()) {
            request.addHeaders(headers);
        } else {
            params.put("access_token", config.getAccessToken());
        }
        String body = request.body(params.toJSONString()).execute().body();
        TmpImg img = tmpImgService.getOne(new LambdaQueryWrapper<TmpImg>().eq(TmpImg::getSha, req.getSha()));
        if(img != null){
            tmpImgService.update(new LambdaUpdateWrapper<TmpImg>().set(TmpImg::getStatus,Constants.STATUS_OFF).eq(TmpImg::getSha, req.getSha()));
        }
        return R.ok(JSONObject.parseObject(body));
    }
}
