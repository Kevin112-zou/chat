package com.ztl.mallchat.common.user.service.impl;

import cn.hutool.core.util.StrUtil;
import com.ztl.mallchat.common.user.dao.UserDao;
import com.ztl.mallchat.common.user.domain.entity.User;
import com.ztl.mallchat.common.user.service.IUserService;
import com.ztl.mallchat.common.user.service.WXMsgService;
import com.ztl.mallchat.common.user.service.adapter.TextBuilder;
import com.ztl.mallchat.common.user.service.adapter.UserAdapter;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.jws.soap.SOAPBinding;
import java.net.URLEncoder;
import java.util.Objects;

/**
 * Description:
 * Author:  kevin
 * Date:  2023/09/10
 */
@Service
@Slf4j
public class WXMsgServiceImpl implements WXMsgService {
    @Value("${wx.mp.callback}")
    private String callback;
    private static final String URL = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=%s&redirect_uri=%s&response_type=code&scope=snsapi_userinfo&state=STATE#wechat_redirect";
    @Autowired
    private UserDao userDao;
    @Autowired
    private IUserService userService;
    @Autowired
    @Lazy
    private WxMpService wxMpService;
    @Override
    public WxMpXmlOutMessage scan(WxMpXmlMessage wxMpXmlMessage) {
        Integer code = getEventKey(wxMpXmlMessage);
        String openId = wxMpXmlMessage.getFromUser();
        if(Objects.isNull(code)){
            return null;
        }
        User user = userDao.getByOpenId(openId);
        boolean registered = Objects.nonNull(user);
        // 这里双重效验就是为了避免当用户扫码后一直没有点击授权，那么也是登录失败的
        boolean authorized = Objects.nonNull(user) && StrUtil.isNotBlank(user.getAvatar());
        if (authorized){
            // todo 登录成功的逻辑，给channel推送消息
        }
        if(!registered){
            // 注册逻辑
            User user1 = UserAdapter.buildUser(openId);
            userService.register(user1);
        }
        // 根据官方文档需要重定向到到指定的URL， 通过定向到/callback
        String skipUrl = String.format(URL, wxMpService.getWxMpConfigStorage().getAppId(), URLEncoder.encode(callback + "/wx/portal/public/callBack"));
        return new TextBuilder().build("请点击链接授权：<a href=\"" + skipUrl + "\">登录</a>", wxMpXmlMessage, wxMpService);
    }

    /**
     * 这里因为首次关注和登录时的code不一样，需要进行一下统一处理
     * @param wxMpXmlMessage
     */
    private Integer getEventKey(WxMpXmlMessage wxMpXmlMessage) {
        try {
            String eventKey = wxMpXmlMessage.getEventKey();
            String code = eventKey.replace("qrscene","");
            return Integer.parseInt(code);
        } catch (Exception e) {
            log.info("getEventKey error eventKey:{}",wxMpXmlMessage.getEventKey(),e);
            return null;
        }
    }
}
