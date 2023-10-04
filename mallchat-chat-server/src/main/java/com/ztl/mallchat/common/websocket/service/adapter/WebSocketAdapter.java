package com.ztl.mallchat.common.websocket.service.adapter;

import com.ztl.mallchat.common.common.enums.YesOrNo;
import com.ztl.mallchat.common.user.domain.entity.User;
import com.ztl.mallchat.common.websocket.domain.enums.WSRespTypeEnum;
import com.ztl.mallchat.common.websocket.domain.vo.resp.ws.WSBaseResp;
import com.ztl.mallchat.common.websocket.domain.vo.resp.ws.WSBlack;
import com.ztl.mallchat.common.websocket.domain.vo.resp.ws.WSLoginSuccess;
import com.ztl.mallchat.common.websocket.domain.vo.resp.ws.WSLoginUrl;
import me.chanjar.weixin.mp.bean.result.WxMpQrCodeTicket;

/**
 * Description:
 * Author:  kevin
 * Date:  2023/09/10
 */

public class WebSocketAdapter {
    public static WSBaseResp<?> buildResp(WxMpQrCodeTicket wxMpQrCodeTicket) {
        WSBaseResp<WSLoginUrl> resp = new WSBaseResp<>();
        resp.setType(WSRespTypeEnum.LOGIN_URL.getType());
        resp.setData(new WSLoginUrl(wxMpQrCodeTicket.getUrl()));
        return resp;
    }


    public static WSBaseResp<?> buildLoginSuccessResp(User user, String token,boolean power) {
        WSBaseResp<WSLoginSuccess> resp = new WSBaseResp<>();
        resp.setType(WSRespTypeEnum.LOGIN_SUCCESS.getType());
        WSLoginSuccess build = WSLoginSuccess.builder()
                .avatar(user.getAvatar())
                .name(user.getName())
                .token(token)
                .uid(user.getId())
                .power(power ? YesOrNo.YES.getStatus() : YesOrNo.NO.getStatus()) // 1表示管理员，0表示普通用户
                .build();
        resp.setData(build);
        return resp;
    }

    public static WSBaseResp<?> buildWaitAuthorizeResp() {
        WSBaseResp<WSLoginUrl> resp = new WSBaseResp<>();
        resp.setType(WSRespTypeEnum.LOGIN_SCAN_SUCCESS.getType());
        return resp;
    }

    public static WSBaseResp<?> buildInvalidTokenResp() {
        WSBaseResp<WSLoginUrl> resp = new WSBaseResp<>();
        resp.setType(WSRespTypeEnum.INVALIDATE_TOKEN.getType());
        return resp;
    }

    public static WSBaseResp<?> buildBlackResp(User user) {
        WSBaseResp<WSBlack> resp = new WSBaseResp<>();
        resp.setType(WSRespTypeEnum.BLACK.getType());
        WSBlack build = WSBlack.builder()
                .uid(user.getId())
                .build();
        resp.setData(build);
        return resp;
    }
}
