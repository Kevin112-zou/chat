package com.ztl.mallchat.common.user.service.impl;

import com.ztl.mallchat.common.common.annotation.RedissonLock;
import com.ztl.mallchat.common.common.enums.YesOrNo;
import com.ztl.mallchat.common.common.event.UserBlackEvent;
import com.ztl.mallchat.common.common.event.UserRegisterEvent;
import com.ztl.mallchat.common.common.event.listener.UserBlackListener;
import com.ztl.mallchat.common.common.exception.BusinessException;
import com.ztl.mallchat.common.common.utils.AssertUtil;
import com.ztl.mallchat.common.user.dao.BlackDao;
import com.ztl.mallchat.common.user.dao.ItemConfigDao;
import com.ztl.mallchat.common.user.dao.UserBackpackDao;
import com.ztl.mallchat.common.user.dao.UserDao;
import com.ztl.mallchat.common.user.domain.entity.*;
import com.ztl.mallchat.common.user.domain.enums.BlackTypeEnum;
import com.ztl.mallchat.common.user.domain.enums.ItemEnum;
import com.ztl.mallchat.common.user.domain.enums.ItemTypeEnum;
import com.ztl.mallchat.common.user.domain.vo.req.user.BlackReq;
import com.ztl.mallchat.common.user.domain.vo.req.user.WearingBadgeReq;
import com.ztl.mallchat.common.user.domain.vo.resp.user.BadgeResp;
import com.ztl.mallchat.common.user.domain.vo.resp.user.UserInfoResp;
import com.ztl.mallchat.common.user.service.IUserService;
import com.ztl.mallchat.common.user.service.adapter.UserAdapter;
import com.ztl.mallchat.common.user.service.cache.ItemCache;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.index.qual.SameLenUnknown;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Description:
 * Author:  kevin
 * Date:  2023/09/10
 */
@Service
@Slf4j
public class UserServiceImpl implements IUserService {

    @Autowired
    private UserDao userDao;
    @Autowired
    private UserBackpackDao userBackpackDao;
    @Autowired
    private ItemCache itemCache;
    @Autowired
    private ItemConfigDao itemConfigDao;
    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;
    @Autowired
    private BlackDao blackDao;
    @Override
    public void register(User user) {
        userDao.save(user);
        //用户注册事件
        applicationEventPublisher.publishEvent(new UserRegisterEvent(this,user));
    }

    @Override
    public UserInfoResp getUserInfo(Long uid) {
        User user = userDao.getById(uid);

        Integer countByValidItemId = userBackpackDao.getCountByValidItemId(uid, ItemEnum.MODIFY_NAME_CARD.getId());
        return UserAdapter.buildUserInfo(user,countByValidItemId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)  // 保证事务对所有异常都生效
    @RedissonLock(key = "#uid")
    public void modifyName(Long uid, String name) {
        User oldUser = userDao.getByName(name);
        if(Objects.nonNull(oldUser)){
            throw new BusinessException("名字已经存在，不能重复哦");
        }
        // 获取最老的一张改名卡
        UserBackpack modifyItem =  userBackpackDao.getFirstValidItem(uid,ItemEnum.MODIFY_NAME_CARD.getId());
        if(Objects.nonNull(modifyItem)){
            // 使用改名卡
            boolean success =  userBackpackDao.userItem(modifyItem);
            if(success){
                // 进行改名
                userDao.modifyName(uid,name);
            }
        }else {
            throw new BusinessException("改名卡不够啦，等待后续活动哦~~~");
        }

    }

    @Override
    public List<BadgeResp> badges(Long uid) {
        // 查询所有的徽章
        List<ItemConfig> itemConfigs = itemCache.getByType(ItemTypeEnum.BADGE.getType());
        // 查询用户所拥有的徽章
        List<Long> itemIds = itemConfigs.stream().map(ItemConfig::getId).collect(Collectors.toList());
        List<UserBackpack> userBackpacks =  userBackpackDao.getByItemIds(uid,itemIds);
        // 查询用户当前佩戴的徽章
        User user = userDao.getById(uid);
        return UserAdapter.buildBadgeResp(itemConfigs,userBackpacks,user);
    }

    @Override
    public void wearingBadge(Long uid, WearingBadgeReq req) {
        // 确保有徽章
        UserBackpack firstValidItem = userBackpackDao.getFirstValidItem(uid, req.getBadgeId());
        AssertUtil.isNotEmpty(firstValidItem,"您还没有这个徽章哦，快去获去吧~~");
        // 确保这个物品时徽章
        ItemConfig itemConfig = itemConfigDao.getById(firstValidItem.getItemId());
        AssertUtil.equal(itemConfig.getType(),ItemTypeEnum.BADGE.getType(),"只有徽章才能佩戴哦~~~");
        userDao.wearingBadge(uid,req.getBadgeId());
    }

    @Override
    public void black(BlackReq req) {
        // 设置拉黑的uid
        Long uid = req.getUid();
        Black user = new Black();
        user.setType(BlackTypeEnum.UID.getType());
        user.setTarget(req.getUid().toString());
        blackDao.save(user);
        // 设置拉黑ip
        User byId = userDao.getById(uid);
        // 防止ip没有出现空指针异常
        blackIp(Optional.ofNullable(byId.getIpInfo()).map(IpInfo::getCreateIp).orElse(null));
        blackIp(Optional.ofNullable(byId.getIpInfo()).map(IpInfo::getUpdateIp).orElse(null));
        // 将拉黑用户推送给前端
        applicationEventPublisher.publishEvent(new UserBlackEvent(this,byId));

    }

    private void blackIp(String ip) {
        if(StringUtils.isBlank(ip)){
            return;
        }
        try {
            Black user = new Black();
            user.setTarget(ip);
            user.setType(BlackTypeEnum.IP.getType());
            blackDao.save(user);
        } catch (Exception e) {
            log.error("duplicate black ip:{}", ip);
        }
    }
}
