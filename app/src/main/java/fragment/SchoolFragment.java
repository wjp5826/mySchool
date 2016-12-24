package fragment;

import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.baidu.wjp.jvtc.R;
import com.wenchao.cardstack.CardStack;

import java.io.File;
import java.util.List;

import adapter.CardStackAdapter;
import utils.util;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.DownloadFileListener;
import cn.bmob.v3.listener.FindListener;
import domain.school;


/**
 * 校园类
 * Created by Administrator on 2016/8/15.
 */
public class SchoolFragment extends BaseFragment {

    private CardStack mCardStack;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_school, null);
        initView(view);
        BmobQuery<school> query = new BmobQuery<>();
        showProgress();
        query.findObjects(new FindListener<school>() {
            @Override
            public void done(List<school> list, BmobException e) {
                if(e == null){
                    if(list.size() >0){
                        mCardStack.setAdapter(new CardStackAdapter(getActivity(), R.layout.item_school, list));
                        closeProgress();
                    }
                }else{
                    util.showMsg("我们的后台君养伤中...");
                }
            }

        });

        return view;
    }

    private void initView(View view) {
        mCardStack = (CardStack) view.findViewById(R.id.school_content);

    }

    @Override
    protected void refreshView(Object data) {

    }

    private void downloadFile(BmobFile file) {
        //允许设置下载文件的存储路径，默认下载文件的目录为：context.getApplicationContext().getCacheDir()+"/bmob/"
        File saveFile = new File(Environment.getExternalStorageDirectory(), file.getFilename());
        file.download(saveFile, new DownloadFileListener() {

            @Override
            public void onStart() {
                util.showMsg("开始下载...");
            }

            @Override
            public void done(String savePath, BmobException e) {
                if (e == null) {
                    util.showMsg("下载成功,保存路径:" + savePath);
                } else {
                    util.showMsg("下载失败：" + e.getErrorCode() + "," + e.getMessage());
                }
            }

            @Override
            public void onProgress(Integer value, long newworkSpeed) {
                Log.i("bmob", "下载进度：" + value + "," + newworkSpeed);
            }

        });
    }
}