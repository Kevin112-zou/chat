package com.ztl.mallchat.common.websocket.service.adapter;

import com.ztl.mallchat.common.websocket.domain.enums.WSRespTypeEnum;
import com.ztl.mallchat.common.websocket.domain.vo.resp.ws.WSBaseResp;
import com.ztl.mallchat.common.websocket.domain.vo.resp.ws.WSLoginUrl;
import me.chanjar.weixin.mp.bean.result.WxMpQrCodeTicket;

/**
 * Description:
 * Author:  kevin
 * Date:  2023/09/10
 */
public class WebSocketAdapter {
    public static WSBaseResp<WSLoginUrl> buildResp(WxMpQrCodeTicket wxMpQrCodeTicket) {
        WSBaseResp<WSLoginUrl> resp = new WSBaseResp<>();
        resp.setType(WSRespTypeEnum.LOGIN_URL.getType());
        resp.setData(new WSLoginUrl(wxMpQrCodeTicket.getUrl()));
        return resp;
    }
}
