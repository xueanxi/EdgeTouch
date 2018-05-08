package eagetouch.anxi.com.eagetouch.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by user on 5/8/18.
 */

public class ToastUtils {
    protected static Toast mToast;
    public static void toastShort(Context context, String content){
        if(mToast != null ){
            mToast.cancel();
        }

        mToast = Toast.makeText(context,content,Toast.LENGTH_SHORT);
        mToast.show();
    }

    public static void toastLong(Context context, String content){
        if(mToast != null ){
            mToast.cancel();
        }

        mToast = Toast.makeText(context,content,Toast.LENGTH_SHORT);
        mToast.show();
    }
}
