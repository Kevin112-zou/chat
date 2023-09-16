package com.ztl.mallchat.common.user.service.impl;

import com.ztl.mallchat.common.common.exception.BusinessException;
import com.ztl.mallchat.common.user.dao.UserBackpackDao;
import com.ztl.mallchat.common.user.dao.UserDao;
import com.ztl.mallchat.common.user.domain.entity.User;
import com.ztl.mallchat.common.user.domain.entity.UserBackpack;
import com.ztl.mallchat.common.user.domain.enums.ItemEnum;
import com.ztl.mallchat.common.user.domain.vo.resp.user.UserInfoResp;
import com.ztl.mallchat.common.user.service.IUserService;
import com.ztl.mallchat.common.user.service.adapter.UserAdapter;
import org.checkerframework.checker.index.qual.SameLenUnknown;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

/**
 * Description:
 * Author:  kevin
 * Date:  2023/09/10
 */
@Service
public class UserServiceImpl implements IUserService {

    @Autowired
    private UserDao userDao;
    @Autowired
    private UserBackpackDao userBackpackDao;
    @Override
    @Transactional
    public Long register(User user1) {
        userDao.save(user1);
        // todo 用户注册事件
        return user1.getId();
    }

    @Override
    public UserInfoResp getUserInfo(Long uid) {
        User user = userDao.getById(uid);

        Integer countByValidItemId = userBackpackDao.getCountByValidItemId(uid, ItemEnum.MODIFY_NAME_CARD.getId());
        return UserAdapter.buildUserInfo(user,countByValidItemId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)  // 保证事务对所有异常都生效
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
}
