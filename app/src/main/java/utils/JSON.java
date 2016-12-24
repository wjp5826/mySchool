package utils;

import android.support.annotation.Nullable;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;

import domain.NewsDetail;
import domain.NewsInfo;

/**
 * json操作类
 * Created by Administrator on 2016/8/21.
 */
public class JSON {

    private static final String EXTERNAL_CACHE = util.getContext().getExternalCacheDir().getAbsolutePath();
    //新闻详情的文件名
    private static final String NEWS_INFO = "NewsInfo.json";
    //新闻列表的文件名

    //解析首页的数据
    public static String createNewsInfoJson(NewsInfo info) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("state", "ok");
        jsonObject.put("time", System.currentTimeMillis());
        //轮播图片的地址
        JSONArray pics = new JSONArray();
        ArrayList<NewsInfo.pics> picList = info.pics;

        int picSize = picList.size();
        for (int i = 0; i < picSize; i++) {
            JSONObject p = new JSONObject();
            p.put("picUrl", picList.get(i).picUrl);
            p.put("newsUrl", picList.get(i).picTitle);
            pics.put(p);
        }
        jsonObject.put("pics", pics);
        //轮播图片的新闻地址
        JSONArray news = new JSONArray();
        ArrayList<NewsInfo.news> newsList = info.news;
        for (int i = 0; i < newsList.size(); i++) {
            JSONObject n = new JSONObject();
            n.put("url", newsList.get(i).newsUrl);
            n.put("title", newsList.get(i).newsTitle);
            news.put(n);
        }
        jsonObject.put("news", news);

        Log.d("json", jsonObject.toString());
        saveToFile(jsonObject.toString(), NEWS_INFO);
        return jsonObject.toString();
    }

    //存储json字符串
    public static synchronized void saveToFile(String str, String fileName) {
        LogUtil.d("method", "saveToFile");
        Writer writer = null;
        OutputStream output = null;
        try {
            File file = new File(EXTERNAL_CACHE, fileName);
            if(file.exists()){
               file.delete();
            }
            output = new FileOutputStream(file);
            writer = new OutputStreamWriter(output);
            writer.write(str);
            writer.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (output != null) {
                    output.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    //从文件读取json
    public static synchronized String readJson(String fileName) {
        File file = new File(EXTERNAL_CACHE, fileName);
        InputStream in;
        Reader reader;
        BufferedReader read;
        try {
            in = new FileInputStream(file);
            reader = new InputStreamReader(in);
            read = new BufferedReader(reader);
            String line;
            StringBuffer sb = new StringBuffer();
            while ((line = read.readLine()) != null) {
                sb.append(line);
            }
            return sb.toString();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static synchronized NewsInfo getNewsInfo() throws IOException, JSONException {
        String jsonStr = readJson(NEWS_INFO);
        if (jsonStr == null) {
            jsonStr = HttpUtil.getPagerUrl(HttpUtil.baseUrl);
        }
        NewsInfo info = parseNewsInfo(jsonStr);
        return info;
    }

    @Nullable
    private static NewsInfo parseNewsInfo(String jsonStr) throws JSONException, IOException {
        JSONObject jsonObject = new JSONObject(jsonStr);
        if (jsonObject.has("state")) {
            NewsInfo info = new NewsInfo();

            long oldTime = (long) jsonObject.get("time");
            long currentTime = System.currentTimeMillis();
            if (currentTime - oldTime < 24 * 60 * 60 * 1000) {
                //解析首页轮播图片
                JSONArray arrayPic = jsonObject.getJSONArray("pics");
                ArrayList<NewsInfo.pics> picList = new ArrayList<>();
                for (int i = 0; i < arrayPic.length(); i++) {
                    NewsInfo.pics pic = new NewsInfo.pics();
                    JSONObject obj = arrayPic.getJSONObject(i);
                    pic.picUrl = obj.getString("picUrl");
                    pic.picTitle = obj.getString("newsUrl");
                    picList.add(pic);
                }
                info.pics = picList;
                //解析
                JSONArray arrayNews = jsonObject.getJSONArray("news");
                ArrayList<NewsInfo.news> newsList = new ArrayList<>();
                for (int i = 0; i < arrayNews.length(); i++) {
                    NewsInfo.news news = new NewsInfo.news();
                    JSONObject obj = arrayNews.getJSONObject(i);
                    news.newsUrl = obj.getString("url");
                    news.newsTitle = obj.getString("title");
                    newsList.add(news);
                }
                info.news = newsList;
                return info;
            }else{
                String newJsonStr = HttpUtil.getPagerUrl(HttpUtil.baseUrl);
                NewsInfo newInfo = parseNewsInfo(newJsonStr);
                return newInfo;
            }
        }
        return null;
    }

    //创建json
    public static synchronized String createNewsDetail(NewsDetail detail, String title) {
        JSONObject obj = new JSONObject();
        try {
            obj.put("state", "ok");
            obj.put("time", System.currentTimeMillis());
            //标题
            obj.put("title", detail.NewsTitle);
            //段落
            ArrayList<String> secList = detail.section;
            JSONArray secArray = new JSONArray();
            for (int i = 0; i < secList.size(); i++) {
                secArray.put(secList.get(i));
            }
            obj.put("sections", secArray);
            //图片
            ArrayList<String> imgList = detail.imgUrl;
            if (imgList != null && !imgList.isEmpty()) {
                JSONArray imgArray = new JSONArray();
                for (int i = 0; i < imgList.size(); i++) {
                    imgArray.put(imgList.get(i));
                }
                obj.put("imgs", imgArray);
            }
            saveToFile(obj.toString(), title);
            return obj.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static NewsDetail getNewsDetail(String url,String title) throws IOException {
        String jsonStr = readJson(title);
        if (jsonStr == null) {
            jsonStr = HttpUtil.getNewsDetailInfo(HttpUtil.baseUrl + url, title);
        }
        NewsDetail detail = parseNewsDetail(jsonStr,url,title);
        return detail;
    }

    @Nullable
    private static NewsDetail parseNewsDetail(String jsonStr,String url,String title) throws IOException {
        try {
            JSONObject obj = new JSONObject(jsonStr);
            if (obj.has("state")) {
                long oldTime = obj.getLong("time");
                long currentTime = System.currentTimeMillis();
                if (currentTime - oldTime < 24 * 60 * 3600 * 1000) {
                    NewsDetail detail = new NewsDetail();
                    //标题
                    detail.NewsTitle = obj.getString("title");
                    //段落
                    JSONArray secArray = obj.getJSONArray("sections");
                    ArrayList<String> section = new ArrayList<>();
                    for (int i = 0; i < secArray.length(); i++) {
                        section.add(secArray.getString(i));
                    }
                    detail.section = section;
                    //图片
                    if (obj.has("imgs")) {
                        JSONArray imgArray = obj.getJSONArray("imgs");
                        ArrayList<String> imgList = new ArrayList<>();
                        for (int i = 0; i < imgArray.length(); i++) {
                            imgList.add(imgArray.getString(i));
                        }
                        detail.imgUrl = imgList;
                    }

                    return detail;
                }else{
                    String newJsonStr = HttpUtil.getNewsDetailInfo(HttpUtil.baseUrl + url, title);
                    NewsDetail detail = parseNewsDetail(newJsonStr,url,title);
                    return detail;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

//    public static void createSchool(List<school> schools){
//        JSONObject jsonObject = new JSONObject();
//        long currentTime = System.currentTimeMillis();
//        jsonObject.put()
//        int schoolSize = schools.size();
//        for(int i = 0;i < schoolSize;i++){
//            school s = schools.get(i);
//
//        }
//    }
}