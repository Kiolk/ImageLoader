package kiolk.com.github.mylibrary;

import android.graphics.Bitmap;

public class ImageResult {

    private ImageRequest mRequest;
    private Bitmap mBitmap;
    private Exception mException;

    public ImageResult(ImageRequest request) {
        this.mRequest = request;
    }

    public ImageRequest getmRequest() {
        return mRequest;
    }

    public void setmRequest(ImageRequest mRequest) {
        this.mRequest = mRequest;
    }

    public Bitmap getmBitmap() {
        return mBitmap;
    }

    public void setmBitmap(Bitmap mBitmap) {
        this.mBitmap = mBitmap;
    }

    public Exception getmException() {
        return mException;
    }

    public void setmException(Exception mException) {
        this.mException = mException;
    }
}
