package fragment;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.baidu.wjp.jvtc.NewsDetailActivity;
import com.baidu.wjp.jvtc.R;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;

import adapter.ListViewAdapter;
import adapter.PagerAdapter;
import utils.JSON;
import utils.ThreadManager;
import utils.util;
import domain.NewsInfo;

/**
 * 首页展示
 * Created by Administrator on 2016/8/12.
 */
public class HomeFragment extends BaseFragment{

    private int previous = 0;//上一个点的位置
    private View mView;
    private RelativeLayout mRelativeLayout;
    private AbsListView.LayoutParams mListParamas;
    private RelativeLayout.LayoutParams mRlParamas;
    private LinearLayout mIndicator;
    private RelativeLayout.LayoutParams mIndicatorParamas;
    private ViewPager mPager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showProgress();
        ThreadManager.getThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    NewsInfo info = JSON.getNewsInfo();
                    Message msg = new Message();
                    msg.what = NEWSINFO_SEND;
                    msg.obj = info;
                    mHandler.sendMessage(msg);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mView = inflater.inflate(R.layout.fragment_home, container, false);

        //创建relativelayout布局
        mRelativeLayout = new RelativeLayout(util.getContext());
        mListParamas = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, util.dp2px(200));
        mRelativeLayout.setLayoutParams(mListParamas);
        mRlParamas = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        //创建一个线性布局，用于指示器
        mIndicator = new LinearLayout(util.getContext());
        mIndicatorParamas = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        mIndicator.setLayoutParams(mIndicatorParamas);

        mIndicator.setOrientation(LinearLayout.HORIZONTAL);
        mIndicatorParamas.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        mIndicatorParamas.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        int margin = util.dp2px(5);
        mIndicatorParamas.setMargins(margin, margin, margin, margin);

        //创建viewpager
        mPager = new ViewPager(util.getContext());
        mPager.setLayoutParams(mRlParamas);
        mRelativeLayout.addView(mPager);
        return mView;
    }

    @Override
    protected void refreshView(Object data) {
        NewsInfo info = (NewsInfo) data;
        final ArrayList<NewsInfo.pics> pics = info.pics;
        final ArrayList<NewsInfo.news> news = info.news;
        mPager.setAdapter(new PagerAdapter(pics));
        mPager.setCurrentItem(pics.size() * 10000);

        //设置指示图标
        int listSize = pics.size();
        for (int i = 0; i < listSize; i++) {
            ImageView img = new ImageView(util.getContext());
            img.setImageResource(R.mipmap.indicator_normal);
            int padding = util.dp2px(3);
            img.setPadding(padding, 0, 0, 0);
            if (i == 0) {
                img.setImageResource(R.mipmap.indicator_selected);
            }
            mIndicator.addView(img);
        }
        mRelativeLayout.addView(mIndicator, mIndicatorParamas);
        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                int pos = position % pics.size();
                //现在的
                ImageView img =(ImageView) mIndicator.getChildAt(pos);
                img.setImageResource(R.mipmap.indicator_selected);
                //上一个点
                ImageView previousPoint = (ImageView) mIndicator.getChildAt(previous);
                previousPoint.setImageResource(R.mipmap.indicator_normal);
                previous = pos;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        ListView newsList = (ListView) mView.findViewById(R.id.home_news);
        newsList.setAdapter(new ListViewAdapter(news));
        newsList.addHeaderView(mRelativeLayout);

        newsList.setDivider(null);//去掉分割线
        newsList.setSelector(new ColorDrawable());//设置状态选择器为全透明

        //设置监听事件
        newsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), NewsDetailActivity.class);
                intent.putExtra("url", news.get(position - 1).newsUrl);
                intent.putExtra("title", news.get(position - 1).newsTitle);
                startActivity(intent);
            }
        });

    }
}