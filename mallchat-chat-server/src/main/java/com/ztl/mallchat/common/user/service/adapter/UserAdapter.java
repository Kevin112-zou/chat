package com.ztl.mallchat.common.user.service.adapter;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
//import com.abin.mallchat.common.common.domain.enums.YesOrNoEnum;
//import com.abin.mallchat.common.user.domain.entity.ItemConfig;
//import com.abin.mallchat.common.user.domain.entity.User;
//import com.abin.mallchat.common.user.domain.entity.UserBackpack;
//import com.abin.mallchat.common.user.domain.vo.response.user.BadgeResp;
//import com.abin.mallchat.common.user.domain.vo.response.user.UserInfoResp;
import com.ztl.mallchat.common.common.enums.YesOrNo;
import com.ztl.mallchat.common.user.domain.entity.ItemConfig;
import com.ztl.mallchat.common.user.domain.entity.User;
import com.ztl.mallchat.common.user.domain.entity.UserBackpack;
import com.ztl.mallchat.common.user.domain.vo.resp.user.BadgeResp;
import com.ztl.mallchat.common.user.domain.vo.resp.user.UserInfoResp;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.bean.WxOAuth2UserInfo;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Description: 用户适配器
 * Author: <a href="https://github.com/zongzibinbin">abin</a>
 * Date: 2023-03-19
 */
@Slf4j
public class UserAdapter {

    public static User buildUser(String openId) {
        User user = new User();
        user.setOpenId(openId);
        return user;
    }

    public static User buildAuthorizeUser(Long id, WxOAuth2UserInfo userInfo) {
        User user = new User();
        user.setId(id);
        user.setAvatar(userInfo.getHeadImgUrl());
        user.setName(userInfo.getNickname());
        user.setSex(userInfo.getSex());
        if (userInfo.getNickname().length() > 6) {
            user.setName("名字过长" + RandomUtil.randomInt(100000));
        } else {
            user.setName(userInfo.getNickname());
        }
        return user;
    }

    public static UserInfoResp buildUserInfo(User user, Integer countByValidItemId) {
        UserInfoResp userInfoResp = new UserInfoResp();
        BeanUtil.copyProperties(user,userInfoResp);
        userInfoResp.setModifyNameChance(countByValidItemId);
        return userInfoResp;
    }


    public static List<BadgeResp> buildBadgeResp(List<ItemConfig> itemConfigs, List<UserBackpack> backpacks, User user) {
        if (ObjectUtil.isNull(user)) {
            // 这里 user 入参可能为空，防止 NPE 问题
            return Collections.emptyList();
        }

        Set<Long> obtainItemSet = backpacks.stream().map(UserBackpack::getItemId).collect(Collectors.toSet());
        return itemConfigs.stream().map(a -> {
            BadgeResp resp = new BadgeResp();
            BeanUtil.copyProperties(a, resp);
            resp.setObtain(obtainItemSet.contains(a.getId()) ? YesOrNo.YES.getStatus() : YesOrNo.NO.getStatus());
            resp.setWearing(ObjectUtil.equal(a.getId(), user.getItemId()) ? YesOrNo.YES.getStatus() : YesOrNo.NO.getStatus());
            return resp;
        }).sorted(Comparator.comparing(BadgeResp::getWearing, Comparator.reverseOrder()) // 排序规则，佩戴》拥有》未拥有
                .thenComparing(BadgeResp::getObtain, Comparator.reverseOrder()))
                .collect(Collectors.toList());
    }
}
