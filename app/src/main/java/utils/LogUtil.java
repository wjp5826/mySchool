package utils;

import android.util.Log;

/**
 * 日志工具类
 * Created by Administrator on 2016/8/5.
 */
public class LogUtil {
    private static final int Verbose = 0;
    private static final int Debug = 1;
    private static final int Info = 2;
    private static final int Warn = 3;
    private static final int Error = 4;

    private static int currentState = 6;

    public static void v(String tag, String message) {
        if (currentState >= Verbose) {
            Log.v(tag, message);
        }
    }

    public static void d(String tag, String message) {
        if (currentState >= Debug) {
            Log.d(tag, message);
        }
    }

    public static void i(String tag, String message) {
        if (currentState >= Info) {
            Log.i(tag, message);
        }
    }

    public static void w(String tag, String message) {
        if (currentState >= Warn) {
            Log.w(tag, message);
        }
    }

    public static void e(String tag, String message) {
        if (currentState >= Error) {
            Log.e(tag, message);
        }
    }

}