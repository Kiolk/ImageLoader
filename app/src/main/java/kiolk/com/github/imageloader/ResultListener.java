package kiolk.com.github.imageloader;

import android.graphics.Bitmap;

/**
 * Created by yauhen on 24.11.17.
 */

public interface ResultListener {
//interface of listener
    void onImageLoaded(Bitmap bitmap);
    void onImageLoadError();
}
