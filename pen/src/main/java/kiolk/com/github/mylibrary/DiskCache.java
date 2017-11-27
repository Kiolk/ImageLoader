package kiolk.com.github.mylibrary;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by yauhen on 26.11.17.
 */

public class DiskCache {

    static Object lock = new Object();
    static Context context;

    public static boolean saveBitmapInDiskCache(Bitmap bitmap, String name, Context context) {
        FileOutputStream fileOutputStream = null;
        File directory = context.getFilesDir();
        File myPath = new File(directory, name + ".jpg");
        boolean result = false;

        try {
            fileOutputStream = new FileOutputStream(myPath);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fileOutputStream.close();
                return result;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public static Bitmap loadBitmapFromDiskCache(Context context, String name) {
        File directory = context.getFilesDir();
        File myPath = new File(directory, name + ".jpg");
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeStream(new FileInputStream(myPath));
            return bitmap;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    static void setContext(Context pContext){
        context = pContext;
    }

}
