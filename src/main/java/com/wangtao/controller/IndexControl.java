package com.wangtao.controller;

import com.wangtao.model.FeedBack;
import com.wangtao.model.Result;
import com.wangtao.service.*;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.security.Principal;

/**
 * @author: zhangocean
 * @Date: 2018/6/16 16:01
 * Describe:
 */
@RestController
public class IndexControl {

    @Resource
    VisitorService visitorService;
    @Resource
    ArticleService articleService;
    @Resource
    CommentService commentService;
    @Resource
    TagService tagService;
    @Resource
    CategoryService categoryService;
    @Resource
    FeedBackService feedBackService;
    @Resource
    UserService userService;
    @Resource
    LeaveMessageService leaveMessageService;

    /**
     * 增加访客量
     * @return  网站总访问量以及访客量
     */
    @GetMapping("/getVisitorNumByPageName")
    public Result getVisitorNumByPageName(HttpServletRequest request,
                                          @RequestParam("pageName") String pageName) throws UnsupportedEncodingException {

        int index = pageName.indexOf("/");
        if(index == -1){
            pageName = "visitorVolume";
        }
        return visitorService.addVisitorNumByPageName(pageName, request);
    }

    /**
     * 分页获得当前页文章
     * @param rows 一页的大小
     * @param pageNum 当前页
     */
    @PostMapping("/myArticles")
    public JSONArray myArticles(@RequestParam("rows") String rows,
                                @RequestParam("pageNum") String pageNum){

        return articleService.findAllArticles(rows, pageNum);

    }

    /**
     * 获得最新评论
     */
    @GetMapping("/newComment")
    public JSONObject newComment(@RequestParam("rows") String rows,
                                @RequestParam("pageNum") String pageNum){

        return commentService.findFiveNewComment(Integer.parseInt(rows),Integer.parseInt(pageNum));
    }

    /**
     * 获得最新留言
     */
    @GetMapping("/newLeaveWord")
    public JSONObject newLeaveWord(@RequestParam("rows") String rows,
                                   @RequestParam("pageNum") String pageNum){
        return leaveMessageService.findFiveNewComment(Integer.parseInt(rows),Integer.parseInt(pageNum));
    }

    /**
     * 获得标签云
     */
    @GetMapping("/findTagsCloud")
    public JSONObject findTagsCloud(){
        return tagService.findTagsCloud();
    }

    /**
     * 获得右侧栏日志数、分类数、标签数
     */
    @GetMapping("/findArchivesCategoriesTagsNum")
    public JSONObject findArchivesCategoriesTagsNum(){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("tagsNum", tagService.countTagsNum());
        jsonObject.put("categoriesNum", categoryService.countCategoriesNum());
        jsonObject.put("archivesNum", articleService.countArticle());
        return jsonObject;
    }

    @GetMapping("/getSiteInfo")
    public JSONObject getSiteInfo(){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("articleNum", articleService.countArticle());
        jsonObject.put("tagsNum", tagService.countTagsNum());
        jsonObject.put("leaveWordNum", leaveMessageService.countLeaveMessageNum());
        jsonObject.put("commentNum", commentService.commentNum());
        return jsonObject;
    }

    /**
     * 反馈
     * @param feedBack
     * @param principal
     * @return
     */
    @PostMapping("/submitFeedback")
    public JSONObject submitFeedback(FeedBack feedBack,
                                     @AuthenticationPrincipal Principal principal){
        String username;
        try {
            username = principal.getName();
        } catch (NullPointerException e){
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("status",403);
            return jsonObject;
        }
        feedBack.setPersonId(userService.findIdByUsername(username));
        return feedBackService.submitFeedback(feedBack);

    }
}
