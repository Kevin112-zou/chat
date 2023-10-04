package com.ztl.mallchat.common.user.dao;

import com.ztl.mallchat.common.user.domain.entity.Black;
import com.ztl.mallchat.common.user.mapper.BlackMapper;
import com.ztl.mallchat.common.user.service.IBlackService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 黑名单 服务实现类
 * </p>
 *
 * @author kevin
 * @since 2023-10-04
 */
@Service
public class BlackDao extends ServiceImpl<BlackMapper, Black> implements IBlackService {

}
