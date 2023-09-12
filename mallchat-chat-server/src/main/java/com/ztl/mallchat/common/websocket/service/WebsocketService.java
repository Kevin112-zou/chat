package com.ztl.mallchat.common.websocket.service;

import io.netty.channel.Channel;

/**
 * author  kevin
 * Date  2023/09/10
 */
public interface WebsocketService {
    void connect(Channel channel);

    void handleLoginReq(Channel channel);

    void offLine(Channel channel);

    void scanLoginSuccess(Integer code, Long id);

    void waitAuthorize(Integer code);

    void authorize(Channel channel, String data);
}
