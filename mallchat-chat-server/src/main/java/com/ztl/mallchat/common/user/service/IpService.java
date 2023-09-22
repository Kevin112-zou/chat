package com.ztl.mallchat.common.user.service;

/**
 * Description:
 * Author:  kevin
 * Date:  2023/09/22
 */
public interface IpService {
    void refreshIpDetailsAsync(Long uid);

    void destroy() throws InterruptedException;
}
