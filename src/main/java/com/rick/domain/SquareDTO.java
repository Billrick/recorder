package com.rick.domain;

import com.rick.domain.page.TableDataInfo;
import lombok.Data;


@Data
public class SquareDTO {

    private TableDataInfo list;

    public SquareDTO(){

    }

    public SquareDTO(TableDataInfo list){
        this.list = list;
    }
}
