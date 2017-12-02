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

import kiolk.com.github.mylibrary.ImageRequest;
import kiolk.com.github.mylibrary.Pen;

public class MainActivity extends AppCompatActivity {

    public static final String LOG = "MyLogs";
    public static final String URL = "https://www.w3schools.com/w3images/lights.jpg";
    public static final String URL1 = "https://nn.by/img/w662h445d1crop1/photos/z_2017_11/sluck2017-wfdy4.jpg";
    public static final String URL2 = "https://nn.by/img/w924d4/photos/z_2017_10/786a4922-l02ue.jpg";
    public static final String URL3 = "https://nn.by/img/w924d4/photos/z_2017_10/786a5165-xe0pd.jpg";


    ImageView mPhoto;
    ImageView mPhoto2;
    ImageView mPhoto3;
    ImageView mPhoto4;
    Button mButton;
    Button getmButtonShowView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mPhoto = (ImageView) findViewById(R.id.picture_image_view);
        mPhoto2 = findViewById(R.id.picture2_image_view);
        mButton = findViewById(R.id.button);
        getmButtonShowView = (Button) findViewById(R.id.button2);
        mPhoto3 = (ImageView) findViewById(R.id.picture3_image_view);
        mPhoto4 = (ImageView) findViewById(R.id.picture4_image_view);


        View.OnClickListener click = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                new ImageDownloadAsyncTask(new ResultImageLoaded()).execute(URL);
                switch (view.getId()) {
                    case R.id.button:
                        ImageRequest request = new ImageRequest(URL, new WeakReference<ImageView>(mPhoto));
//                        Pen pen = Pen.getInstance();
//                        pen.enqueue(request);
                        Pen.getInstance().setLoaderSettings()
                                .setSavingStrategy(Pen.SAVE_SCALING_IMAGE_STRATEGY)
                                .setTypeOfCache(Pen.INNER_FILE_CACHE)
                                .setSizeInnerFileCache(10L);

                        Pen.getInstance().getImageFromUrl(URL).inputTo(mPhoto);

//                        ImageRequest request2 = new ImageRequest(URL1, new WeakReference<ImageView>(mPhoto2));
//                        pen.enqueue(request2);

                        Pen.getInstance().getImageFromUrl(URL).inputTo(mPhoto2);
                     //   Pen.getInstance().getImageFromUrl(URL1).
//                        ImageRequest request3 = new ImageRequest(URL2, new WeakReference<ImageView>(mPhoto3));
//                        pen.enqueue(request3);
                        Pen.getInstance().getImageFromUrl(URL).inputTo(mPhoto3);

//                        ImageRequest request4 = new ImageRequest(URL3, new WeakReference<ImageView>(mPhoto4));
//                        pen.enqueue(request4);
                        Pen.getInstance().getImageFromUrl(URL).inputTo(mPhoto4);
                        break;
                    case R.id.button2:
                        Log.d(LOG, "Press button show view");
                        mPhoto3.getLayoutParams().height = 200;
                        mPhoto3.getLayoutParams().width = 200;
                        mPhoto3.requestLayout();
                        break;
                    default:
                        break;
                }
            }
        };
        mButton.setOnClickListener(click);
        getmButtonShowView.setOnClickListener(click);

//        ImageFactory bitmapFactory = new ImageFactory();
//        Bitmap bmp = ImageFactory.decodeResource(getResources(), R.drawable.patch);
//        int size = bmp.getByteCount();
//        Log.d(LOG, "Size of file: " + size);
//        mPhoto.setImageBitmap(bmp);
//        Bitmap bmp = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(getResources(), R.drawable.patch, options);
        Log.d(LOG, "Height: " + options.outHeight + ". Width: " + options.outWidth + ". bmp: " + options.inBitmap);
        options.inJustDecodeBounds = false;
        options.inSampleSize = calculateInSimpleSize(options, 100, 100);
        Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.patch, options);
        int size = bmp.getByteCount();
        Log.d(LOG, "Size of file: " + size);
        Log.d(LOG, "Height: " + options.outHeight + ". Width: " + options.outWidth + ". bmp: " + options.inBitmap);
        mPhoto.setImageBitmap(bmp);

    }

    public static int calculateInSimpleSize(BitmapFactory.Options pOptions, int height, int width) {
        int outHeight = pOptions.outHeight;
        int outWidth = pOptions.outWidth;
        int inSimpleSize = 1;

        if (outHeight > height || outWidth > width) {
            outHeight = outWidth / 2;
            outWidth = outWidth / 2;
            while ((outWidth / inSimpleSize) >= width && (outWidth / inSimpleSize) >= height) {
                outHeight = outWidth / 2;
                outWidth = outWidth / 2;
                inSimpleSize *= 2;
            }
            Log.d(LOG, "inSimpleSize: " + inSimpleSize);
        }
        return inSimpleSize;
    }

    public class ResultImageLoaded implements ResultListener {

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
