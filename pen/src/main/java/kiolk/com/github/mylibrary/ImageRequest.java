package kiolk.com.github.mylibrary;

import android.widget.ImageView;

import java.lang.ref.WeakReference;

/**
 * Created by yauhen on 24.11.17.
 */

public class ImageRequest {

    private String mUrl;
    private WeakReference<ImageView> mTarget;
    private int mWidth;
    private int mHeight;

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

    public int getmWidth() {
        return mWidth;
    }

    public void setmWidth(int mWidth) {
        this.mWidth = mWidth;
    }

    public int getmHeight() {
        return mHeight;
    }

    public void setmHeight(int mHeight) {
        this.mHeight = mHeight;
    }
}
