package com.ztl.mallchat.common.common.enums;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import org.springframework.boot.SpringApplication;

@AllArgsConstructor
@Getter
public enum YesOrNo {
    YES(1,"是"),
    NO(0,"否")
    ;
    private final Integer status;
    private final String desc;
}
