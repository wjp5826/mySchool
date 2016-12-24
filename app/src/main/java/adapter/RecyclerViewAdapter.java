package adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import utils.util;
import domain.NewsInfo;

/**
 *
 * Created by Administrator on 2016/8/25.
 */
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyHolder> {

    private ArrayList<NewsInfo.news> dataList;
    private LayoutInflater mLayoutInflater;

    public RecyclerViewAdapter(ArrayList<NewsInfo.news> dataList) {
        this.dataList = dataList;
        mLayoutInflater = LayoutInflater.from(util.getContext());
    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(android.R.layout.simple_list_item_1, parent, false);
        MyHolder holder = new MyHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyHolder holder, int position) {
        holder.text.setText(dataList.get(position).newsTitle);
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    class MyHolder extends RecyclerView.ViewHolder {

        public TextView text;

        public MyHolder(View itemView) {
            super(itemView);
            text = (TextView) itemView.findViewById(android.R.id.text1);
        }
    }
}