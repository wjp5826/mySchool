package adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.wjp.jvtc.R;
import com.bumptech.glide.Glide;

import java.util.List;

import utils.util;
import domain.school;


/**
 * 作者：吴建平
 * 时间：2016/10/18.
 * 作用：
 */

public class CardStackAdapter extends ArrayAdapter<school> {

    private List<school> mStrings;
    private LayoutInflater mInflater;
    private int res;

    public CardStackAdapter(Context context, int resource, List<school> objects) {
        super(context, resource, objects);
        mStrings = objects;
        mInflater = LayoutInflater.from(context);
        res = resource;
    }


    @Override
    public int getCount() {
        return Integer.MAX_VALUE;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int pos = position % mStrings.size();
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(res, null);
            holder.mSchoolPlaceIV = (ImageView) convertView.findViewById(R.id.school_img);
            holder.mSchoolPlaceTV = (TextView) convertView.findViewById(R.id.school_place);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        school school = mStrings.get(pos);
        Glide.with(util.getContext()).load(school.getPic().getFileUrl()).into(holder.mSchoolPlaceIV);
        holder.mSchoolPlaceTV.setText(school.getPic_name());

        return convertView;

    }

    class ViewHolder {
        TextView mSchoolPlaceTV;
        ImageView mSchoolPlaceIV;
    }
}
