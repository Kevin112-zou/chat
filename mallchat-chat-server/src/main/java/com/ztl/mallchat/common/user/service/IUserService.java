package com.ztl.mallchat.common.user.service;

import cn.hutool.system.UserInfo;
import com.ztl.mallchat.common.user.domain.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ztl.mallchat.common.user.domain.vo.resp.user.UserInfoResp;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author kevin
 * @since 2023-09-08
 */
public interface IUserService {

    Long register(User user1);

    UserInfoResp getUserInfo(Long uid);

    void modifyName(Long uid,String name);
}
