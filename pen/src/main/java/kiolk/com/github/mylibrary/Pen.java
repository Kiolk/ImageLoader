package kiolk.com.github.mylibrary;

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

import static kiolk.com.github.mylibrary.Utils.LOG;

/**
 * Created by yauhen on 24.11.17.
 */

public class Pen {

    public static final int WITHOUT_CACHE = 0;
    public static final int MEMORY_CACHE = 1;
    public static final int INNER_FILE_CACHE = 2;
    BlockingDeque<ImageRequest> queue;
    ExecutorService executor;
    static LruCache<String, Bitmap> bitmapLruCache;
    Object lock;
    boolean isMemoryCache = true;
    Builder builder;
    int typeOfMemoryCache;

    private static Pen instance = null;

    protected Pen() {
        queue = new LinkedBlockingDeque<>();
        executor = Executors.newFixedThreadPool(3);
        lock = new Object();
        builder = new Builder();
        typeOfMemoryCache = WITHOUT_CACHE;

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


            //implement possibility save file from memory cache to DiskCache
           /* @Override
            protected void entryRemoved(boolean evicted, String key, Bitmap oldValue, Bitmap newValue) {

                synchronized (DiskCache.lock){
                    Bitmap bmpBeforeRemove = bitmapLruCache.get(key);
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

    protected LruCache<String, Bitmap> getBitmapLruCache() {
        return bitmapLruCache;
    }

    public class Builder{

        public Builder() {
        }

        private String url;

        private void setUrl(String url) {
            this.url = url;
        }

        public Builder getBitmapFromUrl(String url){
            setUrl(url);
            return builder;
        }

        public Builder setTypeOfCache(int typeOfCache){
            if(typeOfCache >= WITHOUT_CACHE && typeOfCache <= INNER_FILE_CACHE){
                typeOfMemoryCache = typeOfCache;
            }
            return builder;
        }

        public void inputTo(ImageView view){
            WeakReference<ImageView> weakReference = new WeakReference<ImageView>(view);
            ImageRequest imageRequest = new ImageRequest(builder.url, weakReference);
            Pen.getInstance().enqueue(imageRequest);
        }
    }

    public Builder getImageFromUrl(String url){
        return builder.getBitmapFromUrl(url);
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
