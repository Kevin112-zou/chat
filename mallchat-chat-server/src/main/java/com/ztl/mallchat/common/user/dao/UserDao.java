package com.ztl.mallchat.common.user.dao;

import com.ztl.mallchat.common.common.enums.YesOrNo;
import com.ztl.mallchat.common.user.domain.entity.Black;
import com.ztl.mallchat.common.user.domain.entity.User;
import com.ztl.mallchat.common.user.mapper.UserMapper;
import com.ztl.mallchat.common.user.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author kevin
 * @since 2023-09-08
 */
@Service
public class UserDao extends ServiceImpl<UserMapper, User> {

    public User getByOpenId(String openId) {
        return lambdaQuery()
                .eq(User::getOpenId, openId)
                .one();
    }

    public User getByName(String name) {
        return lambdaQuery()
                .eq(User::getName, name)
                .one();
    }

    public void modifyName(Long uid, String name) {
        lambdaUpdate()
                .eq(User::getId,uid)
                .set(User::getName,name)
                .update();
    }

    public void wearingBadge(Long uid, Long badgeId) {
        lambdaUpdate()
                .eq(User::getId, uid)
                .eq(User::getItemId, badgeId)
                .update();
    }

    public void invalidUid(Long id) {
        lambdaUpdate()
                .eq(User::getId,id)
                .set(User::getStatus, YesOrNo.YES.getStatus())
                .update();
    }
}
