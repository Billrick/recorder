package com.rick.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rick.entity.Img;
import com.rick.mapper.ImgMapper;
import com.rick.service.IImgService;
import org.springframework.stereotype.Service;

@Service
public class ImgServiceImpl extends ServiceImpl<ImgMapper,Img> implements IImgService {
}
