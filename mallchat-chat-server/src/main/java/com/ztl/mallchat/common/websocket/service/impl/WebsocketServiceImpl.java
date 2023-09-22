package com.ztl.mallchat.common.websocket.service.impl;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSONUtil;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.ztl.mallchat.common.common.event.UserOnlineEvent;
import com.ztl.mallchat.common.user.dao.UserDao;
import com.ztl.mallchat.common.user.domain.entity.User;
import com.ztl.mallchat.common.user.service.LoginService;
import com.ztl.mallchat.common.websocket.NettyUtil;
import com.ztl.mallchat.common.websocket.domain.dto.WSChannelExtraDTO;
import com.ztl.mallchat.common.websocket.domain.vo.resp.ws.WSBaseResp;
import com.ztl.mallchat.common.websocket.service.WebsocketService;
import com.ztl.mallchat.common.websocket.service.adapter.WebSocketAdapter;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.SneakyThrows;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.result.WxMpQrCodeTicket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Date;
import java.util.Objects;
import java.util.PrimitiveIterator;
import java.util.concurrent.ConcurrentHashMap;

/**
 * description: 专门用来管理websocket的逻辑，包括推拉
 * author  kevin
 * Date  2023/09/10
 */
@Service
public class WebsocketServiceImpl implements WebsocketService {

    @Autowired
    private UserDao userDao;

    @Autowired
    @Lazy
    private WxMpService wxMpService;

    @Autowired
    private LoginService loginService;
    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;
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
     * @SneakyThrows: 忽视不必要异常
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

    /**
     * 扫码登录成功逻辑需要将前端需要的信息返回
     */
    @Override
    public void scanLoginSuccess(Integer code, Long uid) {
        // 1. 确认连接在机器上
        Channel channel = WAIT_LOGIN_MAP.getIfPresent(code);
        if(Objects.isNull(channel)){
            return;
        }
        // 2. 获取到用户信息
        User user = userDao.getById(uid);
        // 3. 移除code
        WAIT_LOGIN_MAP.invalidate(code);
        // 4. 调用登录模块获取token
        String token = loginService.getLoginToken(uid);
        // 5. 登录成功封装返回的数据,并且要更新用户的一些信息
        loginSuccess(channel,user,token);
    }

    /**
     * 等待用户授权
     * @param code
     */
    @Override
    public void waitAuthorize(Integer code) {
        // 1. 确认连接在机器上
        Channel channel = WAIT_LOGIN_MAP.getIfPresent(code);
        if(Objects.isNull(channel)){
            return;
        }
        sendMsg(channel,WebSocketAdapter.buildWaitAuthorizeResp());
    }

    /**
     * 用户认证
     * @param channel
     * @param token
     */
    @Override
    public void authorize(Channel channel, String token) {
        Long uid = loginService.getValidUid(token);
        if(Objects.nonNull(uid)){
            User user = userDao.getById(uid);
            loginSuccess(channel,user,token);
        }else {
            // 如果token已经过期，告诉前端，让他下次请求不用再带token
            sendMsg(channel,WebSocketAdapter.buildInvalidTokenResp());
        }
    }

    private void loginSuccess(Channel channel, User user, String token) {
        // 1. 保存channel对应的uid
        WSChannelExtraDTO wsChannelExtraDTO = ONLINE_WS_MAP.get(channel);
        wsChannelExtraDTO.setUid(user.getId());
        // 推送消息给前端
        sendMsg(channel,WebSocketAdapter.buildLoginSuccessResp(user,token));
        // 用户上下线的事件
        user.setLastOptTime(new Date());
        user.refreshIp(NettyUtil.getAttr(channel,NettyUtil.IP));
        applicationEventPublisher.publishEvent(new UserOnlineEvent(this,user));
    }

    private void sendMsg(Channel channel, WSBaseResp<?> resp) {
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
