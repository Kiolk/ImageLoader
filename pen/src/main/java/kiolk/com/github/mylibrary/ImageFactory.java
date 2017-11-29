package kiolk.com.github.mylibrary;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.graphics.BitmapFactory.Options;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import kiolk.com.github.mylibrary.utils.LogUtil;

import static kiolk.com.github.mylibrary.utils.ConstantsUtil.*;
import static kiolk.com.github.mylibrary.utils.Utils.LOG;

public class ImageFactory {

    public static ImageResult creteBitmapFromUrl(ImageResult pResult) {
        String url = pResult.getmRequest().getmUrl();
        int reqHeight = pResult.getmRequest().getmHeight();
        int reqWidth = pResult.getmRequest().getmWidth();


        switch (Pen.getInstance().getmTypeOfMemoryCache()) {
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
                synchronized (DiskCache.mLock) {
                    String name = getName(pResult);
                    Context context = pResult.getmRequest().getmTarget().get().getContext();
                    Bitmap bitmap = DiskCache.loadBitmapFromDiskCache(context, name);

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

        try {
            int bytesRead;

            InputStream stream = new URL(url).openStream();
            ByteArrayOutputStream byteArrayInputStream = new ByteArrayOutputStream(stream.available());
            byte[] arrayByte = new byte[KILOBYTE_SIZE];

            while ((bytesRead = stream.read(arrayByte)) > 0) {
                byteArrayInputStream.write(arrayByte, 0, bytesRead);
            }

            byte[] bytes = byteArrayInputStream.toByteArray();
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

            switch (Pen.getInstance().getmTypeOfMemoryCache()) {
                case Pen.MEMORY_CACHE:

                    synchronized (Pen.getInstance().mLock) {
                        Pen.getInstance().addBitmapForLruCache(pResult.getmRequest().getmUrl(), pResult.getmBitmap());
                    }

                    break;
                case Pen.INNER_FILE_CACHE:

                    synchronized (DiskCache.mLock) {
                        boolean resultOfSave;

                        String name = getName(pResult);
                        Bitmap bitmap = pResult.getmBitmap();
                        Context context = pResult.getmRequest().getmTarget().get().getContext();

                        resultOfSave = DiskCache.saveBitmapInDiskCache(bitmap, name, context);

                        if (resultOfSave) {
                            Log.d(LOG, "Save " + name + " to DiskCache");
                        }
                    }

                    break;
                default:
                    break;
            }

            return pResult;
        } catch (IOException e) {
            e.printStackTrace();
            pResult.setmException(e);

            return pResult;
        }
    }


    public static int calculateInSimpleSize(Options pOptions, int pHeight, int pWidth) {
        int outHeight = pOptions.outHeight;
        int outWidth = pOptions.outWidth;
        int inSimpleSize = 1;

        if (outHeight > pHeight || outWidth > pWidth) {
            outHeight = outWidth / 2;
            outWidth = outWidth / 2;
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
        String[] arrayFromUrl = pResult.getmRequest().getmUrl().split(SPLITTING_BY_SLASH);
        int size = arrayFromUrl.length;
        String name = arrayFromUrl[size - 1];

        return name;
    }
}
