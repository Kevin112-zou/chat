package com.ztl.mallchat.common.user.dao;

import com.ztl.mallchat.common.user.domain.entity.User;
import com.ztl.mallchat.common.user.mapper.UserMapper;
import com.ztl.mallchat.common.user.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author kevin
 * @since 2023-09-08
 */
@Service
public class UserDao extends ServiceImpl<UserMapper, User> {

}
