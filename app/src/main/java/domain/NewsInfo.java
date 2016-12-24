package domain;

import java.util.ArrayList;

/**
 *
 * Created by Administrator on 2016/8/22.
 */
public class NewsInfo {

    public String currentTime;//当前时间
    public ArrayList<news> news = new ArrayList<>();
    public ArrayList<pics> pics = new ArrayList<>();

    public static class news {
        public String newsTitle;//新闻标题
        public String newsUrl;//新闻地址
    }

    public static class pics {
        public String picUrl;//轮播图片地址
        public String picTitle;//新闻地址
    }


}