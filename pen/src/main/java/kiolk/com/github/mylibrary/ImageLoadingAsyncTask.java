package kiolk.com.github.mylibrary;

import android.os.AsyncTask;
import android.widget.ImageView;
import android.widget.Toast;

import kiolk.com.github.mylibrary.utils.LogUtil;
import kiolk.com.github.mylibrary.utils.MD5Util;

public class ImageLoadingAsyncTask extends AsyncTask<ImageRequest, Void, ImageResult> {

    @Override
    protected ImageResult doInBackground(ImageRequest... pImageRequests) {
        LogUtil.msg("Started thread :" + Thread.currentThread().getName());
        ImageRequest request = pImageRequests[0];
        ImageResult result = new ImageResult(request);

        return ImageFactory.creteBitmapFromUrl(result);
    }

    @Override
    protected void onPostExecute(ImageResult pImageResult) {
        super.onPostExecute(pImageResult);

        if (pImageResult.getmBitmap() != null){
            ImageView imageView = pImageResult.getmRequest().getmTarget().get();
            String tag = MD5Util.getHashString(pImageResult.getmRequest().getmUrl());
            LogUtil.msg("Compare between " + tag + " and " + imageView.getTag());

            if(imageView.getTag().equals(tag)) {
                imageView.setImageBitmap(pImageResult.getmBitmap());
                LogUtil.msg("set bmp from cache" + pImageResult.getmRequest().getmUrl()+ MD5Util.getHashString(pImageResult.getmRequest().getmUrl()));
            }

        }else{
            //Not very good idea show toast from AsyncTask only for example
            Toast.makeText(pImageResult.getmRequest().getmTarget().get().getContext(),
                    pImageResult.getmException().getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}

