package kiolk.com.github.mylibrary.utils;

import android.util.Log;

import kiolk.com.github.mylibrary.BuildConfig;

public final class LogUtil  {

    private LogUtil(){
    }

    public static void msg(String message){

        if(BuildConfig.DEBUG){
            Log.d(ConstantsUtil.Log.LOG_TAG, message);
        }
    }
}
