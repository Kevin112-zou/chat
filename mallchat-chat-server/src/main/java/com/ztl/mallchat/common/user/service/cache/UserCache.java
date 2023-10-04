package com.ztl.mallchat.common.user.service.cache;

import com.ztl.mallchat.common.user.dao.ItemConfigDao;
import com.ztl.mallchat.common.user.dao.UserRoleDao;
import com.ztl.mallchat.common.user.domain.entity.ItemConfig;
import com.ztl.mallchat.common.user.domain.entity.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class UserCache {
    @Autowired
    private UserRoleDao userRoleDao;
    /**
     * 从缓存中获取权限信息
     */
    @CacheEvict(cacheNames = "user",key = "'roles:' + #uid")
    public Set<Long> getRoleSet(Long uid){
        List<UserRole> userRoles = userRoleDao.listByUid(uid);
        Set<Long> collect = userRoles.stream()
                .map(UserRole::getRoleId)
                .collect(Collectors.toSet());
        return collect;
    }
}
