package com.ztl.mallchat.common.common.event.listener;

import com.ztl.mallchat.common.common.event.UserBlackEvent;
import com.ztl.mallchat.common.common.event.UserRegisterEvent;
import com.ztl.mallchat.common.user.dao.UserDao;
import com.ztl.mallchat.common.user.domain.entity.User;
import com.ztl.mallchat.common.user.domain.enums.IdempotentEnum;
import com.ztl.mallchat.common.user.domain.enums.ItemEnum;
import com.ztl.mallchat.common.user.service.IUserBackpackService;
import com.ztl.mallchat.common.user.service.adapter.UserAdapter;
import com.ztl.mallchat.common.websocket.domain.vo.resp.ws.WSBaseResp;
import com.ztl.mallchat.common.websocket.domain.vo.resp.ws.WSBlack;
import com.ztl.mallchat.common.websocket.service.WebsocketService;
import com.ztl.mallchat.common.websocket.service.adapter.WebSocketAdapter;
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
public class UserBlackListener {
    @Autowired
    private WebsocketService websocketService;
    @Autowired
    private UserDao userDao;
    /**
     * @Async : 异步执行
     * @TransactionalEventListener： 配置执行的顺序，事务提交前还是提交后执行
     */
    @Async
    @EventListener(classes = UserBlackEvent.class)
    public void sendMsg(UserBlackEvent event){
        User user = event.getUser();
        WSBaseResp<?> wsBlackResp = WebSocketAdapter.buildBlackResp(user);
        websocketService.sendMsgToAll(wsBlackResp);
    }

    @Async
    @EventListener(classes = UserBlackEvent.class)
    public void changeStatus(UserBlackEvent event){
        User user = event.getUser();
        userDao.invalidUid(user.getId());
    }
}
