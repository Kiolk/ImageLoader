package kiolk.com.github.mylibrary;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import static kiolk.com.github.mylibrary.utils.ConstantsUtil.*;

class DiskCache {

    static final Object mLock = new Object();

    static boolean saveBitmapInDiskCache(Bitmap pBitmap, String pName, Context pContext) {
        FileOutputStream fileOutputStream = null;
        File directory = pContext.getFilesDir();
        File myPath = new File(directory, pName + STORAGE_FILE_FORMAT);
        boolean isSaved = false;

        try {
            fileOutputStream = new FileOutputStream(myPath);
            pBitmap.compress(Bitmap.CompressFormat.PNG, QUALITY_OF_COMPRESSION_BMP, fileOutputStream);
            isSaved = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return isSaved;
    }

    static Bitmap loadBitmapFromDiskCache(Context pContext, String pName) {
        File directory = pContext.getFilesDir();
        File myPath = new File(directory, pName + STORAGE_FILE_FORMAT);
        Bitmap bitmap = null;

        try {
            bitmap = BitmapFactory.decodeStream(new FileInputStream(myPath));

//            return bitmap;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return bitmap;
    }
}
