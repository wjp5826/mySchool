package fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import utils.NetUtils;
import utils.SPUtils;
import utils.util;
import domain.NewsDetail;
import domain.NewsInfo;

/**
 * fragment的父类
 * Created by Administrator on 2016/8/12.
 */
public abstract class BaseFragment extends Fragment {

    protected static final int NEWSINFO_SEND = 0;//newsinfo的消息发送
    protected static final int NEWSDETAIL_SEND = 1;//newsdetail的消息发送
    protected static final int SEND_CHECKCODE = 6;//验证码消息
    protected static final int GRADE_SEND = 7;//成绩消息
    protected static final int FORUM_SEND = 8;//成绩消息


    protected ProgressDialog mDialog;
    protected Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            closeProgress();
            switch (msg.what) {
                case NEWSINFO_SEND:
                    NewsInfo info = (NewsInfo) msg.obj;
                    if (info == null) {
                        util.showMsg("学校官网出问题了，待会儿来吧...");
                        return;
                    }
                    refreshView(info);
                    break;
                case NEWSDETAIL_SEND:
                    NewsDetail detail = (NewsDetail) msg.obj;
                    if (detail == null) {
                        util.showMsg("未能成功加载...");
                        return;
                    }
                    refreshView(detail);
                    break;
                case SEND_CHECKCODE:
                    Object obj = msg.obj;
                    if (obj == null) {
                        util.showMsg("学校系统出问题了，暂时登录不上");
                    }
                    refreshView(obj);
                    break;
                case GRADE_SEND:
                    Object score = msg.obj;
                    refreshView(score);
                    break;
                case FORUM_SEND:
                    Object html = msg.obj;
                    refreshView(html);
            }
        }
    };


    @Override
    public void onResume() {
        super.onResume();
        util.checkNetWorkConnect(getActivity());
    }

    protected abstract void refreshView(Object data);

    protected void showProgress() {
        if (mDialog == null) {
            mDialog = new ProgressDialog(getActivity());
            mDialog.setMessage("loading");
            mDialog.setCanceledOnTouchOutside(false);
            mDialog.show();
        }
    }

    protected void closeProgress() {
        if (mDialog != null) {
            mDialog.dismiss();
        }
    }

}