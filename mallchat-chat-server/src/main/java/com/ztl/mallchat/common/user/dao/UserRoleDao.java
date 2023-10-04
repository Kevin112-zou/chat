package com.ztl.mallchat.common.user.dao;

import com.ztl.mallchat.common.user.domain.entity.UserRole;
import com.ztl.mallchat.common.user.mapper.UserRoleMapper;
import com.ztl.mallchat.common.user.service.IUserRoleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 用户角色关系表 服务实现类
 * </p>
 *
 * @author kevin
 * @since 2023-10-04
 */
@Service
public class UserRoleDao extends ServiceImpl<UserRoleMapper, UserRole> implements IUserRoleService {

    public List<UserRole> listByUid(Long uid) {
        return lambdaQuery()
                .eq(UserRole::getUid,uid)
                .list();
    }
}
