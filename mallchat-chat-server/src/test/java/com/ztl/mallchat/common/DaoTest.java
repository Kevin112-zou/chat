package com.ztl.mallchat.common;

import com.ztl.mallchat.common.user.dao.UserDao;
import com.ztl.mallchat.common.user.domain.entity.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * author  kevin
 * Date  2023/09/08
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class DaoTest {

    @Autowired
    private UserDao userDao;

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