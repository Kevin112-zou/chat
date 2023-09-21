package com.ztl.mallchat.common.user.service.impl;

import com.ztl.mallchat.common.common.annotation.RedissonLock;
import com.ztl.mallchat.common.common.enums.YesOrNo;
import com.ztl.mallchat.common.common.service.LockService;
import com.ztl.mallchat.common.common.utils.AssertUtil;
import com.ztl.mallchat.common.user.dao.UserBackpackDao;
import com.ztl.mallchat.common.user.domain.entity.UserBackpack;
import com.ztl.mallchat.common.user.domain.enums.IdempotentEnum;
import com.ztl.mallchat.common.user.service.IUserBackpackService;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.client.RedisClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * Description:
 * Author:  kevin
 * Date:  2023/09/20
 */
@Service
public class UserBackpackServiceImpl implements IUserBackpackService {
    @Autowired
    private LockService lockService;
    @Autowired
    private UserBackpackDao userBackpackDao;
    @Autowired
    @Lazy
    private UserBackpackServiceImpl userBackpackService;
    @Override
    public void acquireItem(Long uid, Long itemId, IdempotentEnum idempotentEnum, String businessId) {
        // 1. 组装幂等键
        String idempotent = getIdempotent(itemId, idempotentEnum, businessId);
        userBackpackService.doAcquireItem(uid,itemId,idempotent);
    }

    @RedissonLock(key = "#idempotent",waitTime = 5000)
    public void doAcquireItem(Long uid,Long itemId,String idempotent){
        // 幂等号检查
        UserBackpack userBackpack = userBackpackDao.getIdempotent(idempotent);
        if(Objects.nonNull(userBackpack)){
            // 如果数据库中已经有这个幂等号说明已经发放成功了，直接返回即可
            return;
        }
        // todo 业务处理
        // 发放物品
        UserBackpack build = UserBackpack.builder()
                .uid(uid)
                .itemId(itemId)
                .idempotent(idempotent)
                .status(YesOrNo.NO.getStatus())
                .build();
        userBackpackDao.save(build);
    }
    private String getIdempotent(Long itemId, IdempotentEnum idempotentEnum, String businessId) {
        return String.format("%d_%d_%s",itemId,idempotentEnum.getType(),businessId);
    }
}
