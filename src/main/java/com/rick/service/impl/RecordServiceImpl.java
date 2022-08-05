package com.rick.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rick.constants.Constants;
import com.rick.domain.CommentDTO;
import com.rick.domain.RecordDTO;
import com.rick.entity.*;
import com.rick.framework.satoken.LoginHelper;
import com.rick.mapper.RecordMapper;
import com.rick.service.*;
import com.rick.utils.ObjUtils;
import com.rick.utils.StringUtils;
import com.rick.utils.redis.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class RecordServiceImpl extends ServiceImpl<RecordMapper, Record> implements IRecordService {

    public final IImgService imgService;

    public final IUserService userService;

    public final ITmpImgService tmpImgService;

    public final RedisService redisService;

    public final IViewCountService viewCountService;

    public final IViewService viewService;

    public final ICommentService commentService;

    @Override
    public boolean saveRecordAndImg(RecordDTO record) {
        boolean save = saveOrUpdate(BeanUtil.toBean(record, Record.class));
        if (save && CollectionUtil.isNotEmpty(record.getImgs())) {
            List<Img> imgs = record.getImgs();
            imgs.forEach(
                    img -> img.setRecordId(record.getId())
            );
            Set<String> shas = imgs.stream().map(img -> img.getSha()).collect(Collectors.toSet());
            imgService.saveBatch(imgs);
            //把临时图片的状态改为启用状态
            tmpImgService.update(new LambdaUpdateWrapper<TmpImg>().set(TmpImg::getStatus, Constants.STATUS_ON).in(TmpImg::getSha, shas));

        }
        //已存在的数据被删除时, 修改状态,并把图片记录表的状态改为暂存
        if (CollectionUtil.isNotEmpty(record.getRemoveSha())) {
            imgService.update(new LambdaUpdateWrapper<Img>().set(Img::getStatus, Constants.STATUS_OFF).in(Img::getSha, record.getRemoveSha()));
            tmpImgService.update(new LambdaUpdateWrapper<TmpImg>().set(TmpImg::getStatus, Constants.STATUS_TEMP).in(TmpImg::getSha, record.getRemoveSha()));
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
                        .eq(Img::getStatus, Constants.STATUS_ON)).stream()
                .map(img -> img.getSha()).collect(Collectors.toList());
        imgService.update(new LambdaUpdateWrapper<Img>().set(Img::getStatus, Constants.STATUS_OFF).in(Img::getSha, imgs));
        //将数据改为暂存数据  等候清理器进行清理
        tmpImgService.update(new LambdaUpdateWrapper<TmpImg>().set(TmpImg::getStatus, Constants.STATUS_TEMP).in(TmpImg::getSha, imgs));
        return save;
    }

    @Override
    public List<RecordDTO> getList(Record record) {
        LambdaQueryWrapper<Record> wrapper = new LambdaQueryWrapper<>();
        if (record != null) {
            wrapper.eq(record.getCategoryId() != null, Record::getCategoryId, record.getCategoryId());
        }
        List<RecordDTO> list = BeanUtil.copyToList(list(wrapper), RecordDTO.class);
        list.forEach(item -> {
            //获取图片
            List<Img> imgs = imgService.list(new LambdaQueryWrapper<Img>().eq(Img::getRecordId, item.getId()).eq(Img::getStatus, Constants.STATUS_ON));
            item.setImgs(imgs);
            item.setUser(userService.getById(item.getCreateBy()));
            //获取 点赞 收藏的数量
            ViewCount viewInfo = getViewInfo(item.getId() + "",true);

            //获取 评论记录
            List<Comment> comments = commentService.list(new LambdaQueryWrapper<Comment>().eq(Comment::getRecordId, item.getId()).eq(Comment::getStatus, Constants.STATUS_ON));
            item.setComments(BeanUtil.copyToList(comments, CommentDTO.class));
            viewInfo.setComment(Long.valueOf(comments.size()));
            item.setViewCount(viewInfo);
        });
        return list;
    }

    //获取topic对应的 viewInfo
    @Override
    public ViewCount getViewInfo(String id,Boolean notJob) {
        //取出数据库中的数据
        ViewCount dbView = viewCountService.getOne(new LambdaQueryWrapper<ViewCount>().eq(ViewCount::getTopic, Constants.VIEW_COUNT_TOPIC).eq(ViewCount::getRecordId, id));
        ViewCount sumViewCount = new ViewCount();
        if(dbView != null){
            sumViewCount.setId(dbView.getId());
        }
        //取出redis中的数据 计数; 取出赞和收藏量的计数
        List<Object> keys = Arrays.asList(new String[]{Constants.VIEW_TOPIC_LIKE, Constants.VIEW_TOPIC_COLLECTION});
        List<Object> values = redisService.hmget(StringUtils.format(Constants.LIKE_RECORD_COUNTER, id), keys);
        Object objLikeCount = values.get(0);
        Object objCollectionCount = values.get(1);
        Long redisLikeCount = ObjUtils.objDefaultLongVal(objLikeCount, 0L);
        Long redisCollectionCount = ObjUtils.objDefaultLongVal(objCollectionCount, 0L);
        sumViewCount.setLikeIt(redisLikeCount + (dbView == null ? 0L : dbView.getLikeIt()));
        sumViewCount.setCollection(redisCollectionCount+(dbView == null ? 0L : dbView.getCollection()));
        sumViewCount.setTopic(Constants.VIEW_COUNT_TOPIC);
        sumViewCount.setRecordId(Long.valueOf(id));
        if(notJob){
            Integer userId = LoginHelper.getUserId();
            for (Object key: keys) {
                //自己是否点赞
                selfDoIt((String) key,id,userId+"",sumViewCount);
            }
        }else{
            redisService.hdel(StringUtils.format(Constants.LIKE_RECORD_COUNTER, id), keys.toArray());
        }
        return sumViewCount;
    }


    public void selfDoIt(String topic,String id,String userId,ViewCount sumViewCount){
        //从redis中获取该用户在topic是否有操作记录
        Object userRecord = redisService.hget(StringUtils.format(Constants.LIKE_RECORD_AND_RANK, topic, id), userId);
        JSONObject viewInfo = null;
        Boolean flag = false;
        if (userRecord != null) {
            viewInfo = JSONObject.parseObject((String) userRecord);
            String status = viewInfo.getString("status");
            flag = status.equals(Constants.STATUS_ON);
        } else {
            Long likeCount = viewService.count(
                    new LambdaQueryWrapper<View>()
                            .eq(View::getTopic, topic)
                            .eq(View::getStatus, Constants.STATUS_ON)
                            .eq(View::getFromUserId, userId)
                            .eq(View::getRecordId, id)
            );
            flag = likeCount > 0;
        }
        switch (topic){
            case Constants.VIEW_TOPIC_LIKE:
                sumViewCount.setILikeIt(flag);
                break;
            case Constants.VIEW_TOPIC_COLLECTION:
                sumViewCount.setICollectionIt(flag);
                break;
            default:
        }


    }
}
