package adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.baidu.wjp.jvtc.R;

import java.util.List;

import utils.util;
import domain.Comment;

/**
 * 作者：吴建平
 * 时间：2016/10/26.
 * 作用：
 */

public class CommentListViewAdapter extends BaseAdapter {

    private List<Comment> mCommentList;
    private LayoutInflater mInflater;

    public CommentListViewAdapter(List<Comment> commentList) {
        this.mCommentList = commentList;
        this.mInflater = LayoutInflater.from(util.getContext());
    }

    @Override
    public int getCount() {
        return mCommentList.size();
    }

    @Override
    public Object getItem(int position) {
        return mCommentList.get(position);
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
            convertView = mInflater.inflate(R.layout.item_list_comment, null);
            holder.mComContentTV = (TextView) convertView.findViewById(R.id.news_comment_content);
            holder.mComTime = (TextView) convertView.findViewById(R.id.news_comment_time);
            holder.mComUserNameTV = (TextView) convertView.findViewById(R.id.news_comment_name);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Comment comment = mCommentList.get(position);
        holder.mComUserNameTV.setText(comment.getUserName());
        holder.mComContentTV.setText(comment.getContent());
        holder.mComTime.setText(comment.getTime());
        return convertView;
    }

    class ViewHolder {
        TextView mComUserNameTV;//用户名
        TextView mComContentTV;//评论内容
        TextView mComTime;//评论时间
    }
}
