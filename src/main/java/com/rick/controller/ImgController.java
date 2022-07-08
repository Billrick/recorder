package com.rick.controller;

import cn.hutool.core.codec.Base64Encoder;
import cn.hutool.core.io.FileUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import cn.hutool.http.Method;
import com.alibaba.fastjson.JSONObject;
import com.rick.base.controller.BaseController;
import com.rick.domain.R;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/img")
@RequiredArgsConstructor
public class ImgController extends BaseController {

    public final static String USER = "Billrick";
    public final static String REPS = "recorderF";
    public final static String AUTH_TOKEN = "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";

    public static Map<String,String> headers = new HashMap<>();
    static {
        headers.put("Accept","application/vnd.github.v3+json");
        headers.put("Authorization","token "+AUTH_TOKEN);
    }

    @PostMapping("/upload")
    public R<JSONObject> upload(Integer categoryId,MultipartFile file) throws IOException {
        //用户id + 分类id
        String filePath = getLoginUser().getId() + "/"+categoryId;
        String originalFilename = file.getOriginalFilename();
        String url = MessageFormat.format("https://api.github.com/repos/{0}/{1}/contents/{2}/{3}",USER,REPS,filePath,originalFilename);
        String base64 = Base64Encoder.encode(file.getBytes());
        Map<String,Object> params = new HashMap<>();
        params.put("message","put code");
        params.put("content",base64);
        String body = HttpUtil.createRequest(Method.PUT,url).addHeaders(headers).body(JSONObject.toJSONString(params)).execute().body();
        return R.ok(JSONObject.parseObject(body));
    }
}
