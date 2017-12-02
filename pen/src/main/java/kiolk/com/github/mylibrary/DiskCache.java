package kiolk.com.github.mylibrary;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

import kiolk.com.github.mylibrary.utils.LogUtil;

import static kiolk.com.github.mylibrary.utils.ConstantsUtil.*;

class DiskCache {

    private static final long MAXIMAL_CACHE_SIZE = 10 * 1024 * 1024;
    private static DiskCache mDiskCache;
    static final Object mLock = new Object();
    private static File mCacheDir;
    //    private Context mContext;
    private long mCurrentSizeCache;

    private DiskCache() {
    }

    static DiskCache getInstance() {

        if (mDiskCache == null) {
            mDiskCache = new DiskCache();
            LogUtil.msg("Create object of DiskCache");
        }

        return mDiskCache;
    }

    void setContext(Context pContext) {
//        this.mContext = pContext;
    }

    boolean saveBitmapInDiskCache(Bitmap pBitmap, String pName, Context pContext) {
        FileOutputStream fileOutputStream = null;

        if (mCacheDir == null) {
            getCacheDir(pContext);
        }

        File myPath = new File(mCacheDir, pName + STORAGE_FILE_FORMAT);
        myPath.setLastModified(System.currentTimeMillis());
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
                    keepSizeCacheFolder();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return isSaved;
    }

     Bitmap loadBitmapFromDiskCache(Context pContext, String pName) {
        File myPath = new File(mCacheDir, pName + STORAGE_FILE_FORMAT);
        Bitmap bitmap = null;

        try {
            bitmap = BitmapFactory.decodeStream(new FileInputStream(myPath));

//            return bitmap;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return bitmap;
    }

/*    private static File checkDestinationFolder(Context pContext) {
        File cachePath = pContext.getCacheDir();
        File imageFolder = new File(cachePath, "ImageCache");
        if (!imageFolder.exists()) {
            imageFolder.mkdir();
            mCacheDir = imageFolder;
            LogUtil.msg("Folder ImageCache created");
        }
        return imageFolder;
    }*/

    private void sizeCacheFolder() {
        File[] listFiles = mCacheDir.listFiles();
        long sizeCache = 0;

        for (File listFile : listFiles) {
            sizeCache += listFile.length();
            LogUtil.msg("Size of file " + listFile.getName() + " equal: " + listFile.length());
        }

        LogUtil.msg("Size of cache folder" + sizeCache);
        mCurrentSizeCache = sizeCache;
    }

    private void keepSizeCacheFolder() {
        sizeCacheFolder();
        if (mCurrentSizeCache > MAXIMAL_CACHE_SIZE) {
            File[] listFiles = mCacheDir.listFiles();
            ArrayList<File> arrayFiles = new ArrayList<>();
            arrayFiles.addAll(Arrays.asList(listFiles));

            Comparator<File> comparator = new Comparator<File>() {

                @Override
                public int compare(File o1, File o2) {
                    String lastModificationFile1 = "" + o1.lastModified();
                    String lastModificationFile2 = "" + o2.lastModified();

                    return lastModificationFile1.compareTo(lastModificationFile2);
                }
            };

            Collections.sort(arrayFiles, comparator);
            int i = 0;
            do {
                LogUtil.msg("File remove: " + arrayFiles.get(i).getName());
                boolean deleteResult = arrayFiles.get(i).delete();
                if (deleteResult) {
                    arrayFiles.remove(i);
                    ++i;
                    sizeCacheFolder();
                } else {
                    break;
                }
            } while (mCurrentSizeCache > MAXIMAL_CACHE_SIZE);
        }
    }

    private void getCacheDir(Context pContext) {
        File cachePath = pContext.getCacheDir();
        File imageFolder = new File(cachePath, "ImageCache");

        if (!imageFolder.exists()) {
            boolean isDirCreated = imageFolder.mkdir();
            if(isDirCreated) {
                mCacheDir = imageFolder;
                LogUtil.msg("Folder ImageCache created");
            }else{
                mCacheDir =cachePath;
                LogUtil.msg("Continue write in direct cache directory;");
            }
        } else {
            mCacheDir = imageFolder;
        }
    }
}
