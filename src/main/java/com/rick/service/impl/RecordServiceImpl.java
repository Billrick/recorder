package com.rick.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rick.constants.Constants;
import com.rick.entity.Img;
import com.rick.entity.Record;
import com.rick.entity.TmpImg;
import com.rick.mapper.RecordMapper;
import com.rick.service.IImgService;
import com.rick.service.IRecordService;
import com.rick.service.ITmpImgService;
import com.rick.service.IUserService;
import lombok.RequiredArgsConstructor;
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

    public final ITmpImgService tmpImgService;


    @Override
    public boolean saveRecordAndImg(Record record) {
        boolean save = saveOrUpdate(record);
        if (save && CollectionUtil.isNotEmpty(record.getImgs())) {
            record.getImgs().forEach(
                    img -> img.setRecordId(record.getId())
            );
            imgService.saveBatch(record.getImgs());
        }
        //已存在的数据被删除时, 修改状态,并把图片记录表的状态改为暂存
        if(CollectionUtil.isNotEmpty(record.getRemoveSha())){
            imgService.update(new LambdaUpdateWrapper<Img>().set(Img::getStatus,Constants.STATUS_OFF).in(Img::getSha,record.getRemoveSha()));
            tmpImgService.update(new LambdaUpdateWrapper<TmpImg>().eq(TmpImg::getStatus,Constants.STATUS_TEMP).eq(TmpImg::getSha,record.getRemoveSha()));
        }
        return save;
    }

    @Override
    public boolean deleteRecordAndImg(Integer id) {
        //修改记录状态
        boolean save = update(new LambdaUpdateWrapper<Record>().set(Record::getStatus, Constants.STATUS_OFF).eq(Record::getId, id));
        //修改图片状态
        List<String> imgs = imgService.list(new LambdaQueryWrapper<Img>()
                .eq(Img::getRecordId, id)
                .eq(Img::getStatus,Constants.STATUS_ON)).stream()
                .map(img -> img.getSha()).collect(Collectors.toList());
        imgService.update(new LambdaUpdateWrapper<Img>().set(Img::getStatus, Constants.STATUS_OFF).in(Img::getSha, imgs));
        //将数据改为暂存数据  等候清理器进行清理
        tmpImgService.update(new LambdaUpdateWrapper<TmpImg>().eq(TmpImg::getStatus,Constants.STATUS_TEMP).eq(TmpImg::getSha,imgs));
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
            List<Img> imgs = imgService.list(new LambdaQueryWrapper<Img>().eq(Img::getRecordId, item.getId()).eq(Img::getStatus,Constants.STATUS_ON));
            item.setImgs(imgs);
            item.setUser(userService.getById(item.getCreateBy()));
        });
        return list;
    }
}
