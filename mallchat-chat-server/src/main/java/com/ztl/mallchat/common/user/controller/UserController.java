package com.ztl.mallchat.common.user.controller;


import com.ztl.mallchat.common.common.domain.dto.RequestInfo;
import com.ztl.mallchat.common.common.domain.vo.resp.ApiResult;
import com.ztl.mallchat.common.common.utils.RequestHolder;
import com.ztl.mallchat.common.user.domain.vo.resp.user.UserInfoResp;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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
@RequestMapping("/capi/user")
@Api(tags = "用户相关接口")
public class UserController {
    @GetMapping("/userInfo")
    @ApiOperation("用户详情")
    public ApiResult<UserInfoResp> getUserInfo() {
        RequestInfo requestInfo = RequestHolder.get();
        Long uid = requestInfo.getUid();
        System.out.println(uid);
        return ApiResult.success();
    }
}

