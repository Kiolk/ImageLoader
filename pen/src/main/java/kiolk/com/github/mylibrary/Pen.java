package kiolk.com.github.mylibrary;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.util.LruCache;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import java.lang.ref.WeakReference;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;

import kiolk.com.github.mylibrary.utils.ConstantsUtil;
import kiolk.com.github.mylibrary.utils.ContextHolderUtil;
import kiolk.com.github.mylibrary.utils.LogUtil;

import static kiolk.com.github.mylibrary.utils.Utils.LOG;

public class Pen {

    public static final int WITHOUT_CACHE = 0;
    public static final int MEMORY_CACHE = 1;
    public static final int INNER_FILE_CACHE = 2;

    private BlockingDeque<ImageRequest> mQueue;
    private ExecutorService executor;
    private LruCache<String, Bitmap> mBitmapLruCache;
    int mTypeOfMemoryCache;
    Builder mBuilder;
    final Object mLock;

    private static Pen instance = null;

    private Pen() {
        mQueue = new LinkedBlockingDeque<>();
        executor = Executors.newFixedThreadPool(3);
        mLock = new Object();
        mBuilder = new Builder();
        mTypeOfMemoryCache = WITHOUT_CACHE;

        initialisationLruCache();
    }

    private void initialisationLruCache() {
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / ConstantsUtil.KILOBYTE_SIZE);
        final int cacheSize = maxMemory / ConstantsUtil.PART_OF_MEMORY_CACHE;

        Log.d(LOG, "maxMemory = " + maxMemory + ". MaxMemory from Runtime: "
                + Runtime.getRuntime().maxMemory() + ". CacheSize: " + cacheSize);
        LogUtil.msg("maxMemory = " + maxMemory + ". MaxMemory from Runtime: "
                + Runtime.getRuntime().maxMemory() + ". CacheSize: " + cacheSize);

        mBitmapLruCache = new LruCache<String, Bitmap>(cacheSize) {

            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getByteCount() / ConstantsUtil.KILOBYTE_SIZE;
            }


            //implement possibility save file from memory cache to DiskCache
           /* @Override
            protected void entryRemoved(boolean evicted, String key, Bitmap oldValue, Bitmap newValue) {

                synchronized (DiskCache.mLock){
                    Bitmap bmpBeforeRemove = mBitmapLruCache.get(key);
                    String name = Uri.parse(key).getLastPathSegment();
                    Context context = DiskCache.context;
                    DiskCache.saveBitmapInDiskCache(bmpBeforeRemove, name, context);
                    Log.d(LOG, "Save " + name + " to DiskCache");
                }
                super.entryRemoved(evicted, key, oldValue, newValue);
            }*/
        };
    }

    public static Pen getInstance() {
        if (instance == null) {
            instance = new Pen();
        }

        return instance;
    }

    public int getmTypeOfMemoryCache() {
        return mTypeOfMemoryCache;
    }
//
//    public void setmTypeOfMemoryCache(int mTypeOfMemoryCache) {
//        this.mTypeOfMemoryCache = mTypeOfMemoryCache;
//    }

    public class Builder {

        private Builder() {
        }

        private String mUrl;

        private void setmUrl(String mUrl) {
            this.mUrl = mUrl;
        }

        private Builder getBitmapFromUrl(String url) {
            setmUrl(url);

            return mBuilder;
        }

        public Builder setTypeOfCache(int pTypeOfCache) {
            if (pTypeOfCache >= WITHOUT_CACHE && pTypeOfCache <= INNER_FILE_CACHE) {
                mTypeOfMemoryCache = pTypeOfCache;
            }

            return mBuilder;
        }

        public void inputTo(ImageView pView) {
            WeakReference<ImageView> weakReference = new WeakReference<ImageView>(pView);
            ImageRequest imageRequest = new ImageRequest(mBuilder.mUrl, weakReference);
            Context context = pView.getContext();

            Pen.getInstance().enqueue(imageRequest);
            ContextHolderUtil.getInstance().setContext(context);
        }
    }

    public Builder getImageFromUrl(String url) {
        return mBuilder.getBitmapFromUrl(url);
    }

    private void enqueue(ImageRequest imageRequest) {

        ImageView imageView = imageRequest.getmTarget().get();

        if (imageView == null) {
            LogUtil.msg("Target image view not exist");
            Log.d(LOG, "Target image view not exist");

            return;
        }

        if (imageHasSize(imageRequest)) {
            imageView.setTag(imageRequest.getmUrl());
            mQueue.addFirst(imageRequest);
            Log.d(LOG, "Image view" + imageRequest.getmTarget().get().toString() + " start setup");
            LogUtil.msg("Image view" + imageRequest.getmTarget().get().toString() + " start setup");
            try {
                new ImageLoadingAsyncTask().execute(mQueue.takeFirst());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            waiterImageViewShow(imageRequest);
        }
    }

    private boolean imageHasSize(ImageRequest request) {

        if (request.getmHeight() > 0 && request.getmWidth() > 0) {
            return true;
        }

        ImageView view = request.getmTarget().get();

        if (view != null && view.getHeight() > 0 && view.getWidth() > 0) {
            int viewHeight = view.getHeight();
            int viewWidth = view.getWidth();

            request.setmHeight(viewHeight);
            request.setmWidth(viewWidth);

            return true;
        }

        return false;
    }

    private void waiterImageViewShow(final ImageRequest pRequest) {
        LogUtil.msg("Image view" + pRequest.getmTarget().get().toString() + " wait for draw");
        Log.d(LOG, "Image view" + pRequest.getmTarget().get().toString() + " wait for draw");

        ImageView viewWaitDraw = pRequest.getmTarget().get();

        viewWaitDraw.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {

            @Override
            public boolean onPreDraw() {
                ImageView v = pRequest.getmTarget().get();

                if (v == null) {
                    return true;
                }

//                v.getViewTreeObserver().removeOnPreDrawListener(this);
                if (v.getWidth() > 0 && v.getHeight() > 0) {
                    Log.d(LOG, "Image view" + pRequest.getmTarget().get().toString() + " start draw");
                    LogUtil.msg("Image view" + pRequest.getmTarget().get().toString() + " start draw");

                    pRequest.setmWidth(v.getWidth());
                    pRequest.setmHeight(v.getHeight());
                    enqueue(pRequest);
                    //correct variant for remove OnPreDrawListener
                    v.getViewTreeObserver().removeOnPreDrawListener(this);
                }

                return true;
            }
        });
    }

    //add bitmap for LruCache
    void addBitmapForLruCache(String key, Bitmap bitmap) {
        if (getBitmapFromLruCache(key) == null) {
            mBitmapLruCache.put(key, bitmap);
            Log.d(LOG, "Add bitmap by key: " + key);
            LogUtil.msg("Add bitmap by key: " + key);
        }
    }

    //get bitmap from LruCache
    Bitmap getBitmapFromLruCache(String key) {
        Log.d(LOG, "Try bitmap by key " + key);
        LogUtil.msg("Try bitmap by key " + key);

        return mBitmapLruCache.get(key);
    }
}
