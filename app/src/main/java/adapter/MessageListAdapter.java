package adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.wjp.jvtc.R;
import com.bumptech.glide.Glide;

import java.util.List;

import utils.util;
import domain.Message;

/**
 * 作者：吴建平
 * 时间：2016/11/10.
 * 作用：
 */

public class MessageListAdapter extends BaseAdapter {

    private List<Message> msgList;//消息集合
    LayoutInflater mInflater;//布局加载器

    public MessageListAdapter(List<Message> msgList) {
        this.msgList = msgList;
        this.mInflater = LayoutInflater.from(util.getContext());
    }

    @Override
    public int getCount() {
        return msgList.size();
    }

    @Override
    public Object getItem(int position) {
        return msgList.get(position);
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
            convertView = mInflater.inflate(R.layout.item_message, null);
            holder.msgTitle = (TextView) convertView.findViewById(R.id.msg_title);
            holder.msgImg = (ImageView) convertView.findViewById(R.id.msg_img);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        Message msg = msgList.get(position);
        holder.msgTitle.setText(msg.getTitle());
        Glide.with(util.getContext()).load(msg.getImg().getFileUrl()).into(holder.msgImg);
        return convertView;
    }

    class ViewHolder {
        TextView msgTitle;
        ImageView msgImg;
    }
}
