package com.ztl.mallchat.common.user.service;


import com.ztl.mallchat.common.user.domain.enums.RoleEnum;

/**
 * <p>
 * 角色表 服务类
 * </p>
 *
 * @author kevin
 * @since 2023-10-04
 */
public interface IRoleService{
    /**
     * 判断当前用户是否有权限
     */
    boolean hasPower(Long uid, RoleEnum roleEnum);
}
