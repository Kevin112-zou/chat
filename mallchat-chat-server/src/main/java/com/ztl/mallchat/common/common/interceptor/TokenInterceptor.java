package com.ztl.mallchat.common.common.interceptor;

import cn.hutool.http.ContentType;
import com.ztl.mallchat.common.common.exception.HttpErrorEnum;
import com.ztl.mallchat.common.user.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Optional;

@Component
public class TokenInterceptor implements HandlerInterceptor {

    public static final String UID = "uid";
    @Autowired
    private LoginService loginService;
    public static final String HEADER_AUTHORIZATION = "Authorization";
    public static final String AUTHORIZATION_SCHEMA = "Bearer ";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = getToken(request);
        //拿到token进行一个校验,获取到其中的uid
        Long validUid = loginService.getValidUid(token);
        if(Objects.nonNull(validUid)){ // 不为空说明，用户有登录态
            request.setAttribute(UID,validUid);
        }else { // 用户未登录过
            // 通过判断是否是public接口，如果不是直接返回401,让用户先进行登录
            boolean isPublicURI = isPublicURI(request);
            if(!isPublicURI){
                //返回401
                HttpErrorEnum.ACCESS_DENIED.sendHttpError(response);
                return false;

            }
        }
        return true;
    }

    private static boolean isPublicURI(HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        String[] split = requestURI.split("/");
        boolean isPublicURI = split.length > 3 && "public".equals(split[3]);
        return isPublicURI;
    }

    private String getToken(HttpServletRequest request) {
        String header = request.getHeader(HEADER_AUTHORIZATION);
        // 因为第一次登录的话，可能获取到的token为空，所以用Optional
        return Optional.ofNullable(header)
                .filter(h -> h.startsWith(AUTHORIZATION_SCHEMA)) //使用jwt的前缀协议
                .map(h -> h.replaceFirst(AUTHORIZATION_SCHEMA, "")) //将协议头去掉就是token
                .orElse(null); // 如果没有获取到就返回null
    }
}
