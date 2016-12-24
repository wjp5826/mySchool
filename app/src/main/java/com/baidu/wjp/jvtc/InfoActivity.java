package com.baidu.wjp.jvtc;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import fragment.InfoFragment;

/**
 * 作者：吴建平
 * 时间：2016/10/11.
 * 作用：
 */

public class InfoActivity extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragment_container_info);
        if(fragment == null){
            fragment = new InfoFragment();
        }
        fm.beginTransaction().replace(R.id.fragment_container_info,fragment).commit();
    }
}
