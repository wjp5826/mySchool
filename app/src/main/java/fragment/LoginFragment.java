package fragment;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;

import com.baidu.wjp.jvtc.R;

import java.io.IOException;

import utils.HttpUtil;
import utils.SPUtils;
import utils.ThreadManager;
import utils.util;

/**
 * 作者：吴建平
 * 时间：2016/10/8.
 * 作用：登录页面
 */

public class LoginFragment extends BaseFragment {

    private EditText mUserNameET;//学号输入框
    private EditText mUserPassET;//密码输入框
    private EditText mCheckCodeET;//验证码输入框
    private ImageView mCheckCodeIV;//验证码显示图片
    private Button mLoginSubmit;//登录按钮
    private Bitmap mBitmap;//
    private CheckBox mSavePasswordCB;//记住密码选项
    private String mUserName;
    private String mUserPwd;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view  = inflater.inflate(R.layout.fragment_login,container,false);
        initView(view);
        obtainCode();
        ListenerEvent();
        return view;
    }

    /**
     * 获取验证码
     */
    private void obtainCode() {
        ThreadManager.getThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                Bitmap bitmap = HttpUtil.getCode();
                Message msg = mHandler.obtainMessage(SEND_CHECKCODE, bitmap);
                mHandler.sendMessage(msg);
            }
        });
    }

    /**
     * 初始化控件
     * @param view
     */
    private void initView(View view) {
        mUserNameET = (EditText) view.findViewById(R.id.login_user_name);
        mUserPassET = (EditText) view.findViewById(R.id.login_user_password);
        mCheckCodeET = (EditText) view.findViewById(R.id.login_user_checkcode);
        mCheckCodeIV = (ImageView) view.findViewById(R.id.login_user_checkcode_img);
        mLoginSubmit = (Button) view.findViewById(R.id.login_user_login);
        mSavePasswordCB = (CheckBox) view.findViewById(R.id.login_user_save);
        //每次进入页面加载上一次登录成功的学号
        String userId = SPUtils.getUserId();
        String userPass = SPUtils.getUserPassword();
        if(userId != null){
            mUserNameET.setText(userId);
        }
        if(userPass != null){
            mUserPassET.setText(userPass);
        }
    }

    @Override
    protected void refreshView(Object data) {
        mBitmap = (Bitmap) data;
        mCheckCodeIV.setImageBitmap(mBitmap);
    }

    /**
     * 监听事件
     */
    private void ListenerEvent(){
        //登录按钮监听
        mLoginSubmit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                mUserName = mUserNameET.getText().toString().trim();
                mUserPwd = mUserPassET.getText().toString().trim();
                final String checkcode = mCheckCodeET.getText().toString().trim();

                if(TextUtils.isEmpty(mUserName) || TextUtils.isEmpty(mUserPwd) || TextUtils.isEmpty(checkcode)){
                    util.showMsg("用户名、密码或者验证码均不能为空");
                    return;
                }
                showProgress();
                ThreadManager.getThreadPool().execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            int isLogin = HttpUtil.login(checkcode, mUserName, mUserPwd);
                            if(isLogin == 0){

                            }else if(isLogin == 1){
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        closeProgress();
                                    }
                                });
//                                MyApplication.isLogin = true;
                                SPUtils.saveLoginState(true);
                                SPUtils.saveUserId(mUserNameET.getText().toString().trim());
                                //如果用户点击了记住密码，就保存密码
                                if(SPUtils.getPasswordState()){
                                    SPUtils.saveUserPassword(mUserPassET.getText().toString().trim());
                                }
                                HttpUtil.getUserInfo();
                                getActivity().finish();
                            }else if(isLogin == 2){
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        util.showMsg("验证码输入错误");
                                        closeProgress();
                                    }
                                });
                            }else if(isLogin == 3){
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        util.showMsg("用户名或者密码错误");
                                        closeProgress();
                                    }
                                });
                            }else if (isLogin == 4){
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        util.showMsg("未知错误,请联系开发者");
                                        closeProgress();
                                    }
                                });
                            }
                            obtainCode();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                });
            }
        });
        //点击切换验证码
        mCheckCodeIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                obtainCode();
            }
        });
        //记住密码监听
        mSavePasswordCB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    SPUtils.savePasswordState(true);
                }else{
                    SPUtils.savePasswordState(false);
                }
            }
        });
    }

}
