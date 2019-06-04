package com.wangtao.service;

import com.wangtao.model.FriendLink;
import com.wangtao.model.Result;
import net.sf.json.JSONArray;

/**
 * @author: zhangocean
 * @Date: 2019/5/16 17:08
 * Describe:
 */
public interface FriendLinkService {

    Result addFriendLink(FriendLink friendLink);

    JSONArray getAllFriendLink();

    Result updateFriendLink(FriendLink friendLink, int id);

    Result deleteFriendLink(int id);

    Result getFriendLink();
}
