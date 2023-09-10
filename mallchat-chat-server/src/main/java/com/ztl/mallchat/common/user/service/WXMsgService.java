package com.ztl.mallchat.common.user.service;

import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;

/**
 * Description:
 * Author:  kevin
 * Date:  2023/09/10
 */
public interface WXMsgService {
    WxMpXmlOutMessage scan(WxMpXmlMessage wxMpXmlMessage);
}
