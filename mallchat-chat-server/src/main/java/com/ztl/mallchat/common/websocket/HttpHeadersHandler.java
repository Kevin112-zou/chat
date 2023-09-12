package com.ztl.mallchat.common.websocket;
import cn.hutool.core.net.url.UrlBuilder;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;

import java.util.Optional;
/**
 * Description:
 * Author:  kevin
 * Date:  2023/09/12
 */


public class HttpHeadersHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if(msg instanceof FullHttpRequest){
            FullHttpRequest request = (FullHttpRequest) msg;
            UrlBuilder urlBuilder = UrlBuilder.ofHttp(request.uri());

            /**
             * Optional.ofNullable(urlBuilder.getQuery())：
             * 首先，它从urlBuilder对象中获取查询参数，并将其封装在一个Optional对象中。
             * Optional是一种Java中用于处理可能为null的值的方式，以避免空指针异常。
             *.map(k->k.get("token"))：
             * 然后，它使用.map()方法，如果查询参数不为null，就尝试从查询参数中获取名为"token"的参数。这里的k代表查询参数。
             * .map(CharSequence::toString)：如果存在名为"token"的参数，将其转换为字符串类型。
             * .orElse("")：最后，如果没有找到名为"token"的参数，它将返回一个空字符串""作为默认值。
             */
            String token = Optional.ofNullable(urlBuilder.getQuery()).map(k->k.get("token")).map(CharSequence::toString).orElse("");
            NettyUtil.setAttr(ctx.channel(), NettyUtil.TOKEN, token);
            request.setUri(urlBuilder.getPath().toString());
        }
        ctx.fireChannelRead(msg);
    }
}
