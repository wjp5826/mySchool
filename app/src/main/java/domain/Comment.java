package domain;

import cn.bmob.v3.BmobObject;

/**
 * 作者：吴建平
 * 时间：2016/10/26.
 * 作用：
 */

public class Comment extends BmobObject {
    private String userName;//用户名
    private String url;//评论区
    private String content;//评论内容
    private String time;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
