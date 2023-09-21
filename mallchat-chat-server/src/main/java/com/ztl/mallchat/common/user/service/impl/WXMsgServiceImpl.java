package com.ztl.mallchat.common.user.service.impl;

import cn.hutool.core.util.StrUtil;
import com.ztl.mallchat.common.common.constant.RedisKey;
import com.ztl.mallchat.common.common.utils.RedisUtils;
import com.ztl.mallchat.common.user.dao.UserDao;
import com.ztl.mallchat.common.user.domain.entity.User;
import com.ztl.mallchat.common.user.service.IUserService;
import com.ztl.mallchat.common.user.service.WXMsgService;
import com.ztl.mallchat.common.user.service.adapter.TextBuilder;
import com.ztl.mallchat.common.user.service.adapter.UserAdapter;
import com.ztl.mallchat.common.websocket.service.WebsocketService;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.bean.WxOAuth2UserInfo;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.jws.soap.SOAPBinding;
import java.net.URLEncoder;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Description:
 * Author:  kevin
 * Date:  2023/09/10
 */
@Service
@Slf4j
public class WXMsgServiceImpl implements WXMsgService {

    @Autowired
    private WebsocketService websocketService;

    /**
     * openid和登录code的关系map
     */
    private static final ConcurrentHashMap<String,Integer> WAIT_AUTHORIZE_MAP = new ConcurrentHashMap<>();

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
        User user = userDao.getByOpenId(openId);
        // 这里双重效验就是为了避免当用户扫码后一直没有点击授权，那么也是登录失败的
        if (Objects.nonNull(user) && StringUtils.isNotEmpty(user.getAvatar())) {
            return null;
        }
        //user为空先注册,手动生成,以保存uid
        if (Objects.isNull(user)) {
            User user1 = UserAdapter.buildUser(openId);
            userService.register(user1);
        }

        // 推送链接给用户授权
        WAIT_AUTHORIZE_MAP.put(openId,code);
        // 用户扫码成功但是没有授权，向前端推送一个等待授权的信息
        websocketService.waitAuthorize(code);
        // 根据官方文档需要重定向到到指定的URL， 通过定向到/callback
        String skipUrl = String.format(URL, wxMpService.getWxMpConfigStorage().getAppId(), URLEncoder.encode(callback + "/wx/portal/public/callBack"));
        return new TextBuilder().build("请点击链接授权：<a href=\"" + skipUrl + "\">登录</a>", wxMpXmlMessage, wxMpService);
    }

    /**
     * 用户授权登录具体逻辑
     * @param userInfo
     */
    @Override
    public void authorize(WxOAuth2UserInfo userInfo) {
        String openid = userInfo.getOpenid();
        User user = userDao.getByOpenId(openid);
        // 先更新用户信息
        if(StrUtil.isBlank(user.getAvatar())){
            fillUserInfo(user.getId(),userInfo);
        }
        // 通过openid从map中找到对应的code,然后再通过code找打用户注册的channel，进行登录
        Integer code = WAIT_AUTHORIZE_MAP.remove(openid);
        websocketService.scanLoginSuccess(code,user.getId());

    }

    private void fillUserInfo(Long uid, WxOAuth2UserInfo userInfo) {
        User user = UserAdapter.buildAuthorizeUser(uid, userInfo);
        userDao.updateById(user);
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
