package adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import utils.HttpUtil;
import utils.util;
import domain.NewsInfo;

/**
 * ViewPager的适配类
 * Created by Administrator on 2016/8/15.
 */
public class PagerAdapter extends android.support.v4.view.PagerAdapter {

    public ArrayList<NewsInfo.pics> data;

    public PagerAdapter(ArrayList<NewsInfo.pics> data) {
        this.data = data;
    }

    @Override
    public int getCount() {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        position = position % data.size();
        NewsInfo.pics pics = data.get(position);
        ImageView img = new ImageView(util.getContext());
        img.setScaleType(ImageView.ScaleType.FIT_XY);
//        mLoader.bindBitmap(HttpUtil.baseUrl + pics.picUrl, img);
        Glide.with(util.getContext()).load(HttpUtil.baseUrl + pics.picUrl).into(img);
        container.addView(img);
        return img;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}