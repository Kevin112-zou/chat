package com.ztl.mallchat.common;

import com.ztl.mallchat.common.common.thread.MyUncaughtExceptionHandler;
import com.ztl.mallchat.common.common.utils.JwtUtils;
import com.ztl.mallchat.common.user.dao.UserDao;
import com.ztl.mallchat.common.user.domain.entity.User;
import com.ztl.mallchat.common.user.domain.enums.IdempotentEnum;
import com.ztl.mallchat.common.user.domain.enums.ItemEnum;
import com.ztl.mallchat.common.user.domain.enums.ItemTypeEnum;
import com.ztl.mallchat.common.user.service.IUserBackpackService;
import com.ztl.mallchat.common.user.service.LoginService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * author  kevin
 * Date  2023/09/08
 */
@SpringBootTest
@RunWith(SpringRunner.class)
@Slf4j
public class DaoTest {

    public static final long UID = 100028L;
    @Autowired
    private UserDao userDao;
    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private LoginService loginService;
    @Autowired
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;
    @Test
    public void threadPool(){
        Thread thread = new Thread(()->{
            if(1 == 1){
                log.error("121");
                throw new RuntimeException("1213");
            }
        });
        thread.setUncaughtExceptionHandler(new MyUncaughtExceptionHandler());
        thread.start();
    }
    @Test
    public void jwt(){
        String loginToken = loginService.getLoginToken(UID);
        System.out.println(loginToken);
    }

    @Autowired
    private IUserBackpackService userBackpackService;
    @Test
    public void acquireItem(){
        userBackpackService.acquireItem(UID, ItemEnum.PLANET.getId(), IdempotentEnum.UID, UID+"");
    }
    @Test
    public void test(){
        User user = new User();
        user.setName("hhh");
        user.setSex(1);
        user.setOpenId("12312");
        boolean save = userDao.save(user);
        System.out.println(save);
    }
}
