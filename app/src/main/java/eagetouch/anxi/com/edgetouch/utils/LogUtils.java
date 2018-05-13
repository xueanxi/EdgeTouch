package eagetouch.anxi.com.edgetouch.utils;

import android.util.Log;

/**
 * Created by user on 11/29/17.
 */

public class LogUtils {
    private static boolean mIsLog = true;
    private static final String TAG= "LogUtils";
    private static final String GLOBLE_TAG= "an_xi";

    public static void d(String tag,String content){
        if(mIsLog){
            Log.d(GLOBLE_TAG+" "+tag,content);
            //Log.d(TAG,tag+" "+content);
        }
    }

    public static void e(String tag,String content){
        if(mIsLog){
            Log.e(GLOBLE_TAG+" "+tag,content);
            //Log.e(TAG,content);
        }
    }
}
