package com.ztl.mallchat.common.user.service.impl;

import com.ztl.mallchat.common.user.dao.RoleDao;
import com.ztl.mallchat.common.user.dao.UserRoleDao;
import com.ztl.mallchat.common.user.domain.enums.RoleEnum;
import com.ztl.mallchat.common.user.mapper.RoleMapper;
import com.ztl.mallchat.common.user.service.IRoleService;
import com.ztl.mallchat.common.user.service.cache.UserCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Set;

/**
 * Description:
 * Author:  kevin
 * Date:  2023/10/04
 */
@Service
public class RoleServiceImpl implements IRoleService {
    @Autowired
    private UserCache userCache;
    @Override
    public boolean hasPower(Long uid, RoleEnum roleEnum) {
        Set<Long> roleSet = userCache.getRoleSet(uid);
        boolean contains = roleSet.contains(RoleEnum.CHAT_MANAGER.getId());
        return isAdmin(roleSet) || contains;
    }
    private boolean isAdmin(Set<Long> roleSet){
        return Objects.requireNonNull(roleSet).contains(RoleEnum.ADMIN.getId());
    }
}
