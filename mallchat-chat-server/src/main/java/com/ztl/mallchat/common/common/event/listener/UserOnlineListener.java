package com.ztl.mallchat.common.common.event.listener;

import com.ztl.mallchat.common.common.event.UserOnlineEvent;
import com.ztl.mallchat.common.common.event.UserRegisterEvent;
import com.ztl.mallchat.common.user.dao.UserDao;
import com.ztl.mallchat.common.user.domain.entity.User;
import com.ztl.mallchat.common.user.domain.enums.IdempotentEnum;
import com.ztl.mallchat.common.user.domain.enums.ItemEnum;
import com.ztl.mallchat.common.user.domain.enums.UserActiveStatusEnum;
import com.ztl.mallchat.common.user.service.IUserBackpackService;
import com.ztl.mallchat.common.user.service.IpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Description:
 * Author:  kevin
 * Date:  2023/09/21
 */
@Component
public class UserOnlineListener {
    @Autowired
    private IpService ipService;
    @Autowired
    private UserDao userDao;

    /**
     * @Async : 异步执行
     * @TransactionalEventListener： 配置执行的顺序，事务提交前还是后执行
     */
    @Async
    @EventListener(classes = UserOnlineEvent.class)
    public void saveIpInfoToDb(UserOnlineEvent event){
        User user = event.getUser();
        User update = new User();
        update.setId(user.getId());
        update.setLastOptTime(user.getLastOptTime());
        update.setIpInfo(user.getIpInfo());
        update.setStatus(UserActiveStatusEnum.ONLINE.getStatus());
        userDao.updateById(update);
        // 用户解析ip
        ipService.refreshIpDetailsAsync(user.getId());
    }
}
