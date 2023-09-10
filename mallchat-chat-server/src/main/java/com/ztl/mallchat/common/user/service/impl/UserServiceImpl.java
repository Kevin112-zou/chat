package com.ztl.mallchat.common.user.service.impl;

import com.ztl.mallchat.common.user.dao.UserDao;
import com.ztl.mallchat.common.user.domain.entity.User;
import com.ztl.mallchat.common.user.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Description:
 * Author:  kevin
 * Date:  2023/09/10
 */
@Service
public class UserServiceImpl implements IUserService {

    @Autowired
    private UserDao userDao;
    @Override
    @Transactional
    public Long register(User user1) {
        userDao.save(user1);
        // todo 用户注册事件
        return user1.getId();
    }
}
