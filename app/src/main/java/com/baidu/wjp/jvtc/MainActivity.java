package com.baidu.wjp.jvtc;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;

import fragment.BaseFragment;
import fragment.FragmentFactory;
import utils.FragmentBackHelper;
import utils.SPUtils;
import utils.util;
import cn.bmob.v3.update.BmobUpdateAgent;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private BottomBar mBottomBar;
    private NavigationView mNavigationView;
    private TextView mUserNameTV;
    private TextView mUserDepartTV;
    private TextView mUserClassTV;
    private ImageView mUserPotin;
    private ImageView mHeadImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //自动更新模块开始
        BmobUpdateAgent.update(this);
        //自动更新模块完
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(this);

        initView();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Intent intent = new Intent();
        if (id == R.id.nav_schedule) {
        } else if (id == R.id.nav_grade) {
            if (SPUtils.getLoginState()) {
                intent.setClass(MainActivity.this, GradeActivity.class);
                startActivity(intent);
            } else {
                util.sureDialog(MainActivity.this);
            }
        } else if (id == R.id.nav_found) {
            intent.setClass(MainActivity.this, FoundActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_about) {
            intent.setClass(MainActivity.this,AboutActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //初始化控件
    private void initView() {
        mBottomBar = (BottomBar) findViewById(R.id.bottomBar);
        mBottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(@IdRes int tabId) {

                BaseFragment fragment = (BaseFragment) FragmentFactory.createFragment(tabId);
                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction transaction = fm.beginTransaction();

                if (tabId == R.id.tab_home) {
                    transaction.replace(R.id.fragment_container, fragment);
                } else if (tabId == R.id.tab_school) {
                    transaction.replace(R.id.fragment_container, fragment);
                } else if (tabId == R.id.tab_message) {
                    transaction.replace(R.id.fragment_container, fragment);
                } else if (tabId == R.id.tab_forum) {
                    transaction.replace(R.id.fragment_container, fragment);
                }

                transaction.commit();
            }
        });
        View layout = mNavigationView.inflateHeaderView(R.layout.nav_header_main);

        mUserNameTV = (TextView) layout.findViewById(R.id.head_user_name);
        mUserDepartTV = (TextView) layout.findViewById(R.id.head_user_depart);
        mUserClassTV = (TextView) layout.findViewById(R.id.head_user_class);
        mUserPotin = (ImageView) layout.findViewById(R.id.head_point);
        mHeadImg = (ImageView) layout.findViewById(R.id.head_head_img);
        mHeadImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!SPUtils.getLoginState()) {
                    util.sureDialog(MainActivity.this);
                } else {
                    Intent intent = new Intent(MainActivity.this, InfoActivity.class);
                    startActivity(intent);
                }
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (SPUtils.getLoginState()) {
            String userName = SPUtils.getUserName();
            String userDepart = SPUtils.getUserDepart();
            String userClass = SPUtils.getUserClass();

            mUserNameTV.setText(userName);
            mUserClassTV.setText(userClass);
            mUserDepartTV.setText(userDepart);
            mUserPotin.setVisibility(View.VISIBLE);
            mHeadImg.setImageResource(R.mipmap.logined);
        } else {
            mUserNameTV.setText("未登录");
            mUserClassTV.setText("");
            mUserDepartTV.setText("");
            mUserPotin.setVisibility(View.GONE);
            mHeadImg.setImageResource(R.mipmap.not_login);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (!FragmentBackHelper.handleBackDown(keyCode, event, this)) {
            return super.onKeyDown(keyCode, event);
        }
        return true;
    }

}