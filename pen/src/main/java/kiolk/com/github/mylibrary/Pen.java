package kiolk.com.github.mylibrary;

import java.util.concurrent.BlockingDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * Created by yauhen on 24.11.17.
 */

public class Pen {

    BlockingDeque<ImageRequest> queue;
    ExecutorService executor;

    private static Pen instance = null;

    protected Pen(){
        queue = new LinkedBlockingDeque<>();
        executor = Executors.newFixedThreadPool(3);
    }

    public static Pen getInstance(){
        if(instance == null){
            instance = new Pen();
        }
        return instance;
    }


    public void enqueue (ImageRequest imageRequest){
        queue.addFirst(imageRequest);
        try {
            new ImageLoadingAsyncTask().execute(queue.takeFirst());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }



}
