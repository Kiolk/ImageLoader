package kiolk.com.github.mylibrary;

import android.os.AsyncTask;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * Created by yauhen on 24.11.17.
 */

public class ImageLoadingAsyncTask extends AsyncTask<ImageRequest, Void, ImageResult> {
    @Override
    protected ImageResult doInBackground(ImageRequest... imageRequests) {

        ImageRequest request = imageRequests[0];
        ImageResult result = new ImageResult(request);
        return BitmapFactory.creteBitmapFromUrl(result);
    }

    @Override
    protected void onPostExecute(ImageResult imageResult) {
        super.onPostExecute(imageResult);
        if (imageResult.getBitmap() != null){
            ImageView imageView = imageResult.getRequest().getmTarget().get();
            imageView.setImageBitmap(imageResult.getBitmap());
        }else{
            //Not very good idea show toast from AsyncTask only for example
            Toast.makeText(imageResult.getRequest().getmTarget().get().getContext(),
                    imageResult.getException().getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}

