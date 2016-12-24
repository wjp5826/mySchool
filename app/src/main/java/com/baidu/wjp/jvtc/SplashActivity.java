package com.baidu.wjp.jvtc;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import domain.Splash;
import utils.NetUtils;

/**
 * 作者：吴建平
 * 时间：2016/11/15.
 * 作用：
 */

public class SplashActivity extends Activity{

    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        final ImageView mBakcImg = (ImageView) findViewById(R.id.splash_back);

        //初始化Bmob
        Bmob.initialize(this, "661e9dce715df5e524ac7975636abd21","Bmob");

        if(NetUtils.isConnected(this)){
            BmobQuery<Splash> query = new BmobQuery<>();
            query.findObjects(new FindListener<Splash>() {
                @Override
                public void done(List<Splash> list, BmobException e) {
                    if(e == null){
                        if(list.size() > 0){
                            Glide.with(SplashActivity.this).load(list.get(0).getPic().getFileUrl()).into(mBakcImg);
                        }
                    }
                }
            });
        }else{
            mBakcImg.setImageResource(R.mipmap.back);
        }

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        };

        mHandler.postDelayed(runnable,3000);

    }
}
