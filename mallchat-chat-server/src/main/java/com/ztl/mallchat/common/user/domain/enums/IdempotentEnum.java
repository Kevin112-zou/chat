package com.ztl.mallchat.common.user.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Description:
 * Author:  kevin
 * Date:  2023/09/20
 */
@AllArgsConstructor
@Getter
public enum IdempotentEnum {
    UID(1,"uid"),
    MSG_ID(2,"消息id");
    private final Integer type;
    private final String desc;
}
