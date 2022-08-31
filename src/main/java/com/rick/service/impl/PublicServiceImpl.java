package com.rick.service.impl;

import com.rick.domain.RecordDTO;
import com.rick.domain.SquareDTO;
import com.rick.domain.page.TableDataInfo;
import com.rick.service.IPublicService;
import com.rick.service.IRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PublicServiceImpl implements IPublicService {

    public final IRecordService recordService;

    @Override
    public SquareDTO square(RecordDTO recordDTO) {
        TableDataInfo tableDataInfo = recordService.getList(recordDTO, true);
        return new SquareDTO(tableDataInfo);
    }


}
