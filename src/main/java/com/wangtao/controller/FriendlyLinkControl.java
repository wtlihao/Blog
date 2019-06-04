package com.wangtao.controller;

import com.wangtao.model.Result;
import com.wangtao.service.FriendLinkService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author: zhangocean
 * @Date: 2019/5/19 17:04
 * Describe: 友链页面
 */
@RestController
public class FriendlyLinkControl {

    @Resource
    FriendLinkService friendLinkService;

    /**
     * 获得所有友链信息
     */
    @PostMapping("/getFriendLinkInfo")
    public Result getFriendLink(){
        return friendLinkService.getFriendLink();
    }

}
