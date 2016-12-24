package utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONException;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import domain.NewsDetail;
import domain.NewsInfo;
import domain.Score;
import domain.UserInfo;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 网络工具类
 * Created by Administrator on 2016/8/10.
 */
public class HttpUtil {

    public static final String baseUrl = "http://www.jvtc.jx.cn/";//根链接
    public static final String LoginUrl = "http://xz.jvtc.jx.cn/JVTC_XG/UserLogin.html";//登录链接
    public static final String gradeUrl = "http://xz.jvtc.jx.cn/JVTC_XG/SystemForm/StudentJudge/StudentScore.aspx";//成绩链接

    private static OkHttpClient mOkHttpClient;

    public static Response get(String url) {
        Request request = new Request.Builder()
                .url(url)
                .build();
        Response response = null;
        try {
            response = getClient().newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    /**
     * 获取网络连接
     *
     * @return
     */
    public static OkHttpClient getClient() {
        if (mOkHttpClient == null) {
            mOkHttpClient = new OkHttpClient();
        }
        return mOkHttpClient;
    }

    /**
     * 获取首页轮播链接数组
     *
     * @return
     */
    public static synchronized Document getDoc(String url) throws IOException {
        Connection mConnection = Jsoup.connect(url);
        mConnection.timeout(10000);
        Document doc = mConnection.get();
        return doc;
    }

    //从网络获取首页的数据并保存
    public static synchronized String getPagerUrl(String url) throws IOException {
        Document doc = getDoc(url);
        if (doc == null) {
            return null;
        }
        doc.select("td[id=demo1]").remove();
        NewsInfo info = new NewsInfo();
        //首页轮播
        Elements links = doc.getElementsByTag("script").eq(4);
        ArrayList<NewsInfo.pics> picList = new ArrayList<>();
        for (Element e : links) {
            //获取内容
            String body = e.data();
            LogUtil.d("body", body);
            //解析图片的链接
            int beginIndex = body.indexOf("pics");
            int endIndex = body.indexOf("links");

            String s = body.substring(beginIndex + 6, endIndex - 8);
            String[] data1 = s.split("\\|");
            //解析新闻的链接
            int beginIndex1 = body.indexOf("links");
            int endIndex1 = body.indexOf("texts");
            String s1 = body.substring(beginIndex1 + 7, endIndex1 - 8);
            String[] data2 = s1.split("\\|");

            int dataLength = data1.length;
            for (int i = 0; i < dataLength; i++) {
                NewsInfo.pics pic = new NewsInfo.pics();
                pic.picUrl = data1[i];
                pic.picTitle = data2[i];
                picList.add(pic);
            }
        }
        info.pics = picList;
        //新闻列表
        Elements linkes = doc.select("a[title]");
        ArrayList<NewsInfo.news> newsList = new ArrayList<>();
        for (Element e : linkes) {
            NewsInfo.news news = new NewsInfo.news();
            news.newsTitle = e.attr("title");
            news.newsUrl = e.attr("href");
            newsList.add(news);
        }

        info.news = newsList;
        try {
            String result = JSON.createNewsInfoJson(info);
            return result;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    //获取新闻详情的 1
    public static synchronized String getNewsDetailInfo(String url, final String title) throws IOException {
        Document doc = getDoc(url);
        if (doc == null) {
            return null;
        }
        NewsDetail detail = new NewsDetail();
        Elements elements2 = doc.select("font[size=2]");
        //解析段落
        ArrayList<String> detailList = new ArrayList<>();
        for (int i = 0; i < elements2.size(); i++) {
            String txt = elements2.eq(i).text();
            String result = txt.substring(4, txt.length());
            detailList.add(result);
        }
        detail.section = detailList;
        //解析img
        ArrayList<String> imgUrlList = new ArrayList<>();
        Elements img = doc.select("img[src*=http://www.jvtc.jx.cn/]");
        if (img.isEmpty()) {
            detail.imgUrl = null;
        } else {
            for (Element e : img) {
                System.out.println(e.attr("src"));
                String srcUrl = e.attr("src");
                imgUrlList.add(srcUrl);
            }
            detail.imgUrl = imgUrlList;
        }
        //解析标题
        Elements titleElement = doc.getElementsByTag("strong");
        detail.NewsTitle = titleElement.text();
        //创建json并存储
        String newsDetail = JSON.createNewsDetail(detail, title);
        return newsDetail;
    }

    public static String getNewsDerail2(String baseUrl) throws IOException {
        Document doc = getDoc(baseUrl);
        doc.select("font[size=4]").attr("style","line-height:150%");
        doc.select("table[width=1411]").remove();
        doc.select("table[width=997]").remove();
        doc.select("td[width=223]").remove();
        doc.select("td[width=780]:eq(1)").remove();
        doc.select("font[size]").attr("size","8");
        doc.select("td[style=line-height: 150%]").attr("style","line-height:600%");
        doc.select("td[height=54] div").attr("style","font-size:30px");
        doc.select("table[width=779]").attr("width","100%");
        doc.select("td[width=186]").remove();
        doc.select("span[style]").attr("style","LINE-HEIGHT: 150%; FONT-FAMILY: 仿宋; FONT-SIZE: 30pt");
        doc.select("img[style]").attr("style","BORDER-LEFT-COLOR: #000000; FILTER: ; BORDER-BOTTOM-COLOR: #000000; BORDER-TOP-COLOR: #000000; BORDER-RIGHT-COLOR: #000000;width:100%");
        return doc.toString();
    }

    //以下是模拟登录代码区
    private static ArrayList<String> sLoginInputHidden = new ArrayList<>();//用来存放表单隐藏域的值
    private static String sCookieHead1;//组成cookie的第一段字符串
    private static String sLoginCookie;//登录用的cookie，登录完后会获取到一个新的cookie
    private static final String loginSuccessful = "SystemForm/main.htm";//登录成功返回
    private static final String checkCodeLost = "验证码输入错误";//验证码缺少
    private static final String userNameError = "用户名或密码错误";//用户名或者密码错误
    private static final String userInfoUrl = "http://xz.jvtc.jx.cn/JVTC_XG/SystemForm/Class/MyStudent.aspx";//用户信息链接


    /**
     * 获取验证码以及隐藏域的值
     * @return
     */
    public static Bitmap getCode() {

        Request res = new Request.Builder()
                .url(LoginUrl)
                .build();
        try {
            Response response = getClient().newCall(res).execute();
            String html = response.body().string();
            Document document = Jsoup.parse(html);
            Elements elements = document.select("input[type=hidden]");
            sLoginInputHidden = new ArrayList<>();
            for (Element e : elements) {
                sLoginInputHidden.add(e.attr("value"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        String codeUrl = "http://xz.jvtc.jx.cn/JVTC_XG/default3.html";
        Request getcode = new Request.Builder()
                .url(codeUrl)
                .build();

        Response code = null;
        try {
            code = getClient().newCall(getcode).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        List<String> codes = code.headers().values("Set-Cookie");
        String[] str = codes.get(0).split(";");
        sCookieHead1 = "CNZZDATA1000271341=1159883264-1474369452-http%253A%252F%252Fxz.jvtc.jx.cn%252F%7C1474369452; ";
        sLoginCookie = sCookieHead1 + str[0];
        Log.d("cookie", codes.toString());
        InputStream in;
        in = code.body().byteStream();
        Bitmap bitmap = BitmapFactory.decodeStream(in);
        return bitmap;
    }

    /**
     * 登录
     *
     * @param randCode     验证码
     * @param userName     用户名
     * @param userPassword 密码
     */
    public static int login(final String randCode, final String userName, final String userPassword) throws IOException {
        Log.d("info", "开始登陆");
        FormBody formBody = new FormBody.Builder()
                .add("CheckCode", randCode)
                .add("UserName", userName)
                .add("UserPass", userPassword)
                .add("__VIEWSTATE", sLoginInputHidden.get(0))
                .add("__VIEWSTATEGENERATOR", sLoginInputHidden.get(1))
                .add("__EVENTVALIDATION", sLoginInputHidden.get(2))
                .add("Btn_OK.x", "25")
                .add("Btn_OK.y", "31")
                .build();
        sLoginInputHidden.clear();
        Request login = new Request.Builder()
                .url("http://xz.jvtc.jx.cn/JVTC_XG/UserLogin.html")
                .post(formBody)
                .addHeader("Host", "xz.jvtc.jx.cn")
                .addHeader("Origin", "http://xz.jvtc.jx.cn")
                .addHeader("Cookie", sLoginCookie)
                .addHeader("User-Agent","Mozilla/5.0 (compatible; MSIE 9.0; qdesk 2.4.1266.203; Windows NT 6.1; WOW64; Trident/5.0)")
                .build();
        Response response = getClient().newCall(login).execute();
        String url = response.body().string();
        List<String> u = response.headers().values("Set-Cookie");
        Document doc = Jsoup.parse(decode(url));
        //根据返回的script内容判断登录情况
        Elements elements = doc.getElementsByTag("script");
        String loginResult = null;
        for (Element element : elements) {
            Log.d("script", element.data());
            if(element.data().contains("alert") || element.data().contains("window")){
                loginResult = element.data();
                break;
            }
        }

        if(TextUtils.isEmpty(loginResult)){//输入为空
            return 0;
        }
        if(loginResult.contains(loginSuccessful)){//登录成功
            String[] c = u.get(1).split(";");
            String mLoginedCookie = sLoginCookie + "; " + c[0];
            SPUtils.saveUserCookie(mLoginedCookie);
            return 1;
        }
        if(loginResult.contains(checkCodeLost)){//验证码输入错误
            return 2;
        }
        if(loginResult.contains(userNameError)){//用户名或者密码错误
            return 3;
        }
        return 4;
    }

    public static UserInfo getUserInfo() throws IOException {
        String cookie = SPUtils.getUserCookie();
        Request getInfo = new Request.Builder()
                .url(userInfoUrl)
                .addHeader("Cookie",cookie)
                .build();
        Response response = getClient().newCall(getInfo).execute();
        String userInfoHTML = response.body().string();
        Document doc = Jsoup.parse(userInfoHTML);
        UserInfo info = new UserInfo();
        info.user_name = doc.select("input[id=Student11_StudentName]").attr("value");
        String sexNo = doc.select("input[id=Student11_Sex_0]").attr("value");
        if(sexNo.equals("1")){
            info.user_sex = "男";
        }else if(sexNo.equals("0")){
            info.user_sex = "女";
        }
        info.user_class = doc.select("select[name=Student51$ClassNo] > option[selected=selected]").text();
        info.user_depart = doc.select("select[name=Student51$CollegeNo] > option[selected=selected]").text();
        info.user_date = doc.select("input[id=Student11_InTime]").attr("value");

        //以键值对保存
        SPUtils.saveUserClass(info.user_class);
        SPUtils.saveUserDepart(info.user_depart);
        SPUtils.saveUserSex(info.user_sex);
        SPUtils.saveUserName(info.user_name);
        SPUtils.saveUserDate(info.user_date);

        return info;
    }

    /**
     * 获取成绩
     * @param date 所选日期
     * @return 所有成绩的集合
     * @throws IOException
     */
    public static ArrayList<Score> getGrade(String date) throws IOException {
        ArrayList<String> sParams = new ArrayList<>();
        String cookie = SPUtils.getUserCookie();
        if(cookie == null){
            return null;
        }
        Request get = new Request.Builder()
                .url(gradeUrl)
                .addHeader("Cookie",cookie)
                .build();
        Response response = getClient().newCall(get).execute();
        String result = response.body().string();
        Document doc = Jsoup.parse(result);
        Elements elements = doc.select("input[type=hidden]");
        for(Element e : elements){
            sParams.add(e.attr("value"));
        }
        Log.d("list",sParams.toString());

        FormBody formBody = new FormBody.Builder()
                .add("YearTime",date)
                .add("__EVENTTARGET","YearTime")
                .add("__EVENTARGUMENT","")
                .add("__LASTFOCUS","")
                .add("__VIEWSTATEENCRYPTED","")
                .add("__VIEWSTATE",sParams.get(0))
                .add("__VIEWSTATEGENERATOR",sParams.get(1))
                .build();

        Request login = new Request.Builder()
                .url(gradeUrl)
                .addHeader("Cookie",cookie)
                .addHeader("Origin","http://xz.jvtc.jx.cn")
                .post(formBody)
                .build();
//        mBuilder.followRedirects(false);
        Response res = getClient().newCall(login).execute();
        String gradehtml = res.body().string();

        ArrayList<Score> scoreList = new ArrayList<>();
        Document doc1 = Jsoup.parse(gradehtml);
        String countStr = doc1.select("font[color=red]").text();
        int countInt = Integer.valueOf(countStr);
        if(countInt == 0){
            return null;
        }
        for (int i = 1; i < countInt; i++) {
            Elements course = doc1.select("table[class=white] tr:eq(" + i + ") td:eq(4)");
            Elements credit = doc1.select("table[class=white] tr:eq(" + i + ") td:eq(5)");
            Elements grade = doc1.select("table[class=white] tr:eq(" + i + ") td:eq(6)");
            Score score = new Score();
            score.mCourse = course.text();
            score.mCredit = credit.text();
            score.mScore = grade.text();
            scoreList.add(score);
        }
        return scoreList;
    }

    private static String decode(String unicodeStr) {
        if (unicodeStr == null) {
            return null;
        }
        StringBuffer retBuf = new StringBuffer();
        int maxLoop = unicodeStr.length();
        for (int i = 0; i < maxLoop; i++) {
            if (unicodeStr.charAt(i) == '\\') {
                if ((i < maxLoop - 5)
                        && ((unicodeStr.charAt(i + 1) == 'u') || (unicodeStr
                        .charAt(i + 1) == 'U')))
                    try {
                        retBuf.append((char) Integer.parseInt(
                                unicodeStr.substring(i + 2, i + 6), 16));
                        i += 5;
                    } catch (NumberFormatException localNumberFormatException) {
                        retBuf.append(unicodeStr.charAt(i));
                    }
                else
                    retBuf.append(unicodeStr.charAt(i));
            } else {
                retBuf.append(unicodeStr.charAt(i));
            }
        }
        return retBuf.toString();
    }
}