package kiolk.com.github.imageloader;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * Created by yauhen on 24.11.17.
 */

public class ImageDownloadAsyncTask extends AsyncTask<String, Void, Bitmap> {

    public static final String LOG = "MyLogs";
    //Create object of ResultListener
    ResultListener listener;

    public ImageDownloadAsyncTask(ResultListener listener) {
        //Initialized this object through constructor
        this.listener = listener;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);
        //Wrought logic with this listener object
        if (bitmap != null) {
            listener.onImageLoaded(bitmap);
        } else {
            listener.onImageLoadError();
        }


    }

    @Override
    protected Bitmap doInBackground(String... strings) {
        String url = strings[0];
        Bitmap bitmap = null;
        try {

            InputStream stream = new URL(url).openStream();
            ByteArrayOutputStream byteArrayInputStream = new ByteArrayOutputStream(stream.available());
            byte[] arrayByte = new byte[1024];
            int bytesRead;
            while ((bytesRead = stream.read(arrayByte)) > 0) {
                byteArrayInputStream.write(arrayByte, 0, bytesRead);
            }
            byte[] bytes = byteArrayInputStream.toByteArray();
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
            Log.d(LOG, "Height: " + options.outHeight + ". Width: " + options.outWidth + ". bmp: " + options.inBitmap);
            options.inJustDecodeBounds = false;
            options.inSampleSize = MainActivity.calculateInSimpleSize(options, 100, 100);
            Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
            int size = bmp.getByteCount();
            Log.d(LOG, "Size of file: " + size);
            Log.d(LOG, "Height: " + options.outHeight + ". Width: " + options.outWidth + ". bmp: " + options.inBitmap);
//            bitmap = ImageFactory.decodeByteArray(bytes, 0, bytes.length, null);
            return bmp;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }
}
