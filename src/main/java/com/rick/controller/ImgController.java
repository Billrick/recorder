package com.rick.controller;

import cn.hutool.core.codec.Base64Encoder;
import cn.hutool.core.io.FileTypeUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import cn.hutool.http.Method;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.rick.base.controller.BaseController;
import com.rick.domain.R;
import com.rick.entity.Img;
import com.rick.service.IImgService;
import com.rick.utils.StringUtils;
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
@RequestMapping("/f")
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

    public final IImgService imgService;

    @PostMapping("/upload")
    public R<JSONObject> upload(Integer categoryId,MultipartFile file) throws IOException {
        //用户id + 分类id
        String filePath = getLoginUser().getId() + "/"+categoryId;
        String type = FileTypeUtil.getType(file.getInputStream());
        String uuid = (file.getOriginalFilename().replace("."+type,"")) +"@"+ IdUtil.fastSimpleUUID() + "."+type;
        String url = MessageFormat.format("https://api.github.com/repos/{0}/{1}/contents/{2}/{3}",USER,REPS,filePath,uuid);
        String base64 = Base64Encoder.encode(file.getBytes());
        JSONObject params = new JSONObject();
        params.put("message","put code");
        params.put("content",base64);
        String body = HttpUtil.createRequest(Method.PUT,url).addHeaders(headers).body(params.toJSONString()).execute().body();
        return R.ok(JSONObject.parseObject(body));
    }

    @PostMapping("/del")
    public R<JSONObject> del(Integer categoryId,String sha,String fileName){
        if(StringUtils.isEmpty(fileName)){
            Img one = imgService.getOne(new LambdaQueryWrapper<Img>().eq(Img::getSha, sha));
            int i = one.getImgUrl().lastIndexOf("/");
            fileName = one.getImgUrl().substring(i);
        }
        String filePath = getLoginUser().getId() + "/"+categoryId;
        String url = MessageFormat.format("https://api.github.com/repos/{0}/{1}/contents/{2}/{3}",USER,REPS,filePath,fileName);
        JSONObject params = new JSONObject();
        params.put("message","remove code");
        params.put("sha",sha);
        String body = HttpUtil.createRequest(Method.DELETE, url).addHeaders(headers).body(params.toJSONString()).execute().body();
        return R.ok(JSONObject.parseObject(body));
    }
}
