package com.ztl.mallchat.common.websocket.domain.vo.resp.ws;

import lombok.Data;

/**
 * author  kevin
 * Date  2023/09/08
 */
@Data
public class WSBaseResp<T> {
    /**
     * @see com.ztl.mallchat.common.websocket.domain.enums.WSRespTypeEnum
     */
    private Integer type;
    private T data;
}
