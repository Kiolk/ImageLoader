package kiolk.com.github.mylibrary.utils;

import android.content.Context;

public class ContextHolderUtil {

    private static ContextHolderUtil mContextHolder = null;
    private Context mContext;

    private ContextHolderUtil() {
    }

    public static ContextHolderUtil getInstance() {

        if (mContextHolder == null) {
            mContextHolder = new ContextHolderUtil();
        }
        return mContextHolder;
    }

    public void setContext(Context pContest){
        mContextHolder.mContext = pContest;
    }

    public Context getContext(){
        return mContextHolder.mContext;
    }
}
