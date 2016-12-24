package utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.widget.Toast;

import com.baidu.wjp.jvtc.LoginActivity;

import java.text.SimpleDateFormat;


/**
 * 工具类
 * Created by Administrator on 2016/8/10.
 */
public class util {


    public static Context getContext() {
        return MyApplication.getContext();
    }

    public static int dp2px(float dp) {
        float density = getContext().getResources().getDisplayMetrics().density;
        return (int) (dp * density + 0.5f);
    }

    public static float px2dp(int px) {
        float density = getContext().getResources().getDisplayMetrics().density;
        return px / density;
    }

    public static Handler getHandler() {
        return MyApplication.getHandler();
    }

    public static void showMsg(String string) {
        Toast.makeText(util.getContext(), string, Toast.LENGTH_SHORT).show();
    }

    public static void sureDialog(final Activity a){
        AlertDialog.Builder builder = new AlertDialog.Builder(a);
        builder.setTitle("登录提示");
        builder.setMessage("你尚未登录，是否登录？");
        builder.setPositiveButton("好的", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(a,LoginActivity.class);
                a.startActivity(intent);
//                a.startActivityForResult(intent,);
            }
        });
        builder.setNegativeButton("不了", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    public static String timeChange(Long currentMill){
        SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日 hh时mm分ss秒");
        return format.format(currentMill);

    }

    /**
     * 回收bitmap内存
     * @param bitmap
     */
    public static void recycleBitmap(Bitmap bitmap){
        if(bitmap != null && !bitmap.isRecycled()){
            bitmap.recycle();
            bitmap = null;
        }
        System.gc();
    }

    public static void checkNetWorkConnect(final Activity activity) {
        if (NetUtils.isConnected(util.getContext())) {//判断是否联网
            if(!SPUtils.getNetWorkState()){
                if (!NetUtils.isWifi(util.getContext())) {//判断是否是WiFi
                    AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                    builder.setTitle("联网提示")
                            .setMessage("你的手机未连接wifi网络")
                            .setPositiveButton("我是土豪", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    SPUtils.saveNetWorkState(true);
                                    dialog.dismiss();
                                }
                            }).setNegativeButton("去设置", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            NetUtils.openSetting(activity);
                        }
                    }).create().show();
                }
            }

        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setTitle("联网提示")
                    .setMessage("你的手机未连接网络")
                    .setPositiveButton("退出", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            activity.finish();
                        }
                    }).setNegativeButton("去设置", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    NetUtils.openSetting(activity);
                }
            }).create().show();
        }
    }

}