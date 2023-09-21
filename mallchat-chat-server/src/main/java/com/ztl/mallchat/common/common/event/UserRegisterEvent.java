package com.ztl.mallchat.common.common.event;

import com.ztl.mallchat.common.user.domain.entity.User;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * Description:
 * Author:  kevin
 * Date:  2023/09/21
 */

@Getter
public class UserRegisterEvent extends ApplicationEvent {
    private final User user;
    public UserRegisterEvent(Object source,User user) {
        super(source);
        this.user = user;
    }
}
