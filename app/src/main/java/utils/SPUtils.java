package utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * 作者：吴建平
 * 时间：2016/9/25.
 * 作用：
 */
public class SPUtils {

    private static SharedPreferences spUtil = util.getContext().getSharedPreferences("userInfo", Context.MODE_PRIVATE);
    private static SharedPreferences.Editor mEditor = spUtil.edit();
    private static final String USER_NAME = "userName";
    private static final String USER_PASSWORD = "userPassword";
    private static final String USER_IDENTITY = "userIdentity";//本人身份
    private static final String USER_COOKIE = "cookie";
    private static final String USER_SEX = "sex";//性别
    private static final String USER_DEPART = "depart";//所在院系
    private static final String USER_CLASS = "class";//所在班级
    private static final String USER_ID = "id";//学号
    private static final String USER_DATE = "date";//入学时间
    private static final String LOGIN_STATE = "login";//入学时间
    private static final String NET_CHOOSE = "wifi";//网络选择
    private static final String SAVE_PASSWORD = "savepassworld";//记住密码


    private static void saveValueInPreference(String key,String value){
        mEditor.putString(key,value);
        mEditor.commit();
    }

    /**
     * 保存账号
     * @param userName
     */
    public static void saveUserName(String userName) {
        saveValueInPreference(USER_NAME,userName);
    }

    public static String getUserName(){
        return spUtil.getString(USER_NAME,null);
    }

    /**
     * 保存用户性别
     * @param sex
     */
    public static void saveUserSex(String sex){
        saveValueInPreference(USER_SEX,sex);
    }

    public static String getUserSex(){
        return spUtil.getString(USER_SEX,null);
    }

    /**
     * 用户院系
     * @param depart
     */
    public static void saveUserDepart(String depart){
        saveValueInPreference(USER_DEPART,depart);
    }
    public static String getUserDepart(){
        return spUtil.getString(USER_DEPART,null);
    }

    /**
     * 班级
     * @param classs
     */
    public static void saveUserClass(String classs){
        saveValueInPreference(USER_CLASS,classs);
    }

    public static String getUserClass(){
        return spUtil.getString(USER_CLASS,null);
    }

    /**
     * id
     * @param id
     */
    public static void saveUserId(String id){
        saveValueInPreference(USER_ID,id);
    }

    public static String getUserId(){
        return spUtil.getString(USER_ID,null);
    }

    /**
     * 保存用户密码
     *
     * @param userPassword
     */
    public static void saveUserPassword(String userPassword) {
        saveValueInPreference(USER_PASSWORD,userPassword);
    }

    public static String getUserPassword(){
        return spUtil.getString(USER_PASSWORD,null);
    }

    /**
     * 保存用户类别
     * @param userIdentity
     */
    public static void saveUserIdentity(String userIdentity) {
        saveValueInPreference(USER_IDENTITY,userIdentity);
    }

    /**
     * 保存cookie
     * @param cookie
     */
    public static void saveUserCookie(String cookie){
        saveValueInPreference(USER_COOKIE,cookie);
    }

    public static String getUserCookie(){
        return spUtil.getString(USER_COOKIE,null);
    }

    /**
     * 保存入学时间
     * @param date
     */
    public static void saveUserDate(String date){
        saveValueInPreference(USER_DATE,date);
    }

    public static String getUserDate(){
        return spUtil.getString(USER_DATE,null);
    }

    /**
     *  登录状态
     */
    public static void saveLoginState(boolean isLogin){
        mEditor.putBoolean(LOGIN_STATE,isLogin);
        mEditor.commit();
    }

    public static boolean getLoginState(){
        return spUtil.getBoolean(LOGIN_STATE,false);
    }

    /**
     * WiFi选择与否
     * @param isWifi
     */
    public static void saveNetWorkState(boolean isWifi){
        mEditor.putBoolean(NET_CHOOSE,isWifi);
        mEditor.commit();
    }

    public static boolean getNetWorkState(){
        return spUtil.getBoolean(NET_CHOOSE,false);
    }

    /**
     * 保存记住密码选项状态
     * @param isSave
     */
    public static void savePasswordState(boolean isSave){
        mEditor.putBoolean(SAVE_PASSWORD,isSave);
        mEditor.commit();
    }

    /**
     * 获取密码的状态
     * @return
     */
    public static boolean getPasswordState(){
        return spUtil.getBoolean(SAVE_PASSWORD,false);
    }
}
