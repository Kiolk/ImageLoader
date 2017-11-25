package kiolk.com.github.mylibrary;

import android.widget.ImageView;

import java.lang.ref.WeakReference;

/**
 * Created by yauhen on 24.11.17.
 */

public class ImageRequest {

    private String mUrl;
    private WeakReference<ImageView> mTarget;
    private int width;
    private int height;

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

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}
