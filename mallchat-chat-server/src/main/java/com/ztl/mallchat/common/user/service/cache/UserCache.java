package com.ztl.mallchat.common.user.service.cache;

import com.ztl.mallchat.common.user.dao.BlackDao;
import com.ztl.mallchat.common.user.dao.ItemConfigDao;
import com.ztl.mallchat.common.user.dao.UserRoleDao;
import com.ztl.mallchat.common.user.domain.entity.Black;
import com.ztl.mallchat.common.user.domain.entity.ItemConfig;
import com.ztl.mallchat.common.user.domain.entity.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class UserCache {
    @Autowired
    private UserRoleDao userRoleDao;
    @Autowired
    private BlackDao blackDao;
    /**
     * 从缓存中获取权限信息
     */
    @Cacheable(cacheNames = "user",key = "'roles:' + #uid")
    public Set<Long> getRoleSet(Long uid){
        List<UserRole> userRoles = userRoleDao.listByUid(uid);
        Set<Long> collect = userRoles.stream()
                .map(UserRole::getRoleId)
                .collect(Collectors.toSet());
        return collect;
    }

    /**
     * 从缓存中获取到所有拉黑的用户uid和ip
     */
    @Cacheable(cacheNames = "user",key = "'userBlackList'")
    public Map<Integer,Set<String>> getBlackMap() {
        Map<Integer, List<Black>> collect = blackDao.list().stream().collect(Collectors.groupingBy(Black::getType));
        Map<Integer,Set<String>> result = new HashMap<>();
        collect.forEach((type,list)->{
            result.put(type,list.stream().map(Black::getTarget).collect(Collectors.toSet()));
        });
        return result;
    }

    /**
     * 清理缓存
     */
    @CacheEvict(cacheNames = "user",key = "'userBlackList'")
    public Map<Integer,Set<String>> evictBlackMap() {
        return null;
    }
}
