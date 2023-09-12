package com.ztl.mallchat.common.common.thread;

import lombok.AllArgsConstructor;

import java.util.concurrent.ThreadFactory;

/**
 * Description:
 * Author:  kevin
 * Date:  2023/09/12
 */
@AllArgsConstructor
public class MyThreadFactory implements ThreadFactory {
    private ThreadFactory original;
    private static final MyUncaughtExceptionHandler MY_UNCAUGHT_EXCEPTION_HANDLER = new MyUncaughtExceptionHandler();

    /**
     * 使用装饰器模式向线程工厂中添加打印日志的逻辑
     */
    @Override
    public Thread newThread(Runnable r) {
        Thread thread = original.newThread(r);
        thread.setUncaughtExceptionHandler(MY_UNCAUGHT_EXCEPTION_HANDLER);
        return thread;
    }
}
