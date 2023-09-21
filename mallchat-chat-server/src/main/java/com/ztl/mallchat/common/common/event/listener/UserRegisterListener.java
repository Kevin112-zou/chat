package com.ztl.mallchat.common.common.event.listener;

import com.ztl.mallchat.common.common.event.UserRegisterEvent;
import com.ztl.mallchat.common.user.domain.entity.User;
import com.ztl.mallchat.common.user.domain.enums.IdempotentEnum;
import com.ztl.mallchat.common.user.domain.enums.ItemEnum;
import com.ztl.mallchat.common.user.domain.enums.ItemTypeEnum;
import com.ztl.mallchat.common.user.service.IUserBackpackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Description:
 * Author:  kevin
 * Date:  2023/09/21
 */
@Component
public class UserRegisterListener {
    @Autowired
    private IUserBackpackService userBackpackService;

    @EventListener(classes = UserRegisterEvent.class)
    public void sendCard(UserRegisterEvent event){
        User user = event.getUser();
        userBackpackService.acquireItem(user.getId(), ItemEnum.MODIFY_NAME_CARD.getId(), IdempotentEnum.UID,user.getId().toString());
    }
}
