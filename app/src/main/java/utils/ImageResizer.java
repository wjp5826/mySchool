package utils;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.FileDescriptor;

/**
 * 图片加工类
 * Created by Administrator on 2016/5/27.
 */
public class ImageResizer {
    private final String TAG = "ImageResizer";

    /**
     * 从资源系统加载
     *
     * @param res
     * @param resId
     * @param reqWidth  视图的宽
     * @param reqHeight 视图的高
     * @return
     */
    public Bitmap decodeSampleBitmapFromResource(Resources res, int resId, int reqWidth, int reqHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    /**
     * 从文件系统加载
     *
     * @param fd
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    public Bitmap decodeSampleBitmapFromFile(FileDescriptor fd, int reqWidth, int reqHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFileDescriptor(fd, null, options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        return BitmapFactory.decodeFileDescriptor(fd, null, options);
    }

    private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        int sampleSize = 1;
        if (reqHeight == 0 || reqWidth == 0) {
            return sampleSize;
        }
        if (options.outHeight > reqHeight || options.outWidth > reqWidth) {
            int width = (int) ((float) reqWidth / (float) options.outWidth);
            int height = (int) ((float) reqHeight / (float) options.outHeight);
            sampleSize = (width + height) / 2;
        }
        return sampleSize;
    }
}