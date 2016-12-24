
package adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import com.baidu.wjp.jvtc.R;
import java.util.ArrayList;
import utils.util;
import domain.NewsInfo;

/**
 * 作者：吴建平
 * 时间：2016/8/26.
 * 作用：listview的适配类
 */
public class ListViewAdapter extends BaseAdapter {

    private ArrayList<NewsInfo.news> data;
    private LayoutInflater mLayoutInflater;

    public ListViewAdapter(ArrayList<NewsInfo.news> data) {
        this.data = data;
        mLayoutInflater = LayoutInflater.from(util.getContext());
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView= mLayoutInflater.inflate(R.layout.item_newslist, null);
            holder.text = (TextView) convertView.findViewById(R.id.news_title);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.text.setText(data.get(position).newsTitle);
        return convertView;
    }

    class ViewHolder {
        public TextView text;
    }
}