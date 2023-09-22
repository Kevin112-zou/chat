package com.ztl.mallchat.common.common.event;

import com.ztl.mallchat.common.user.domain.entity.User;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * Description:
 * Author:  kevin
 * Date:  2023/09/22
 */
@Getter
public class UserOnlineEvent extends ApplicationEvent {
    private User user;
    public UserOnlineEvent(Object source,User user) {
        super(source);
        this.user = user;
    }
}
