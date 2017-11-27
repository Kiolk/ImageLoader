package kiolk.com.github.diskcache;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOError;
import java.io.IOException;

/**
 * Created by yauhen on 26.11.17.
 */

public class DiskCache {


    static DiskCache diskCache;
    public Object lock;

    private DiskCache() {
        lock = new Object();
    }

    public static DiskCache getInstance() {
        if (diskCache == null) {
            diskCache = new DiskCache();
        }
        return diskCache;
    }

    public boolean saveBitmapInDiskCache(Bitmap bitmap, String name, Context context) {
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

    public Bitmap loadBitmapFromDiskCache(Context context, String name) {
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
}
