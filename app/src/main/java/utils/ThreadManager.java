package utils;

import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 线程池管理类
 * Created by Administrator on 2016/8/18.
 */
public class ThreadManager {
    private static ThreadPool sThreadPool;

    public static ThreadPool getThreadPool() {
        if (sThreadPool == null) {
            synchronized (ThreadManager.class) {
                if (sThreadPool == null) {
                    sThreadPool = new ThreadPool(10, 10);
                }
            }
        }
        return sThreadPool;
    }

    public static class ThreadPool {

        private ThreadPoolExecutor mPoolExecutor;
        private int coolPoolSize;
        private int maxPoolSize;


        public ThreadPool(int coolPoolSize, int maxPoolSize) {
            this.coolPoolSize = coolPoolSize;
            this.maxPoolSize = maxPoolSize;
        }

        public void execute(Runnable runnable) {
            if (mPoolExecutor == null) {
                mPoolExecutor = new ThreadPoolExecutor(coolPoolSize, maxPoolSize, 1L, TimeUnit.SECONDS, new LinkedBlockingDeque<Runnable>(), Executors.defaultThreadFactory());
            }
            mPoolExecutor.execute(runnable);
        }

        public void cancle(Runnable runnable) {
            if (mPoolExecutor != null) {
                mPoolExecutor.getQueue().remove(runnable);
            }
        }
    }
}