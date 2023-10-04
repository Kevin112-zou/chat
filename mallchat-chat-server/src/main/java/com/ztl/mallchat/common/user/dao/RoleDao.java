package com.ztl.mallchat.common.user.dao;

import com.ztl.mallchat.common.user.domain.entity.Role;
import com.ztl.mallchat.common.user.mapper.RoleMapper;
import com.ztl.mallchat.common.user.service.IRoleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 角色表 服务实现类
 * </p>
 *
 * @author kevin
 * @since 2023-10-04
 */
@Service
public class RoleDao extends ServiceImpl<RoleMapper, Role>{

}
