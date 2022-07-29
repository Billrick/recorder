package com.rick.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rick.constants.Constants;
import com.rick.entity.Img;
import com.rick.entity.Record;
import com.rick.mapper.RecordMapper;
import com.rick.service.IImgService;
import com.rick.service.IRecordService;
import com.rick.service.IUserService;
import com.rick.utils.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class RecordServiceImpl extends ServiceImpl<RecordMapper, Record> implements IRecordService {

    public final IImgService imgService;

    public final IUserService userService;

    @Override
    public boolean saveRecordAndImg(Record record) {
        boolean save = save(record);
        if (save && CollectionUtil.isNotEmpty(record.getImgs())) {
            record.getImgs().forEach(
                    img -> img.setRecordId(record.getId())
            );
            imgService.saveBatch(record.getImgs());
        }
        return save;
    }

    @Override
    public boolean deleteRecordAndImg(Integer id) {
        boolean save = update(new LambdaUpdateWrapper<Record>().set(Record::getStatus, Constants.STATUS_OFF).eq(Record::getId, id));
        List<Long> imgs = imgService.list(new LambdaQueryWrapper<Img>().eq(Img::getRecordId, id)).stream().map(img -> img.getId()).collect(Collectors.toList());
        imgService.update(new LambdaUpdateWrapper<Img>().set(Img::getStatus, Constants.STATUS_OFF).in(Img::getId, imgs));
        return save;
    }

    @Override
    public List<Record> getList(Record record) {
        LambdaQueryWrapper<Record> wrapper = new LambdaQueryWrapper<>();
        if(record != null){
            wrapper.eq(record.getCategoryId() != null, Record::getCategoryId, record.getCategoryId());
        }
        List<Record> list = list(wrapper);
        list.forEach(item -> {
            List<Img> imgs = imgService.list(new LambdaQueryWrapper<Img>().eq(Img::getRecordId, item.getId()));
            item.setImgs(imgs);
            item.setUser(userService.getById(item.getCreateBy()));
        });
        return list;
    }
}
