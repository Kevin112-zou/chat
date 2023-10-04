package com.ztl.mallchat.common.common.interceptor;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.extra.servlet.ServletUtil;
import com.ztl.mallchat.common.common.domain.dto.RequestInfo;
import com.ztl.mallchat.common.common.exception.HttpErrorEnum;
import com.ztl.mallchat.common.common.utils.RequestHolder;
import com.ztl.mallchat.common.user.domain.enums.BlackTypeEnum;
import com.ztl.mallchat.common.user.service.cache.UserCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Component
public class BlackInterceptor implements HandlerInterceptor {


    @Autowired
    private UserCache userCache;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 从缓存中拿到黑名单列表
        Map<Integer, Set<String>> blackSet = userCache.getBlackMap();
        // 从RequestHolder拿到当前的uid
        RequestInfo requestInfo = RequestHolder.get();

        boolean uidInBlackList = inBlackList(requestInfo.getUid(), blackSet.get(BlackTypeEnum.UID.getType()));
        if(uidInBlackList){
            HttpErrorEnum.ACCESS_DENIED.sendHttpError(response);
            return false;
        }
        boolean ipInBlackList = inBlackList(requestInfo.getIp(), blackSet.get(BlackTypeEnum.IP.getType()));
        if(ipInBlackList){
            HttpErrorEnum.ACCESS_DENIED.sendHttpError(response);
            return false;
        }
        return true;
    }

    private boolean inBlackList(Object target, Set<String> set) {
        if(Objects.isNull(target) || CollectionUtil.isEmpty(set)){
            return false;
        }
        return set.contains(target.toString());
    }

}
