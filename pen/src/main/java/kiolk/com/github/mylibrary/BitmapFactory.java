package kiolk.com.github.mylibrary;

import android.graphics.Bitmap;
import android.util.Log;

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

            options.inSampleSize = BitmapFactory.calculateInSimpleSize(options, 100, 100);
            Bitmap bmp = android.graphics.BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
            int size = bmp.getByteCount();
            Log.d(LOG, "Size of file: " + size);
            Log.d(LOG, "Height: " + options.outHeight + ". Width: " + options.outWidth + ". bmp: " + options.inBitmap);
//            bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, null);
            result.setBitmap(bmp);
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
