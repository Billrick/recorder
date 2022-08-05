package com.rick.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rick.entity.ViewCount;
import com.rick.mapper.ViewCountMapper;
import com.rick.service.IViewCountService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ViewCountService extends ServiceImpl<ViewCountMapper, ViewCount> implements IViewCountService {
}
