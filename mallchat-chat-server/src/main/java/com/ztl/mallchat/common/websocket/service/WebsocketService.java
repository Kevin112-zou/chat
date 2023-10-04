package com.ztl.mallchat.common.websocket.service;

import com.ztl.mallchat.common.websocket.domain.vo.resp.ws.WSBaseResp;
import io.netty.channel.Channel;
import me.chanjar.weixin.mp.bean.device.BaseResp;

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

    /**
     * 发送消息给所有在线用户（单机可用）
     */
    void sendMsgToAll(WSBaseResp<?> msg);

}
