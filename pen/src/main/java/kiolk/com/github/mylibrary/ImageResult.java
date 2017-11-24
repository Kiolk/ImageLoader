package kiolk.com.github.mylibrary;

import android.graphics.Bitmap;

/**
 * Created by yauhen on 24.11.17.
 */

public class ImageResult {

    private ImageRequest request;
    private Bitmap bitmap;
    private Exception exception;

    public ImageResult(ImageRequest request) {
        this.request = request;
    }

    public ImageRequest getRequest() {
        return request;
    }

    public void setRequest(ImageRequest request) {
        this.request = request;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }
}
