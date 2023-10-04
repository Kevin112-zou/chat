package com.ztl.mallchat.common.user.service;

import cn.hutool.system.UserInfo;
import com.ztl.mallchat.common.user.domain.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ztl.mallchat.common.user.domain.vo.req.user.BlackReq;
import com.ztl.mallchat.common.user.domain.vo.req.user.WearingBadgeReq;
import com.ztl.mallchat.common.user.domain.vo.resp.user.BadgeResp;
import com.ztl.mallchat.common.user.domain.vo.resp.user.UserInfoResp;

import java.util.List;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author kevin
 * @since 2023-09-08
 */
public interface IUserService {

    /**
     * 用户注册
     */
    void register(User user1);

    /**
     * 获取用户信息
     */
    UserInfoResp getUserInfo(Long uid);

    /**
     * 使用改名卡修改名称
     */
    void modifyName(Long uid,String name);

    /**
     * 获取徽章
     */
    List<BadgeResp> badges(Long uid);

    void wearingBadge(Long uid, WearingBadgeReq req);

    void black(BlackReq req);

}
