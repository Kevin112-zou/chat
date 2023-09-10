package com.ztl.mallchat.common.user.service.handler;

import com.ztl.mallchat.common.user.service.adapter.TextBuilder;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URLEncoder;
import java.util.Map;

@Component
public class ScanHandler extends AbstractHandler {

    @Value("${wx.mp.callback}")
    private String callback;
    private static final String URL = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=%s&redirect_uri=%s&response_type=code&scope=snsapi_userinfo&state=STATE#wechat_redirect";
    @Override
    public WxMpXmlOutMessage handle(WxMpXmlMessage wxMpXmlMessage, Map<String, Object> map,
                                    WxMpService wxMpService, WxSessionManager wxSessionManager) throws WxErrorException {
        // ToDo 扫码事件处理
        String code = wxMpXmlMessage.getEventKey();
        String openId = wxMpXmlMessage.getFromUser();

        // 根据官方文档需要重定向到到指定的URL， 通过定向到/callback
        String skipUrl = String.format(URL, wxMpService.getWxMpConfigStorage().getAppId(), URLEncoder.encode(callback + "/wx/portal/public/callBack"));
        return new TextBuilder().build("请点击链接授权：<a href=\"" + skipUrl + "\">登录</a>", wxMpXmlMessage, wxMpService);


    }

}
