package com.baidu.wjp.jvtc;

import android.app.ListActivity;
import android.os.Bundle;

import java.util.List;

import adapter.CommentListViewAdapter;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import domain.Comment;

/**
 * 作者：吴建平
 * 时间：2016/11/8.
 * 作用：
 */

public class CommentActivity extends ListActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String url = getIntent().getStringExtra(NewsDetailActivity.TO_COMMENT);
        requestComment(url);
    }
    private void requestComment(String url) {
        BmobQuery<Comment> queryComment = new BmobQuery<>();
        queryComment.addWhereEqualTo("url", url);
        queryComment.setLimit(100);
        queryComment.findObjects(new FindListener<Comment>() {
            @Override
            public void done(List<Comment> list, BmobException e) {
                CommentListViewAdapter adapter = new CommentListViewAdapter(list);
                adapter.notifyDataSetChanged();
                setListAdapter(adapter);
            }
        });
    }
}
