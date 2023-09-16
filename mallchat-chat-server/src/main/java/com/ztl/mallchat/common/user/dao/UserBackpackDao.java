package com.ztl.mallchat.common.user.dao;

import com.ztl.mallchat.common.common.enums.YesOrNo;
import com.ztl.mallchat.common.user.domain.entity.UserBackpack;
import com.ztl.mallchat.common.user.mapper.UserBackpackMapper;
import com.ztl.mallchat.common.user.service.IUserBackpackService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户背包表 服务实现类
 * </p>
 *
 * @author kevin
 * @since 2023-09-15
 */
@Service
public class UserBackpackDao extends ServiceImpl<UserBackpackMapper, UserBackpack>{

    public Integer getCountByValidItemId(Long uid, Long id) {
        return lambdaQuery()
                .eq(UserBackpack::getUid, uid)
                .eq(UserBackpack::getItemId, id)
                .eq(UserBackpack::getStatus, YesOrNo.NO.getStatus())
                .count();
    }

    public UserBackpack getFirstValidItem(Long uid, Long itemId) {
        return lambdaQuery()
                .eq(UserBackpack::getUid, uid)
                .eq(UserBackpack::getItemId, itemId)
                .eq(UserBackpack::getStatus, YesOrNo.NO.getStatus())
                .orderByAsc(UserBackpack::getId)
                .last("limit 1")
                .one();
    }

    public boolean userItem(UserBackpack modifyItem) {
        return lambdaUpdate()
                .eq(UserBackpack::getUid, modifyItem.getId())
                .eq(UserBackpack::getStatus, YesOrNo.NO.getStatus())
                .set(UserBackpack::getStatus, YesOrNo.YES.getStatus())
                .update();
    }
}
