package fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.baidu.wjp.jvtc.R;

import utils.SPUtils;

/**
 * 作者：吴建平
 * 时间：2016/10/11.
 * 作用：
 */

public class InfoFragment extends BaseFragment {

    private TextView mUserNameTV;
    private TextView mUserSexTV;
    private TextView mUserDepartTV;
    private TextView mUserClassTV;
    private Button mUserExitBtn;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_info, container, false);
        initView(view);

        return view;
    }

    private void initView(View view) {
        mUserNameTV = (TextView) view.findViewById(R.id.info_name);
        mUserSexTV = (TextView) view.findViewById(R.id.info_sex);
        mUserDepartTV = (TextView) view.findViewById(R.id.info_depart);
        mUserClassTV = (TextView) view.findViewById(R.id.info_class);
        mUserExitBtn = (Button) view.findViewById(R.id.info_exit);
        if (SPUtils.getLoginState()) {
            mUserNameTV.setText(SPUtils.getUserName());
            mUserSexTV.setText(SPUtils.getUserSex());
            mUserClassTV.setText(SPUtils.getUserClass());
            mUserDepartTV.setText(SPUtils.getUserDepart());

            mUserExitBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    MyApplication.isLogin = false;
                    SPUtils.saveLoginState(false);
                    getActivity().finish();
                }
            });
        }
    }

    @Override
    protected void refreshView(Object data) {

    }
}
