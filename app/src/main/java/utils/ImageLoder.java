package utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.StatFs;
import android.support.v4.util.LruCache;
import android.widget.ImageView;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 图片加载类
 * Created by 吴建平 on 2016/5/27.
 */
public class ImageLoder {

    //线程池相关的一些参数
    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();//CPU核心数
    private static final int CORE_POOL_SIZE = CPU_COUNT + 1;//线程池的核心线程数
    private static final int MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 1;//线程池最大容量
    private static final long KEEP_ALIVE = 10L;//线程闲置超时时长
    //磁盘缓存的参数
    private static final int DISK_CACHE_SIZE = 1024 * 1024 * 50;//磁盘缓存的大小
    private boolean mIsDiskLruCacheCreated = false;//磁盘缓存创建状态值

    private static final int DISK_CACHE_INDEX = 0;
    private static final int IO_BUFFERED_SIZE = 8 * 1024;//缓冲流大小


    private static final int MESSAGE_POST_RESULT = 1;//消息发送的

    private int position;

    //构造线程池
    private static final ThreadFactory sThreadFactory = new ThreadFactory() {
        //一种线程安全的加减操作
        private final AtomicInteger mCount = new AtomicInteger(1);

        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, "imageLoader" + mCount.getAndIncrement());
        }
    };

    public static final Executor THREAD_POOL_EXECUTOR = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE, TimeUnit.SECONDS, new LinkedBlockingDeque<Runnable>(), sThreadFactory);

    //处理消息
    private Handler mMainHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_POST_RESULT:
                    LoaderResult result = (LoaderResult) msg.obj;
                    ImageView img = result.img;
                    String uri = (String) img.getTag();
                    if (uri.equals(result.uri)) {
                        img.setImageBitmap(result.bitmap);
                    }
            }
        }
    };

    private Context mContext;
    private ImageResizer imageResizer = new ImageResizer();
    private LruCache<String, Bitmap> mMemoryCache;
    private DiskLruCache mDiskLruCache;

    /**
     * 构造方法，主要用于内存缓存和磁盘缓存的创建
     *
     * @param mContext
     */
    public ImageLoder(Context mContext) {
        this.mContext = mContext;
        //内存缓存的创建
        //设置内存缓存的最大缓存为
        int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 2014);
        int chacheSize = maxMemory / 8;
        mMemoryCache = new LruCache<String, Bitmap>(chacheSize) {
            //测量bitmap的大小
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getRowBytes() * value.getHeight() / 1024;
            }
        };
        //磁盘缓存的创建
        File diskChacheDir = getDiskChacheDir(mContext, "bitmap");
        if (!diskChacheDir.exists()) {
            diskChacheDir.mkdir();
        }
        if (getUsableSpace(diskChacheDir) > DISK_CACHE_SIZE) {
            try {
                mDiskLruCache = DiskLruCache.open(diskChacheDir, 1, 1, DISK_CACHE_SIZE);
            } catch (IOException e) {
                e.printStackTrace();
                mIsDiskLruCacheCreated = false;
            }
        }

    }

    /**
     * 创建一个ImagerLoader的新对象
     *
     * @param context
     * @return
     */
    public static ImageLoder build(Context context) {
        return new ImageLoder(context);
    }

    //内存缓存的添加
    private void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        mMemoryCache.put(key, bitmap);
    }

    /**
     * 从内存缓存中获取
     *
     * @param key
     * @return
     */
    private Bitmap getBitmapFromMemoCache(String key) {
        return mMemoryCache.get(key);
    }

    //异步加载接口设计
    public void bindBitmap(final String uri, final ImageView imageView) {
        bindBitmap(uri, imageView, 0, 0);
    }

    public void bindBitmap(final String uri, final ImageView imageView, final int reqWidth, final int reqHeight) {
        imageView.setTag(uri);
        Bitmap bitmap = loadBitmapFromMemory(uri);
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
            return;
        }
        Runnable loadBitmapTask = new Runnable() {
            @Override
            public void run() {
                Bitmap bitmap = loadBitmap(uri, reqWidth, reqHeight);
                if (bitmap != null) {
                    LoaderResult result = new LoaderResult(imageView, uri, bitmap);
                    mMainHandler.obtainMessage(MESSAGE_POST_RESULT, result).sendToTarget();
                }
            }
        };
        THREAD_POOL_EXECUTOR.execute(loadBitmapTask);
    }

    //同步加载接口设计
    private Bitmap loadBitmap(String uri, int reqWidth, int reqHeight) {
        //尝试从内存缓存中加载
        Bitmap bitmap = loadBitmapFromMemory(uri);
        if (bitmap != null) {
            return bitmap;
        }
        //尝试从磁盘缓存中加载
        bitmap = loadBitmapFromDiskCache(uri, reqWidth, reqHeight);
        if (bitmap != null) {
            return bitmap;
        }
        try {
            //尝试从网络下载，然后存到磁盘中
            bitmap = loadBitmapFromHttp(uri, reqWidth, reqHeight);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //尝试从网络直接下载
        if (bitmap == null && !mIsDiskLruCacheCreated) {
            bitmap = downloadFromUrl(uri);
        }
        return bitmap;
    }

    private Bitmap downloadFromUrl(String uri) {
        Bitmap bitmap = null;
        HttpURLConnection connection = null;
        BufferedInputStream in = null;

        try {
            URL url = new URL(uri);
            connection = (HttpURLConnection) url.openConnection();
            in = new BufferedInputStream(connection.getInputStream(), IO_BUFFERED_SIZE);
            bitmap = BitmapFactory.decodeStream(in);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }


        }
        return bitmap;
    }

    /**
     * 从网络加载然后放到磁盘中去
     *
     * @param uri
     * @param reqWidth
     * @param reqHeight
     * @return
     * @throws IOException
     */
    private Bitmap loadBitmapFromHttp(String uri, int reqWidth, int reqHeight) throws IOException {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            throw new RuntimeException("不能在主线程中访问网络");
        }
        if (mDiskLruCache == null) {
            return null;
        }
        String key = hashKeyFromUrl(uri);
        DiskLruCache.Editor editor = mDiskLruCache.edit(key);
        if (editor != null) {
            OutputStream outputStream = editor.newOutputStream(DISK_CACHE_INDEX);
            if (downloadUrlToStream(uri, outputStream)) {
                editor.commit();
            } else {
                editor.abort();
            }
            mDiskLruCache.flush();
        }
        return loadBitmapFromDiskCache(uri, reqWidth, reqHeight);
    }

    /**
     * 从连接获取到资源的流
     *
     * @param urlString    图片链接
     * @param outputstream 图片的输出流
     * @return
     */
    private boolean downloadUrlToStream(String urlString, final OutputStream outputstream) {
        //创建okHttpClient对象
        OkHttpClient mOKHttpClient = new OkHttpClient();
        //创建一个request
        final Request mRequest = new Request.Builder().url(urlString).build();
        // new call
        Call call = mOKHttpClient.newCall(mRequest);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    BufferedInputStream in;
                    BufferedOutputStream out;

                    in = new BufferedInputStream(response.body().byteStream());
                    out = new BufferedOutputStream(outputstream, IO_BUFFERED_SIZE);

                    int b;

                    while ((b = in.read()) != -1) {
                        out.write(b);
                        out.flush();
                    }

                }
            }


        });
        return true;
    }

    /**
     * 磁盘缓存的查找
     *
     * @param uri
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    private Bitmap loadBitmapFromDiskCache(String uri, int reqWidth, int reqHeight) {
        if (mDiskLruCache == null) {
            return null;
        }
        Bitmap bitmap = null;
        String key = hashKeyFromUrl(uri);

        try {
            DiskLruCache.Snapshot snapshot = mDiskLruCache.get(key);
            if (snapshot != null) {
                FileInputStream fileInputStream = (FileInputStream) snapshot.getInputStream(DISK_CACHE_INDEX);
                FileDescriptor fileDescriptor = fileInputStream.getFD();
                bitmap = imageResizer.decodeSampleBitmapFromFile(fileDescriptor, reqWidth, reqHeight);
                if (bitmap != null) {
                    addBitmapToMemoryCache(key, bitmap);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bitmap;
    }

    private Bitmap loadBitmapFromMemory(String url) {
        final String key = hashKeyFromUrl(url);
        Bitmap bitmap = getBitmapFromMemoCache(key);
        return bitmap;
    }

    /**
     * 将URL转换成MD5文件
     *
     * @param url
     * @return
     */
    private String hashKeyFromUrl(String url) {
        String cacheKey;
        try {
            final MessageDigest mDigest = MessageDigest.getInstance("MD5");
            mDigest.update(url.getBytes());
            cacheKey = bytesToHexString(mDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            cacheKey = String.valueOf(url.hashCode());
        }
        return cacheKey;
    }

    //
    private String bytesToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            //将指定的int类型的数转换为十六进制字符串
            String hex = Integer.toHexString(0xFF & bytes[i]);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }

    /**
     * 创建文件
     *
     * @param context
     * @param uniqueName
     * @return
     */
    public File getDiskChacheDir(Context context, String uniqueName) {
        //外部存储是否可用
        boolean externalStorageAvailable = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        final String cachePath;
        if (externalStorageAvailable) {
            cachePath = context.getExternalCacheDir().getPath();
        } else {
            cachePath = context.getCacheDir().getPath();
        }
        return new File(cachePath + File.separator + uniqueName);
    }

    /**
     * 获取相应文件下的可用空间大小
     *
     * @param path
     * @return
     */
    private long getUsableSpace(File path) {
        //如果api大于或等于9，则使用如下方法
        //获取手机可用空间大小
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            return path.getUsableSpace();
        }
        //当api小于9，使用如下方法
        final StatFs statFs = new StatFs(path.getPath());
        long blockSize = statFs.getBlockSize();//每个block所占的字节数
        long availableBlocks = statFs.getAvailableBlocks();//可用的block
        return blockSize * availableBlocks;
    }

    private static class LoaderResult {
        public ImageView img;
        public String uri;
        public Bitmap bitmap;

        public LoaderResult(ImageView img, String uri, Bitmap bitmap) {
            this.img = img;
            this.uri = uri;
            this.bitmap = bitmap;
        }
    }
}
