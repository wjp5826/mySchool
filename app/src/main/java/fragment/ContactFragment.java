package fragment;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.baidu.wjp.jvtc.R;

import java.util.List;

import adapter.MessageListAdapter;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import domain.Message;

/**
 * 作者：吴建平
 * 时间：2016/10/21.
 * 作用：
 */

public class ContactFragment extends BaseFragment {

    private ListView mMsgList;//消息列表

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contact,null);
        initView(view);
        setEvent();
        return view;
    }

    private void setEvent() {
        BmobQuery<Message> query = new BmobQuery<>();
        showProgress();
        query.findObjects(new FindListener<Message>() {
            @Override
            public void done(List<Message> list, BmobException e) {
                mMsgList.setAdapter(new MessageListAdapter(list));
                closeProgress();
            }
        });
    }

    private void initView(View view) {
        mMsgList = (ListView) view.findViewById(R.id.message_listview);
        mMsgList.setDivider(null);
        mMsgList.setSelector(new ColorDrawable());
    }

    @Override
    protected void refreshView(Object data) {

    }
}
