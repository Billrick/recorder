package com.rick.domain.request;

import lombok.Data;

@Data
public class DelFileReq {

    private String recordId;
    private String categoryId;
    private String sha;
    private String fileName;
}
