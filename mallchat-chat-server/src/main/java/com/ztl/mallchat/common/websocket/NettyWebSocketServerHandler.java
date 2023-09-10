package com.ztl.mallchat.common.websocket;

import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.http.webservice.SoapClient;
import cn.hutool.json.JSONUtil;
import com.ztl.mallchat.common.websocket.domain.enums.WSReqTypeEnum;
import com.ztl.mallchat.common.websocket.domain.vo.req.ws.WSBaseReq;
import com.ztl.mallchat.common.websocket.service.WebsocketService;
import com.ztl.mallchat.common.websocket.service.impl.WebsocketServiceImpl;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

public class NettyWebSocketServerHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    private WebsocketService websocketService;

    /**
     * 当连接建立的时候，将websocket保存到spring容器中管理起来
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        websocketService = SpringUtil.getBean(WebsocketService.class);
        websocketService.connect(ctx.channel());
    }

    /**
     * 当你在 Netty 中使用 Channel 与远程端点通信时，可能会遇到一些非常具体的事件，这些事件不是标准的消息传输或数据读写事件。
     * 这些事件可以是连接状态的更改、协议特定的事件、超时事件等等。在这些情况下，你可以使用 userEventTriggered 方法来捕获和处理这些自定义事件
     * @param ctx
     * @param evt
     * @throws Exception
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {

        if(evt instanceof WebSocketServerProtocolHandler.HandshakeComplete){
            System.out.println("握手完成");
        }else if(evt instanceof IdleStateEvent){
            System.out.println("心跳检查");
            IdleStateEvent event = (IdleStateEvent) evt;
            if(event.state() == IdleState.READER_IDLE){
                System.out.println("读空闲");
                // 关闭连接
                ctx.channel().close();
                // todo 用户下线
            }
        }
    }
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        // 1. 拿到数据msg
        String text = msg.text();
//        System.out.println(text);
        // 2. 根据type的类型执行相应的逻辑
        WSBaseReq wsBaseReq = JSONUtil.toBean(text, WSBaseReq.class);
        switch (WSReqTypeEnum.of(wsBaseReq.getType())) {
            case LOGIN:
                websocketService.handleLoginReq(ctx.channel());
                System.out.println("用户登录！！！");
            case AUTHORIZE:
                break;
            case HEARTBEAT:
                break;
        }
    }

}
