package com.ztl.mallchat.common.user.service;

/**
 * Description:
 * Author:  kevin
 * Date:  2023/09/11
 */
public interface LoginService {
    /**
     * 登录成功，获取token
     *
     * @param uid
     * @return 返回token
     */
    String getLoginToken(Long uid);


    /**
     * 校验token是不是有效
     *
     * @param token
     * @return
     */
    boolean verify(String token);

    /**
     * 刷新token有效期
     *
     * @param token
     */
    void renewalTokenIfNecessary(String token);



    /**
     * 如果token有效，返回uid
     *
     * @param token
     * @return
     */
    Long getValidUid(String token);
}
