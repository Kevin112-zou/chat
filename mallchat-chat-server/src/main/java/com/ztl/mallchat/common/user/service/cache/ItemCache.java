package com.ztl.mallchat.common.user.service.cache;

import com.ztl.mallchat.common.user.dao.ItemConfigDao;
import com.ztl.mallchat.common.user.domain.entity.ItemConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ItemCache {

    @Autowired
    private ItemConfigDao itemConfigDao;
    /**
     * 从缓存中获取徽章
     */
    @Cacheable(cacheNames = "item",key = "'itemsByType:' + #itemType") //先从缓存中获取数据，获取不到才会执行查询sql
    public List<ItemConfig> getByType(Integer itemType){
        return itemConfigDao.getByType(itemType);
    }

    @CacheEvict(cacheNames = "item",key = "'itemsByType:' + #itemType") // 清空缓存
    public void evictByType(Integer itemType){

    }
}
