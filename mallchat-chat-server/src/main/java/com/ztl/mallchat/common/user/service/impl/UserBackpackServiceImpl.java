package com.ztl.mallchat.common.user.service.impl;

import com.ztl.mallchat.common.common.enums.YesOrNo;
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
    private RedissonClient redissonClient;
    @Autowired
    private UserBackpackDao userBackpackDao;
    @Override
    public void acquireItem(Long uid, Long itemId, IdempotentEnum idempotentEnum, String businessId) {
        // 1. 组转幂等键
        String idempotent = getIdempotent(itemId, idempotentEnum, businessId);
        // 2. 加分布式锁
        RLock lock = redissonClient.getLock("acquireItem" + idempotent);
        boolean b = lock.tryLock();
        // 3. 判断是否加锁成功
        AssertUtil.isTrue(b,"请求太频繁了！！！");
        try{
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
        }finally {
            lock.unlock();
        }

    }

    private String getIdempotent(Long itemId, IdempotentEnum idempotentEnum, String businessId) {
        return String.format("%d_%d_%s",itemId,idempotentEnum.getType(),businessId);
    }
}
