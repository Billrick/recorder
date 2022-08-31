package com.rick.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.rick.constants.Constants;
import com.rick.domain.CommentDTO;
import com.rick.domain.RecordDTO;
import com.rick.domain.WebUserDTO;
import com.rick.domain.page.TableDataInfo;
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
    public boolean saveRecordAndImg(RecordDTO recordDto) {
        Record record = BeanUtil.toBean(recordDto, Record.class);
        boolean save = saveOrUpdate(record);
        if (save && CollectionUtil.isNotEmpty(recordDto.getImgs())) {
            List<Img> imgs = recordDto.getImgs();
            imgs.forEach(
                    img -> img.setRecordId(record.getId())
            );
            imgService.saveBatch(imgs);
            Set<Long> ids = imgs.stream().map(img -> img.getId()).collect(Collectors.toSet());
            //把临时图片的状态改为启用状态
            tmpImgService.update(new LambdaUpdateWrapper<TmpImg>().set(TmpImg::getStatus, Constants.STATUS_ON).in(TmpImg::getId, ids));

        }
        //已存在的数据被删除时, 修改状态,并把图片记录表的状态改为暂存
        if (CollectionUtil.isNotEmpty(recordDto.getRemoveIds())) {
            imgService.update(new LambdaUpdateWrapper<Img>().set(Img::getStatus, Constants.STATUS_OFF).in(Img::getId, recordDto.getRemoveIds()));
            tmpImgService.update(new LambdaUpdateWrapper<TmpImg>().set(TmpImg::getStatus, Constants.STATUS_TEMP).in(TmpImg::getId, recordDto.getRemoveIds()));
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
        if(CollectionUtil.isNotEmpty(imgs)){
            imgService.update(new LambdaUpdateWrapper<Img>().set(Img::getStatus, Constants.STATUS_OFF).in(Img::getSha, imgs));
            //将数据改为暂存数据  等候清理器进行清理
            tmpImgService.update(new LambdaUpdateWrapper<TmpImg>().set(TmpImg::getStatus, Constants.STATUS_TEMP).in(TmpImg::getSha, imgs));
        }
        return save;
    }

    @Override
    public TableDataInfo getList(RecordDTO record,Boolean isPublic) {
        LambdaQueryWrapper<Record> wrapper = new LambdaQueryWrapper<>();
        if(!isPublic){
            wrapper.eq(Record::getCreateBy,LoginHelper.getUserId());
        }
        if (record != null) {
            wrapper.eq(record.getCategoryId() != null, Record::getCategoryId, record.getCategoryId());
            wrapper.in(CollectionUtil.isNotEmpty(record.getCategoryIds()),Record::getCategoryId,record.getCategoryIds());
        }
        wrapper.eq(Record::getStatus,Constants.STATUS_ON);
        wrapper.orderByDesc(Record::getId);
        PageHelper.startPage(record.getCurrent(),record.getPageSize());
        List<Record> entityList = list(wrapper);
        TableDataInfo dataTable = getDataTable(entityList);

        List<RecordDTO> list = BeanUtil.copyToList(entityList, RecordDTO.class);
        list.forEach(item -> {
            //获取图片
            List<Img> imgs = imgService.list(new LambdaQueryWrapper<Img>().eq(Img::getRecordId, item.getId()).eq(Img::getStatus, Constants.STATUS_ON));
            item.setImgs(imgs);
            User user = userService.getById(item.getCreateBy());
            item.setUser(BeanUtil.toBean(user, WebUserDTO.class));
            //获取 点赞 收藏的数量
            ViewCount viewInfo = getViewInfo(item.getId() + "",true);
            //获取 评论记录
            //List<Comment> comments = commentService.list(new LambdaQueryWrapper<Comment>().eq(Comment::getRecordId, item.getId()).eq(Comment::getStatus, Constants.STATUS_ON));
            //item.setComments(BeanUtil.copyToList(comments, CommentDTO.class));
            //viewInfo.setComment(Long.valueOf(comments.size()));
            item.setViewCount(viewInfo);
        });
        dataTable.setRows(list);
        return dataTable;
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
            if(StpUtil.isLogin()){
                Integer userId = LoginHelper.getUserId();
                for (Object key: keys) {
                    //自己是否点赞
                    selfDoIt((String) key,id,userId+"",sumViewCount);
                }
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

    /**
     * 响应请求分页数据
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    protected TableDataInfo getDataTable(List<?> list)
    {
        TableDataInfo rspData = new TableDataInfo();
        rspData.setCode(200);
        rspData.setRows(list);
        rspData.setTotal(new PageInfo(list).getTotal());
        return rspData;
    }
}
