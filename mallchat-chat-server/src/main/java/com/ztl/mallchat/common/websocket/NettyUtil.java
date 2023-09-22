package com.ztl.mallchat.common.websocket;

import io.netty.channel.Channel;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;

/**
 * Description:
 * Author:  kevin
 * Date:  2023/09/12
 */
public class NettyUtil {
    public static AttributeKey<String> TOKEN = AttributeKey.valueOf("token");
    public static AttributeKey<String> IP = AttributeKey.valueOf("ip");
    /**
     * 你可以在Netty应用程序中方便地将自定义属性附加到通道上，以便稍后可以使用这些属性来存储和检索数据。
     */
    public static <T> void setAttr(Channel channel, AttributeKey<T> key , T value){
        Attribute<T> attr = channel.attr(key);
        attr.set(value);
    }
    public static <T> T getAttr(Channel channel, AttributeKey<T> key){
        Attribute<T> attr = channel.attr(key);
        return attr.get();
    }

}
