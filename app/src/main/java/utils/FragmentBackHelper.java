package utils;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.KeyEvent;

import java.util.List;

/**
 * 作者：吴建平
 * 时间：2016/11/4.
 * 作用：处理返回事件的工具类
 */

public class FragmentBackHelper {

    /**
     * 处理返回事件
     *
     * @param fragmentManager
     * @return
     */
    public static boolean handleBackDown(int keycode, KeyEvent event, FragmentManager fragmentManager) {
        List<Fragment> fragments = fragmentManager.getFragments();
        if (fragments == null) {
            return false;
        }
        int fragmentCount = fragments.size();
        for (int i = fragmentCount; i > 0; i--) {
            Fragment child = fragments.get(i - 1);
            if (isFragmentHandleBackDown(keycode, event, child)) {
                return true;
            }
        }

        if (fragmentManager.getBackStackEntryCount() > 0) {
            fragmentManager.popBackStack();
            return true;
        }
        return false;
    }

    public static boolean handleBackDown(int keyCode, KeyEvent event, Fragment fragment) {
        return handleBackDown(keyCode, event, fragment.getChildFragmentManager());
    }

    public static boolean handleBackDown(int keyCode, KeyEvent event, FragmentActivity fragment) {
        return handleBackDown(keyCode, event, fragment.getSupportFragmentManager());
    }

    /**
     * 检查fragment是否消费了back事件
     *
     * @param fragment
     * @return
     */
    private static boolean isFragmentHandleBackDown(int keyCode,KeyEvent event, Fragment fragment) {
        return fragment != null
                && fragment.isVisible()
                && fragment.getUserVisibleHint()
                && fragment instanceof FragmentBackHandler
                && ((FragmentBackHandler) fragment).onBackDown(keyCode,event);
    }
}
