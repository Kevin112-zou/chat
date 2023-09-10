package com.ztl.mallchat.common.user.config;


import com.ztl.mallchat.common.user.service.handler.LogHandler;
import com.ztl.mallchat.common.user.service.handler.MsgHandler;
import com.ztl.mallchat.common.user.service.handler.ScanHandler;
import com.ztl.mallchat.common.user.service.handler.SubscribeHandler;
import lombok.AllArgsConstructor;
import me.chanjar.weixin.mp.api.WxMpMessageRouter;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.api.impl.WxMpServiceImpl;
import me.chanjar.weixin.mp.config.impl.WxMpDefaultConfigImpl;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.stream.Collectors;

import static me.chanjar.weixin.common.api.WxConsts.EventType;
import static me.chanjar.weixin.common.api.WxConsts.EventType.SUBSCRIBE;
import static me.chanjar.weixin.common.api.WxConsts.XmlMsgType.EVENT;

/**
 * wechat mp configuration
 *
 * @author <a href="https://github.com/binarywang">Binary Wang</a>
 */
@AllArgsConstructor
@Configuration
@EnableConfigurationProperties(WxMpProperties.class)
public class WxMpConfiguration {
    private final LogHandler logHandler;
    private final MsgHandler msgHandler;
    private final SubscribeHandler subscribeHandler;
    private final ScanHandler scanHandler;
    private final WxMpProperties properties;

    @Bean
    public WxMpService wxMpService() {
        // 从配置属性中获取微信公众号配置信息列表
        final List<WxMpProperties.MpConfig> configs = this.properties.getConfigs();

        // 如果配置信息为空，抛出运行时异常提醒用户配置相关信息
        if (configs == null) {
            throw new RuntimeException("大哥，拜托先看下项目首页的说明（readme文件），添加下相关配置，注意别配错了！");
        }

        // 创建一个微信公众号服务对象
        WxMpService service = new WxMpServiceImpl();

        // 设置多个微信公众号配置信息并存储在multiConfigStorages属性中
        service.setMultiConfigStorages(configs
                .stream().map(a -> {
                    WxMpDefaultConfigImpl configStorage;
                    configStorage = new WxMpDefaultConfigImpl();

                    // 设置各个配置信息
                    configStorage.setAppId(a.getAppId());
                    configStorage.setSecret(a.getSecret());
                    configStorage.setToken(a.getToken());
                    configStorage.setAesKey(a.getAesKey());

                    return configStorage;
                }).collect(Collectors.toMap(WxMpDefaultConfigImpl::getAppId, a -> a, (o, n) -> o)));

        // 返回创建的微信公众号服务对象作为Spring Bean
        return service;
    }


    @Bean
    public WxMpMessageRouter messageRouter(WxMpService wxMpService) {
        final WxMpMessageRouter newRouter = new WxMpMessageRouter(wxMpService);

        // 记录所有事件的日志 （异步执行）
        newRouter.rule().handler(this.logHandler).next();

        // 关注事件
        newRouter.rule().async(false).msgType(EVENT).event(SUBSCRIBE).handler(this.subscribeHandler).end();

        // 扫码事件
        newRouter.rule().async(false).msgType(EVENT).event(EventType.SCAN).handler(this.scanHandler).end();

        // 默认
        newRouter.rule().async(false).handler(this.msgHandler).end();

        return newRouter;
    }

}
