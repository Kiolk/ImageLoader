package kiolk.com.github.mylibrary;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import kiolk.com.github.mylibrary.utils.LogUtil;
import kiolk.com.github.mylibrary.utils.MD5Util;

import static kiolk.com.github.mylibrary.utils.ConstantsUtil.KILOBYTE_SIZE;
import static kiolk.com.github.mylibrary.utils.ConstantsUtil.LOG;

class ImageFactory {

    static ImageResult creteBitmapFromUrl(ImageResult pResult) {
        String url = pResult.getmRequest().getmUrl();

        switch (Pen.getInstance().getTypeOfMemoryCache()) {
            case Pen.MEMORY_CACHE:
                synchronized (Pen.getInstance().mLock) {

                    if (Pen.getInstance().getBitmapFromLruCache(url) != null) {
                        pResult.setmBitmap(Pen.getInstance().getBitmapFromLruCache(url));
                        Log.d(LOG, "Set bitmap from LruCache");
                        LogUtil.msg("Set bitmap from LruCache");

                        return pResult;
                    }
                }
                break;
            case Pen.INNER_FILE_CACHE:
                synchronized (DiskCache.getInstance().mLock) {
                    String name = getName(pResult);
                    Context context = pResult.getmRequest().getmTarget().get().getContext();
                    Bitmap bitmap = DiskCache.getInstance().loadBitmapFromDiskCache(context, name);

                    if (bitmap != null) {
                        pResult.setmBitmap(bitmap);
                        Log.d(LOG, "Set bitmap from DiskCache");
                        LogUtil.msg("Set bitmap from DiskCache");

                        return pResult;
                    }
                }
                break;
            default:
                break;
        }

        pResult = creteBitmap(pResult);

        switch (Pen.getInstance().getTypeOfMemoryCache()) {
            case Pen.MEMORY_CACHE:

                synchronized (Pen.getInstance().mLock) {
                    Pen.getInstance().addBitmapForLruCache(pResult.getmRequest().getmUrl(), pResult.getmBitmap());
                }

                break;
            case Pen.INNER_FILE_CACHE:

                synchronized (DiskCache.getInstance().mLock) {
                    boolean resultOfSave;

                    String name = getName(pResult);
                    Bitmap bitmap = pResult.getmBitmap();
                    Context context = pResult.getmRequest().getmTarget().get().getContext();

                    resultOfSave = DiskCache.getInstance().saveBitmapInDiskCache(bitmap, name, context);

                    if (resultOfSave) {
                        Log.d(LOG, "Save " + name + " to DiskCache");
                    }
                }

                break;
            default:
                break;
        }

        return pResult;
    }

    private static ImageResult creteBitmap(ImageResult pResult) {
        String url = pResult.getmRequest().getmUrl();
        int reqHeight = pResult.getmRequest().getmHeight();
        int reqWidth = pResult.getmRequest().getmWidth();

        try {
            int bytesRead;
            InputStream stream = new URL(url).openStream();
            ByteArrayOutputStream byteArrayInputStream = new ByteArrayOutputStream(stream.available());
            byte[] arrayByte = new byte[KILOBYTE_SIZE];

            while ((bytesRead = stream.read(arrayByte)) > 0) {
                byteArrayInputStream.write(arrayByte, 0, bytesRead);
            }

            byte[] bytes = byteArrayInputStream.toByteArray();

            if (Pen.getInstance().getStrategySaveImage() == Pen.SAVE_SCALING_IMAGE_STRATEGY) {
                Options options = new Options();
                options.inJustDecodeBounds = true;

                BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
                Log.d(LOG, "Height: " + options.outHeight + ". Width: " + options.outWidth + ". bmp: " + options.inBitmap);
                options.inJustDecodeBounds = false;
                options.inSampleSize = ImageFactory.calculateInSimpleSize(options, reqHeight, reqWidth);
                Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
                int size = bmp.getByteCount();
                Log.d(LOG, "Size of file: " + size);
                Log.d(LOG, "Height: " + options.outHeight + ". Width: " + options.outWidth + ". bmp: " + options.inBitmap);
                pResult.setmBitmap(bmp);
            } else {
                Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                LogUtil.msg("Size non scaled file = " + bmp.getByteCount());
                pResult.setmBitmap(bmp);
            }

            return pResult;
        } catch (IOException e) {
            e.printStackTrace();
            pResult.setmException(e);

            return pResult;
        }
    }

    private static int calculateInSimpleSize(Options pOptions, int pHeight, int pWidth) {
        int outHeight = pOptions.outHeight;
        int outWidth = pOptions.outWidth;
        int inSimpleSize = 1;

        if (outHeight > pHeight || outWidth > pWidth) {
            outWidth = outWidth / 2;
            outHeight = outWidth / 2;

            while ((outWidth / inSimpleSize) >= pWidth && (outWidth / inSimpleSize) >= pHeight) {
                outHeight = outWidth / 2;
                outWidth = outWidth / 2;
                inSimpleSize *= 2;
            }
            Log.d(LOG, "inSimpleSize: " + inSimpleSize);
        }

        return inSimpleSize;
    }

    private static String getName(ImageResult pResult) {
        return MD5Util.getHashString(pResult.getmRequest().getmUrl());
    }
}
