package kiolk.com.github.mylibrary;

import android.os.AsyncTask;
import android.widget.ImageView;
import android.widget.Toast;

public class ImageLoadingAsyncTask extends AsyncTask<ImageRequest, Void, ImageResult> {

    @Override
    protected ImageResult doInBackground(ImageRequest... pImageRequests) {
        ImageRequest request = pImageRequests[0];
        ImageResult result = new ImageResult(request);

        return ImageFactory.creteBitmapFromUrl(result);
    }

    @Override
    protected void onPostExecute(ImageResult pImageResult) {
        super.onPostExecute(pImageResult);

        if (pImageResult.getmBitmap() != null){
            ImageView imageView = pImageResult.getmRequest().getmTarget().get();
            if(imageView.getTag().equals(pImageResult.getmRequest().getmUrl())) {
                imageView.setImageBitmap(pImageResult.getmBitmap());
            }
        }else{
            //Not very good idea show toast from AsyncTask only for example
            Toast.makeText(pImageResult.getmRequest().getmTarget().get().getContext(),
                    pImageResult.getmException().getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}

