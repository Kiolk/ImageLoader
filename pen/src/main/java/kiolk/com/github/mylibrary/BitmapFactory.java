package kiolk.com.github.mylibrary;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import static kiolk.com.github.mylibrary.Utils.LOG;


/**
 * Created by yauhen on 24.11.17.
 */

public class BitmapFactory {


    public static ImageResult creteBitmapFromUrl(ImageResult result) {

        String url = result.getRequest().getmUrl();
        int reqHeight = result.getRequest().getHeight();
        int reqWidth = result.getRequest().getWidth();

        if (Pen.getInstance().typeOfMemoryCache == Pen.MEMORY_CACHE) {
            //check exist bitmap in LruCache and set on result
            synchronized (Pen.getInstance().lock) {
                if (Pen.getBitmapFromLruCache(url) != null) {
                    result.setBitmap(Pen.getBitmapFromLruCache(url));
                    Log.d(LOG, "Set bitmap from LruCache");
                    return result;
                }
            }
        }
        if (Pen.getInstance().typeOfMemoryCache == Pen.INNER_FILE_CACHE){
            //if file not present im memory cache find this file in DiskCache
            synchronized (DiskCache.lock) {
                String[] array = result.getRequest().getmUrl().split("/");
                int size = array.length;
                String name = array[size-1];
                Context context = result.getRequest().getmTarget().get().getContext();
                Bitmap bitmap = DiskCache.loadBitmapFromDiskCache(context, name);
                if (bitmap != null) {
                    result.setBitmap(bitmap);
                    Log.d(LOG, "Set bitmap from DiskCache");
                    return result;
                }
            }
        }

        try {

            InputStream stream = new URL(url).openStream();
            ByteArrayOutputStream byteArrayInputStream = new ByteArrayOutputStream(stream.available());
            byte[] arrayByte = new byte[1024];
            int bytesRead;

            while ((bytesRead = stream.read(arrayByte)) > 0) {
                byteArrayInputStream.write(arrayByte, 0, bytesRead);
            }

            byte[] bytes = byteArrayInputStream.toByteArray();
            android.graphics.BitmapFactory.Options options = new android.graphics.BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            android.graphics.BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
            Log.d(LOG, "Height: " + options.outHeight + ". Width: " + options.outWidth + ". bmp: " + options.inBitmap);
            options.inJustDecodeBounds = false;

            //TODO set possibility use size of view for bitmap

            options.inSampleSize = BitmapFactory.calculateInSimpleSize(options, reqHeight, reqWidth);
            Bitmap bmp = android.graphics.BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
            int size = bmp.getByteCount();
            Log.d(LOG, "Size of file: " + size);
            Log.d(LOG, "Height: " + options.outHeight + ". Width: " + options.outWidth + ". bmp: " + options.inBitmap);
//            bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, null);
            result.setBitmap(bmp);

            //add file to LruCache
            if (Pen.getInstance().typeOfMemoryCache == Pen.MEMORY_CACHE) {
                synchronized (Pen.getInstance().lock) {
                    Pen.addBitmapForLruCache(result.getRequest().getmUrl(), result.getBitmap());
                }
            }
            if (Pen.getInstance().typeOfMemoryCache == Pen.INNER_FILE_CACHE) {
                //set context for possibility use DiskCache
                synchronized (DiskCache.lock) {
//                if (DiskCache.context == null) {
//                    Context context = result.getRequest().getmTarget().get().getContext();
//                    DiskCache.setContext(context);
//                    Log.d(LOG, "Set context ");
//                }
                    String[] array = result.getRequest().getmUrl().split("/");
                    int sizeOfArray = array.length;
                    String name = array[sizeOfArray-1];
                    Bitmap bitmap = result.getBitmap();
                    Context context = result.getRequest().getmTarget().get().getContext();
                    boolean resultOfSave = false;
                    resultOfSave = DiskCache.saveBitmapInDiskCache(bitmap, name, context);
                    if (resultOfSave) {
                        Log.d(LOG, "Save " + name + " to DiskCache");
//                    Toast.makeText(context, "Seccesfull save data in internal storage by name : " + name, Toast.LENGTH_SHORT).show();
                    }
                }
            }
            return result;

        } catch (IOException e) {
            e.printStackTrace();
            result.setException(e);
            return result;
        }
    }


    public static int calculateInSimpleSize(android.graphics.BitmapFactory.Options pOptions, int height, int width) {
        int outHeight = pOptions.outHeight;
        int outWidth = pOptions.outWidth;
        int inSimpleSize = 1;

        if (outHeight > height || outWidth > width) {
            outHeight = outWidth / 2;
            outWidth = outWidth / 2;
            while ((outWidth / inSimpleSize) >= width && (outWidth / inSimpleSize) >= height) {
                outHeight = outWidth / 2;
                outWidth = outWidth / 2;
                inSimpleSize *= 2;
            }
            Log.d(LOG, "inSimpleSize: " + inSimpleSize);
        }
        return inSimpleSize;
    }
}
