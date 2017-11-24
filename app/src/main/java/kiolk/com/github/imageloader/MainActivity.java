package kiolk.com.github.imageloader;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.lang.ref.WeakReference;

public class MainActivity extends AppCompatActivity {

    public static final String LOG = "MyLogs";
    public static final String URL = "https://www.w3schools.com/w3images/lights.jpg";
    ImageView mPhoto;
    ImageView mPhoto2;
    Button mButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mPhoto = (ImageView) findViewById(R.id.picture_image_view);
        mPhoto2 = findViewById(R.id.picture2_image_view);
        mButton = findViewById(R.id.button);


        View.OnClickListener click = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                new ImageDownloadAsyncTask(new ResultImageLoaded()).execute(URL);

                ImageRequest request = new ImageRequest(URL, new WeakReference<ImageView>(mPhoto));
                Pen pen = Pen.getInstance();
                pen.enqueue(request);

            }
        };
        mButton.setOnClickListener(click);

//        BitmapFactory bitmapFactory = new BitmapFactory();
//        Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.patch);
//        int size = bmp.getByteCount();
//        Log.d(LOG, "Size of file: " + size);
//        mPhoto.setImageBitmap(bmp);
//        Bitmap bmp = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(getResources(), R.drawable.patch, options);
        Log.d(LOG, "Height: " + options.outHeight + ". Width: " + options.outWidth + ". bmp: " + options.inBitmap);
        options.inJustDecodeBounds=false;
        options.inSampleSize = calculateInSimpleSize(options, 100, 100);
        Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.patch, options);
        int size = bmp.getByteCount();
        Log.d(LOG, "Size of file: " + size);
        Log.d(LOG, "Height: " + options.outHeight + ". Width: " + options.outWidth + ". bmp: " + options.inBitmap);
        mPhoto.setImageBitmap(bmp);

    }

    public static int calculateInSimpleSize (BitmapFactory.Options pOptions, int height, int width){
        int outHeight = pOptions.outHeight;
        int outWidth = pOptions.outWidth;
        int inSimpleSize = 1;

        if(outHeight > height || outWidth > width){
            outHeight = outWidth/2;
            outWidth = outWidth/2;
            while ((outWidth/inSimpleSize) >= width && (outWidth/inSimpleSize) >= height){
                outHeight = outWidth/2;
                outWidth = outWidth/2;
                inSimpleSize *= 2;
            }
            Log.d(LOG, "inSimpleSize: " + inSimpleSize);
        }
        return inSimpleSize;
    }

    public class ResultImageLoaded implements ResultListener{

        // implement of interface of ResultListener and wrought how it change main activity

        @Override
        public void onImageLoaded(Bitmap bitmap) {
            BitmapFactory.Options options = new BitmapFactory.Options();

            mPhoto.setImageBitmap(bitmap);
        }

        @Override
        public void onImageLoadError() {
            Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_LONG).show();
        }
    }
}