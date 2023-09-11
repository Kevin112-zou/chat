package com.ztl.mallchat.common.common.constant;


/**
 * Description:
 * Author:  kevin
 * Date:  2023/09/11
 */
public class RedisKey {
    public static final String BASE_KEY = "mallcaht:chat";
    /**
     * 用户token的key
     */
    public static final String USER_TOKEN_STRING = "userToken:uid_%d";

    public static String getKey(String key,Object... o){
        //使用 String.format 方法将 %d 替换为用户的唯一标识符或其他参数。最终返回的字符串将用作Redis键。
        return BASE_KEY + String.format(key,o);
    }
}
