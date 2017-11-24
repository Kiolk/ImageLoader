package kiolk.com.github.imageloader;

import android.widget.ImageView;

import java.lang.ref.WeakReference;

/**
 * Created by yauhen on 24.11.17.
 */

public class ImageRequest {

    private String mUrl;
    private WeakReference<ImageView> mTarget;

    public ImageRequest(String mUrl, WeakReference<ImageView> mTarget) {
        this.mUrl = mUrl;
        this.mTarget = mTarget;
    }

    public String getmUrl() {
        return mUrl;
    }

    public void setmUrl(String mUrl) {
        this.mUrl = mUrl;
    }

    public WeakReference<ImageView> getmTarget() {
        return mTarget;
    }

    public void setmTarget(WeakReference<ImageView> mTarget) {
        this.mTarget = mTarget;
    }
}
