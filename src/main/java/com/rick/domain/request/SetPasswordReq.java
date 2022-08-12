package com.rick.domain.request;

import lombok.Data;

@Data
public class SetPasswordReq {

    private String currentPassword;
    private String password;
}
