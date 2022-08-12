package com.rick.framework.job;

import com.rick.service.ITmpImgService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@MyJob
public class ClearTempJob  implements BaseJob{

    public final ITmpImgService tmpImgService;

    @Override
    public void run() {

    }
}
