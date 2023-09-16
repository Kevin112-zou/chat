package com.ztl.mallchat.common.user.controller;


import com.ztl.mallchat.common.common.domain.dto.RequestInfo;
import com.ztl.mallchat.common.common.domain.vo.resp.ApiResult;
import com.ztl.mallchat.common.common.utils.RequestHolder;
import com.ztl.mallchat.common.user.domain.vo.req.user.ModifyNameReq;
import com.ztl.mallchat.common.user.domain.vo.resp.user.UserInfoResp;
import com.ztl.mallchat.common.user.service.IUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

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
    @Autowired
    private IUserService userService;
    @GetMapping("/userInfo")
    @ApiOperation("用户详情")
    public ApiResult<UserInfoResp> getUserInfo() {
        RequestInfo requestInfo = RequestHolder.get();
        Long uid = requestInfo.getUid();
        return ApiResult.success(userService.getUserInfo(uid));
    }

    @PutMapping("/name")
    @ApiOperation("修改用户名")
    public ApiResult<Void> modifyName(@Valid @RequestBody ModifyNameReq req){
        userService.modifyName(RequestHolder.get().getUid(),req.getName());
        return ApiResult.success();
    }
}

