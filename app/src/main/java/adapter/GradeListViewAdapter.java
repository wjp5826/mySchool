package adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.wjp.jvtc.R;

import java.util.ArrayList;

import utils.MyApplication;
import domain.Score;

/**
 * 作者：吴建平
 * 时间：2016/10/13.
 * 作用：
 */

public class GradeListViewAdapter extends BaseAdapter {

    ArrayList<Score> mScores;
    private LayoutInflater mInflater;

    public GradeListViewAdapter(ArrayList<Score> scores) {
        this.mScores = scores;
        this.mInflater = LayoutInflater.from(MyApplication.getContext());
    }

    @Override
    public int getCount() {
        return mScores.size();
    }

    @Override
    public Object getItem(int position) {
        return mScores.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder mHolder;
        if (convertView == null) {
            mHolder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.item_grade_list, null);
            mHolder.mCourse = (TextView) convertView.findViewById(R.id.grade_course);
            mHolder.mCredit = (TextView) convertView.findViewById(R.id.grade_credit);
            mHolder.mScore = (TextView) convertView.findViewById(R.id.grade_score);
            mHolder.mGradeBack = (LinearLayout) convertView.findViewById(R.id.grade_back);
            convertView.setTag(mHolder);
        } else {
            mHolder = (ViewHolder) convertView.getTag();
        }
        Score score = mScores.get(position);

        Double grade = Double.valueOf(score.mScore);
        if (grade < 60) {
            mHolder.mGradeBack.setBackgroundColor(Color.RED);
            mHolder.mScore.setTextColor(Color.WHITE);
            mHolder.mCredit.setTextColor(Color.WHITE);
            mHolder.mCourse.setTextColor(Color.WHITE);
        }
        mHolder.mCourse.setText(score.mCourse);
        mHolder.mCredit.setText(score.mCredit);
        mHolder.mScore.setText(score.mScore);


        return convertView;
    }

    class ViewHolder {
        TextView mCourse, mCredit, mScore;
        LinearLayout mGradeBack;
    }
}
