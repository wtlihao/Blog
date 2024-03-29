package com.wangtao.controller;

import com.wangtao.model.FriendLink;
import com.wangtao.model.Result;
import com.wangtao.redis.StringRedisServiceImpl;
import com.wangtao.service.*;
import com.wangtao.util.ResultUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.security.Principal;

/**
 * @author: zhangocean
 * @Date: 2018/7/25 16:14
 * Describe: 超级管理页面
 */
@RestController
public class SuperAdminControl {

    @Resource
    PrivateWordService privateWordService;
    @Resource
    FeedBackService feedBackService;
    @Resource
    VisitorService visitorService;
    @Resource
    UserService userService;
    @Resource
    ArticleService articleService;
    @Resource
    ArticleLikesRecordService articleLikesRecordService;
    @Resource
    StringRedisServiceImpl stringRedisService;
    @Resource
    CategoryService categoryService;
    @Resource
    FriendLinkService friendLinkService;
    @Resource
    RedisService redisService;

    /**
     * 获得所有悄悄话
     * @return
     */
    @PostMapping("/getAllPrivateWord")
    @PreAuthorize("hasAuthority('ROLE_SUPERADMIN')")
    public JSONObject getAllPrivateWord(){
        return privateWordService.getAllPrivateWord();
    }

    /**
     * 回复悄悄话
     * @return
     */
    @PostMapping("/replyPrivateWord")
    public JSONObject replyPrivateWord(@AuthenticationPrincipal Principal principal,
                                       @RequestParam("replyContent") String replyContent,
                                       @RequestParam("replyId") String id){
        String username;
        JSONObject jsonObject;
        try {
            username = principal.getName();
        } catch (NullPointerException e){
            jsonObject = new JSONObject();
            jsonObject.put("status",403);
            return jsonObject;
        }

        return privateWordService.replyPrivateWord(replyContent, username, Integer.parseInt(id));
    }

    /**
     * 分页获得所有反馈信息
     * @param rows 一页大小
     * @param pageNum 当前页
     */
    @GetMapping("/getAllFeedback")
    public JSONObject getAllFeedback(@RequestParam("rows") String rows,
                                     @RequestParam("pageNum") String pageNum){
        return feedBackService.getAllFeedback(Integer.parseInt(rows),Integer.parseInt(pageNum));
    }

    /**
     * 获得统计信息
     * @return
     */
    @GetMapping("/getStatisticsInfo")
    public JSONObject getStatisticsInfo(){
        JSONObject returnJson = new JSONObject();
        Long totalVisitor = redisService.getVisitorNumOnRedis("visitor", "totalVisitor");
        Long yesterdayVisitor = redisService.getVisitorNumOnRedis("visitor", "yesterdayVisitor");
        returnJson.put("allVisitor", totalVisitor);
        returnJson.put("allUser", userService.countUserNum());
        returnJson.put("yesterdayVisitor", yesterdayVisitor);
        returnJson.put("articleNum", articleService.countArticle());
        if(stringRedisService.hasKey("articleThumbsUp")){
            int articleThumbsUp = (int) stringRedisService.get("articleThumbsUp");
            returnJson.put("articleThumbsUpNum", articleThumbsUp);
        } else {
            returnJson.put("articleThumbsUpNum", 0);
        }
        return returnJson;
    }

    /**
     * 获得文章管理
     * @return
     */
    @PostMapping("/getArticleManagement")
    public JSONObject getArticleManagement(@AuthenticationPrincipal Principal principal,
                                           @RequestParam("rows") String rows,
                                           @RequestParam("pageNum") String pageNum){
        String username = null;
        JSONObject returnJson = new JSONObject();
        try {
            username = principal.getName();
        } catch (NullPointerException e){
            returnJson.put("status",403);
            return  returnJson;
        }
        return articleService.getArticleManagement(Integer.parseInt(rows), Integer.parseInt(pageNum));
    }

    /**
     * 删除文章
     * @param id 文章id
     * @return 1--删除成功   0--删除失败
     */
    @GetMapping("/deleteArticle")
    public int deleteArticle(@RequestParam("id") String id){
        if("".equals(id) || id == null){
            return 0;
        }
        return articleService.deleteArticle(Long.parseLong(id));
    }

    /**
     * 获得文章点赞信息
     */
    @PostMapping("/getArticleThumbsUp")
    public JSONObject getArticleThumbsUp(@RequestParam("rows") int rows,
                                         @RequestParam("pageNum") int pageNum,
                                         @AuthenticationPrincipal Principal principal){
        String username;
        try {
            username = principal.getName();
        } catch (NullPointerException e){
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("status", 403);
            return jsonObject;
        }
        return articleLikesRecordService.getArticleThumbsUp(username, rows, pageNum);
    }

    /**
     * 已读一条点赞信息
     */
    @GetMapping("/readThisThumbsUp")
    public int readThisThumbsUp(@RequestParam("id") int id){

        return articleLikesRecordService.readThisThumbsUp(id);
    }

    /**
     * 已读所有点赞信息
     */
    @GetMapping("/readAllThumbsUp")
    public JSONObject readAllThumbsUp(@AuthenticationPrincipal Principal principal){
        String username;
        try {
            username = principal.getName();
        } catch (NullPointerException e){
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("status", 403);
            return jsonObject;
        }
        return articleLikesRecordService.readAllThumbsUp();
    }

    /**
     * 获得所有分类
     */
    @GetMapping("/getArticleCategories")
    public JSONObject getArticleCategories(){
        return categoryService.findAllCategories();
    }

    /**
     * 添加或删除分类
     */
    @PostMapping("/updateCategory")
    @ResponseBody
    public JSONObject updateCategory(@RequestParam("categoryName") String  categoryName,
                              @RequestParam("type") int type){
        return categoryService.updateCategory(categoryName, type);
    }

    /**
     * 获得友链
     */
    @PostMapping("/getFriendLink")
    public JSONArray getFriendLink(){
        return friendLinkService.getAllFriendLink();
    }

    /**
     * 添加或编辑友链
     */
    @PostMapping("/addFriendLink")
    public Result addFriendLink(@RequestParam("id") String id,
                                @RequestParam("blogger") String blogger,
                                @RequestParam("url") String url){
        FriendLink friendLink = new FriendLink(blogger, url);
        if("".equals(id)){
            return friendLinkService.addFriendLink(friendLink);
        } else {
            return friendLinkService.updateFriendLink(friendLink, Integer.parseInt(id));
        }
    }

    /**
     * 删除友链
     */
    @PostMapping("/deleteFriendLink")
    public Result deleteFriendLink(@RequestParam("id") int id,
                                   @AuthenticationPrincipal Principal principal){
        try {
            String username = principal.getName();
        } catch (NullPointerException e){
            return ResultUtil.error(403, "该用户没有权限！");
        }
        return friendLinkService.deleteFriendLink(id);
    }
}
