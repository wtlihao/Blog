package com.wangtao.controller;

import com.wangtao.model.User;
import com.wangtao.redis.StringRedisServiceImpl;
import com.wangtao.service.UserService;
import com.wangtao.util.MD5Util;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

/**
 * @author: zhangocean
 * @Date: 2018/6/8 9:24
 * Describe: 登录控制
 */
@Controller
public class LoginControl {

    @Resource
    UserService userService;
    @Resource
    StringRedisServiceImpl stringRedisService;

    @ResponseBody
    @PostMapping("/changePassword")
    public String changePassword(@RequestParam("phone") String phone,
                                 @RequestParam("authCode") String authCode,
                                 @RequestParam("newPassword") String newPassword){

        String trueMsgCode = (String) stringRedisService.get(phone);

        //判断获得的手机号是否是发送验证码的手机号
        if(trueMsgCode == null){
            return "3";
        }
        //判断验证码是否正确
        if(!authCode.equals(trueMsgCode)){
            return "0";
        }
        User user = userService.findUserByPhone(phone);
        if(user == null){
            return "2";
        }
        MD5Util md5Util = new MD5Util();
        String MD5Password = md5Util.encode(newPassword);
        userService.updatePasswordByPhone(phone, MD5Password);

        return "1";
    }

}
