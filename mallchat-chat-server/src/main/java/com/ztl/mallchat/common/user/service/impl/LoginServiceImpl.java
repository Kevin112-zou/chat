package com.ztl.mallchat.common.user.service.impl;

import com.ztl.mallchat.common.common.constant.RedisKey;
import com.ztl.mallchat.common.common.utils.JwtUtils;
import com.ztl.mallchat.common.common.utils.RedisUtils;
import com.ztl.mallchat.common.user.service.LoginService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigurationPackage;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.concurrent.TimeUnit;


/**
 * Description:
 * Author:  kevin
 * Date:  2023/09/11
 */
@Service
public class LoginServiceImpl implements LoginService {
    public static final int TOKEN_EXPIRE_DAYS = 3;
    public static final int TOKEN_RENEWAL_DAYS = 1;
    @Autowired
    private JwtUtils jwtUtils;

    @Override
    public String getLoginToken(Long uid) {
        String token = jwtUtils.createToken(uid);
        // 将token保存到redis中
        RedisUtils.set(RedisKey.getKey(RedisKey.USER_TOKEN_STRING,uid), token, TOKEN_EXPIRE_DAYS, TimeUnit.DAYS);
        return token;

    }

    @Override
    public boolean verify(String token) {
        return false;
    }

    /**
     * @Async 注解是Spring Framework中的一个注解，用于支持异步方法的执行。会将任务交给spring的线程池管理
     * 它允许你将一个普通的Java方法标记为异步方法，使该方法可以在后台线程中执行，而不会阻塞调用它的线程。
     * 这对于处理需要一些时间来完成的任务，如长时间的计算、远程调用或I/O操作非常有用，因为它可以提高应用程序的性能和响应性。
     */
    @Override
    @Async
    public void renewalTokenIfNecessary(String token) {
        Long uid = getValidUid(token);
        String userTokenKey = getUserTokenKey(uid);
        Long expireDays = RedisUtils.getExpire(userTokenKey, TimeUnit.DAYS);
        if(expireDays == -2){ // 等于-2的话，表示不存在的key
            return;
        }
        // 如果过期时间小于1day的话，就刷新token的有效期
        if(expireDays < TOKEN_RENEWAL_DAYS){
            RedisUtils.set(RedisKey.getKey(RedisKey.USER_TOKEN_STRING,uid), token, TOKEN_EXPIRE_DAYS, TimeUnit.DAYS);
        }

    }

    @Override
    public Long getValidUid(String token) {
        Long uid = jwtUtils.getUidOrNull(token);
        if(Objects.isNull(uid)){
            return null;
        }
        //***修改bug不能用get()方法去解析字符串类型的数据要不然会带上引号***
        String oldToken = RedisUtils.getStr(getUserTokenKey(uid));
        // 这里为了避免oldToken被newToken刷新了，但是依然拿着老的token再解析出uid，做了一层比较
        return Objects.equals(oldToken,token)? uid : null;
    }

    private String getUserTokenKey(Long uid){
        return RedisKey.getKey(RedisKey.USER_TOKEN_STRING, uid);
    }
}
