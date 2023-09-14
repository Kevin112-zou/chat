package com.ztl.mallchat.common.user.controller;


import com.ztl.mallchat.common.user.domain.vo.response.user.UserInfoResp;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 用户表 前端控制器
 * </p>
 *
 * @author kevin
 * @since 2023-09-08
 */
@RestController
@RequestMapping("/user")
@Api("用户相关接口")
public class UserController {
    @GetMapping("/userInfo")
    @ApiOperation("用户详情")
    public UserInfoResp getUserInfo() {
        return null;
    }
}

