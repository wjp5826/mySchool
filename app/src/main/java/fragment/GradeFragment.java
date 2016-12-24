package fragment;

import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import com.baidu.wjp.jvtc.R;

import java.io.IOException;
import java.util.ArrayList;

import adapter.GradeListViewAdapter;
import utils.HttpUtil;
import utils.SPUtils;
import utils.ThreadManager;
import domain.Score;


/**
 * 作者：吴建平
 * 时间：2016/10/13.
 * 作用：
 */

public class GradeFragment extends BaseFragment {

    private Spinner mSpinner;
    private ListView mScoreLV;
    private String mDate;
    private View mView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragme_grade, container, false);

        initView(mView);

        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mDate = parent.getSelectedItem().toString();
                ThreadManager.getThreadPool().execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            ArrayList<Score> scoreList = HttpUtil.getGrade(mDate);
                            Message msg = mHandler.obtainMessage(GRADE_SEND, scoreList);
                            mHandler.sendMessage(msg);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        return mView;
    }

    /**
     * 通过入学日期计算出三个学年
     *
     * @return
     */
    private String[] getDate() {
        String date = SPUtils.getUserDate();
        String[] date1 = date.split("-");
        int startYear = Integer.valueOf(date1[0]);
        String[] dateArray = new String[3];
        dateArray[0] = startYear + "-" + ++startYear;
        dateArray[1] = startYear + "-" + ++startYear;
        dateArray[2] = startYear + "-" + ++startYear;
        return dateArray;
    }

    /**
     * 初始化控件
     *
     * @param view
     */
    private void initView(View view) {
        mSpinner = (Spinner) view.findViewById(R.id.grade_spinner);
        mSpinner.setAdapter(new ArrayAdapter<>(getActivity(), R.layout.item_spinner, getDate()));
        mSpinner.setVisibility(View.VISIBLE);
        mScoreLV = (ListView) view.findViewById(R.id.grade_listview);

    }

    @Override
    protected void refreshView(Object data) {
        if (data != null) {
            if (data instanceof ArrayList<?>) {
                ArrayList<Score> scores = (ArrayList<Score>) data;
                GradeListViewAdapter adapter = new GradeListViewAdapter(scores);
                adapter.notifyDataSetChanged();
                mScoreLV.setAdapter(adapter);
            }
        }else{
            ArrayList<Score> str = new ArrayList<>();
            GradeListViewAdapter adapter = new GradeListViewAdapter(str);
            mScoreLV.setAdapter(adapter);
        }
        mScoreLV.setEmptyView(mView.findViewById(R.id.empty));

    }
}
