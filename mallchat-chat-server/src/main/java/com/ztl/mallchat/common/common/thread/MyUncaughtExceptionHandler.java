package com.ztl.mallchat.common.common.thread;

import lombok.extern.slf4j.Slf4j;

/**
 * Description:
 * Author:  kevin
 * Date:  2023/09/12
 */
@Slf4j
public class MyUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler{
    @Override
    public void uncaughtException(Thread t, Throwable e) {
        log.error("Exception in Thread" + t.getName(),e);
    }
}
