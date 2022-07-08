package com.rick.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rick.entity.View;
import com.rick.mapper.ViewMapper;
import com.rick.service.IViewService;
import org.springframework.stereotype.Service;

@Service
public class ViewServiceImpl extends ServiceImpl<ViewMapper,View> implements IViewService {
}
