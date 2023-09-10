package com.ztl.mallchat.common.websocket.service;

import io.netty.channel.Channel;

/**
 * author  kevin
 * Date  2023/09/10
 */
public interface WebsocketService {
    void connect(Channel channel);

    void handleLoginReq(Channel channel);
}
