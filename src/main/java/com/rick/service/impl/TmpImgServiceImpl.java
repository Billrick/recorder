package com.rick.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rick.entity.TmpImg;
import com.rick.mapper.TmpImgMapper;
import com.rick.service.ITmpImgService;
import org.springframework.stereotype.Service;

@Service
public class TmpImgServiceImpl extends ServiceImpl<TmpImgMapper,TmpImg> implements ITmpImgService {
}
