package eagetouch.anxi.com.eagetouch.utils;

import android.content.Context;
import android.os.Build;
import android.provider.Settings;

/**
 * Created by user on 5/8/18.
 */

public class PermissionUtils {

    public static boolean canDrawOverlays(Context context){
        if(Build.VERSION.SDK_INT < 23) return true;
        if(Settings.canDrawOverlays(context)){
            return true;
        }else{
            return false;
        }
    }
}
