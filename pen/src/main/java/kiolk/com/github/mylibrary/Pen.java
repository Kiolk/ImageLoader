package kiolk.com.github.mylibrary;

import android.graphics.Bitmap;
import android.util.Log;
import android.util.LruCache;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import java.util.concurrent.BlockingDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;

import static kiolk.com.github.mylibrary.Utils.LOG;

/**
 * Created by yauhen on 24.11.17.
 */

public class Pen {

    BlockingDeque<ImageRequest> queue;
    ExecutorService executor;
    static LruCache<String, Bitmap> bitmapLruCache;
    Object lock;

    private static Pen instance = null;

    protected Pen() {
        queue = new LinkedBlockingDeque<>();
        executor = Executors.newFixedThreadPool(3);
        lock = new Object();

        //initialization of LruCache
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory / 4;
        Log.d(LOG, "maxMemory = " + maxMemory + ". MaxMemory from Runtime: "
                + Runtime.getRuntime().maxMemory() + ". CacheSize: " + cacheSize);
        bitmapLruCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getByteCount() / 1024;
            }
        };
    }

    public static Pen getInstance() {
        if (instance == null) {
            instance = new Pen();
        }
        return instance;
    }

    protected LruCache<String, Bitmap> getBitmapLruCache() {
        return bitmapLruCache;
    }

    public void enqueue(ImageRequest imageRequest) {

        ImageView imageView = imageRequest.getmTarget().get();

        if (imageView == null) {
            Log.d(LOG, "Target image view not exist");
            return;

        }

        if (imageHasSize(imageRequest)) {
            imageView.setTag(imageRequest.getmUrl());
            queue.addFirst(imageRequest);
            Log.d(LOG, "Image view" + imageRequest.getmTarget().get().toString() + " start setup");
            try {
                new ImageLoadingAsyncTask().execute(queue.takeFirst());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            waiterImageViewShow(imageRequest);
        }
    }

    private boolean imageHasSize(ImageRequest request) {
        if (request.getHeight() > 0 && request.getWidth() > 0) {
            return true;
        }
        ImageView view = request.getmTarget().get();
        if (view != null && view.getHeight() > 0 && view.getWidth() > 0) {
            int viewHeight = view.getHeight();
            int viewWidth = view.getWidth();
            request.setHeight(viewHeight);
            request.setWidth(viewWidth);
            return true;
        }
        return false;
    }

    private void waiterImageViewShow(final ImageRequest request) {
        Log.d(LOG, "Image view" + request.getmTarget().get().toString() + " wait for draw");
        ImageView viewWaiterDraw = request.getmTarget().get();
        viewWaiterDraw.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                ImageView v = request.getmTarget().get();
                if (v == null) {
                    return true;
                }

                //???? Why show before if?
//                v.getViewTreeObserver().removeOnPreDrawListener(this);

                if (v.getWidth() > 0 && v.getHeight() > 0) {
                    Log.d(LOG, "Image view" + request.getmTarget().get().toString() + " start draw");
                    request.setWidth(v.getWidth());
                    request.setHeight(v.getHeight());
                    enqueue(request);
                    //correct variant for remove OnPreDrawListener
                    v.getViewTreeObserver().removeOnPreDrawListener(this);

                }
                return true;
            }
        });
    }

    //add bitmap for LruCache
    protected static void addBitmapForLruCache(String key, Bitmap bitmap) {
        if (getBitmapFromLruCache(key) == null) {
            bitmapLruCache.put(key, bitmap);
            Log.d(LOG, "Add bitmap by key: " + key);
        }
    }

    //get bitmap from LruCache
    protected static Bitmap getBitmapFromLruCache(String key){
        Log.d(LOG, "Try bitmap by key " + key);
        return bitmapLruCache.get(key);
    }
}
