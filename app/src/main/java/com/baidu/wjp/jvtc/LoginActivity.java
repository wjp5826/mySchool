package com.baidu.wjp.jvtc;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import fragment.LoginFragment;

/**
 * 作者：吴建平
 * 时间：2016/10/8.
 * 作用：
 */

public class LoginActivity extends BaseActivity{

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragment_container_login);
        if(fragment == null){
            fragment = new LoginFragment();
        }
        fm.beginTransaction().replace(R.id.fragment_container_login,fragment).commit();
    }
}
