package utils;

import android.app.Application;
import android.content.Context;
import android.os.Handler;



/**
 *
 * Created by Administrator on 2016/8/10.
 */
public class MyApplication extends Application {

    private static Handler mHandler = new Handler();
    private static Context mContext;
    public static boolean isLogin = false;//是否登录

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
    }

    public static Context getContext() {
        return mContext;
    }

    public static Handler getHandler() {
        return mHandler;
    }
}