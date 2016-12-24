package fragment;

import android.support.v4.app.Fragment;


import com.baidu.wjp.jvtc.R;

import java.util.HashMap;

/**
 *
 * Created by Administrator on 2016/8/12.
 */
public class FragmentFactory {
    private static HashMap<Integer, Fragment> mFragment = new HashMap<>();

    public static Fragment createFragment(int id) {
        BaseFragment fragment = (BaseFragment) mFragment.get(id);
        if (fragment == null) {
            switch (id) {
                case R.id.tab_home:
                    fragment = new HomeFragment();
                    break;
                case R.id.tab_school:
                    fragment = new SchoolFragment();
                    break;
                case R.id.tab_message:
                    fragment = new ContactFragment();
                    break;
                case R.id.tab_forum:
                    fragment = new ForumFragment();
                    break;
            }
            mFragment.put(id, fragment);
        }
        return fragment;
    }
}