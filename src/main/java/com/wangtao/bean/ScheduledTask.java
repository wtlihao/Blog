package com.wangtao.bean;

import com.wangtao.redis.HashRedisServiceImpl;
import com.wangtao.service.VisitorService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.LinkedHashMap;

/**
 * @author: zhangocean
 * @Date: 2019/5/22 13:22
 * Describe: 定时任务
 */
@Component
public class ScheduledTask {

    @Resource
    HashRedisServiceImpl hashRedisServiceImpl;
    @Resource
    VisitorService visitorService;

    /**
     * cron表达式生成器：http://cron.qqe2.com/
     *
     * 每晚20点清空redis中当日网站访问记录，但保存totalVisitor、visitorVolume、yesterdayVisitor
     */
    @Scheduled(cron = "0 0 0 * * ? ")
    public void resetVisitorNumber(){
        long oldTotalVisitor = visitorService.getTotalVisitor();
        long newTotalVisitor = Long.valueOf(hashRedisServiceImpl.get("visitor", "totalVisitor").toString());
        long yesterdayVisitor = newTotalVisitor - oldTotalVisitor;
        if(hashRedisServiceImpl.hasHashKey("visitor", "yesterdayVisitor")){
            hashRedisServiceImpl.put("visitor", "yesterdayVisitor", yesterdayVisitor);
        } else {
            hashRedisServiceImpl.put("visitor", "yesterdayVisitor", oldTotalVisitor);
        }
        //将redis中的所有访客记录更新到数据库中
        LinkedHashMap map = (LinkedHashMap) hashRedisServiceImpl.getAllFieldAndValue("visitor");
        String pageName;
        for(Object e : map.keySet()){
            pageName = String.valueOf(e);
            visitorService.updateVisitorNumByPageName(pageName, String.valueOf(map.get(e)));
            if(!"totalVisitor".equals(pageName) && !"visitorVolume".equals(pageName) && !"yesterdayVisitor".equals(pageName)){
                hashRedisServiceImpl.hashDelete("visitor", pageName);
            }
        }
    }

}
