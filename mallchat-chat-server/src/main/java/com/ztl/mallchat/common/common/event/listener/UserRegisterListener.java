package com.ztl.mallchat.common.common.event.listener;

import com.ztl.mallchat.common.common.event.UserRegisterEvent;
import com.ztl.mallchat.common.user.domain.entity.User;
import com.ztl.mallchat.common.user.domain.enums.IdempotentEnum;
import com.ztl.mallchat.common.user.domain.enums.ItemEnum;
import com.ztl.mallchat.common.user.domain.enums.ItemTypeEnum;
import com.ztl.mallchat.common.user.service.IUserBackpackService;
import org.checkerframework.checker.units.qual.A;
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
public class UserRegisterListener {
    @Autowired
    private IUserBackpackService userBackpackService;

    /**
     * @Async : 异步执行
     * @TransactionalEventListener： 配置执行的顺序，事务提交前还是后执行
     */
    @Async
    @EventListener(classes = UserRegisterEvent.class)
    public void sendCard(UserRegisterEvent event){
        User user = event.getUser();
        userBackpackService.acquireItem(user.getId(), ItemEnum.MODIFY_NAME_CARD.getId(), IdempotentEnum.UID,user.getId().toString());
    }
}
