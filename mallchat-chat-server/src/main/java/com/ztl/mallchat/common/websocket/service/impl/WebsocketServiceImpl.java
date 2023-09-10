package com.ztl.mallchat.common.websocket.service.impl;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSONUtil;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.ztl.mallchat.common.websocket.domain.dto.WSChannelExtraDTO;
import com.ztl.mallchat.common.websocket.domain.enums.WSRespTypeEnum;
import com.ztl.mallchat.common.websocket.domain.vo.req.ws.WSBaseReq;
import com.ztl.mallchat.common.websocket.domain.vo.resp.ws.WSBaseResp;
import com.ztl.mallchat.common.websocket.domain.vo.resp.ws.WSLoginUrl;
import com.ztl.mallchat.common.websocket.service.WebsocketService;
import com.ztl.mallchat.common.websocket.service.adapter.WebSocketAdapter;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.swagger.models.auth.In;
import lombok.SneakyThrows;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.result.WxMpQrCodeTicket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * description: 专门用来管理websocket的逻辑，包括推拉
 * author  kevin
 * Date  2023/09/10
 */
@Service
public class WebsocketServiceImpl implements WebsocketService {

    @Autowired
    private WxMpService wxMpService;

    /**
     * 管理所有的连接（用户&游客）
     */
    private static final ConcurrentHashMap<Channel, WSChannelExtraDTO> ONLINE_WS_MAP = new ConcurrentHashMap<>();

    public static final int MAXIMUM_SIZE = 1000;
    public static final Duration DURATION = Duration.ofHours(1);
    /**
     * 为了防止map溢出或者OOM的情况，这里使用Caffeine框架做了一个淘汰策略
     * 用来临时保存code和channel的映射关系
     */
    private static final Cache<Integer,Channel> WAIT_LOGIN_MAP = Caffeine.newBuilder()
            .maximumSize(MAXIMUM_SIZE)
            .expireAfterWrite(DURATION)
            .build();

    /**
     * 具体的保存逻辑，通过ConcurrentHashMap来保存每个Channel中的信息
     * @param channel
     */
    @Override
    public void connect(Channel channel) {
        ONLINE_WS_MAP.put(channel, new WSChannelExtraDTO());
    }

    /**
     * 前端登录请求逻辑
     * @param channel
     */
    @SneakyThrows
    @Override
    public void handleLoginReq(Channel channel) {
        // 1. 生成随机的code
        Integer code =  generateLoginCode(channel);
        // 2. 向微信申请带参数的二维码
        WxMpQrCodeTicket wxMpQrCodeTicket = wxMpService.getQrcodeService().qrCodeCreateTmpTicket(code, (int) DURATION.getSeconds());
        // 3. 将二维码推送给前端
        sendMsg(channel, WebSocketAdapter.buildResp(wxMpQrCodeTicket));
    }

    @Override
    public void offLine(Channel channel) {
        // 如果用户下线就将channel从map中remove掉
        ONLINE_WS_MAP.remove(channel);
        // todo 进行用户下线的广播
    }

    private void sendMsg(Channel channel, WSBaseResp<WSLoginUrl> resp) {
        channel.writeAndFlush(new TextWebSocketFrame(JSONUtil.toJsonStr(resp)));
    }

    private Integer generateLoginCode(Channel channel) {
        Integer code;
        do {
            code = RandomUtil.randomInt(Integer.MAX_VALUE);
            /**
             * 这里的逻辑是nonNull说明就是code设置成功了
             */
        }while (Objects.nonNull(WAIT_LOGIN_MAP.asMap().putIfAbsent(code,channel))); // putIfAbsent(): 用来判断是否code重复，只有不重复才能设置成功
        return code;
    }
}
