package kiolk.com.github.mylibrary;

import android.util.Log;
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

    private static Pen instance = null;

    protected Pen() {
        queue = new LinkedBlockingDeque<>();
        executor = Executors.newFixedThreadPool(3);
    }

    public static Pen getInstance() {
        if (instance == null) {
            instance = new Pen();
        }
        return instance;
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

                if(v.getWidth() > 0 && v.getHeight() > 0){
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


}
