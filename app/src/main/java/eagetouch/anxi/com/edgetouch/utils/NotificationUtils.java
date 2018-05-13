package eagetouch.anxi.com.edgetouch.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import eagetouch.anxi.com.edgetouch.App;
import eagetouch.anxi.com.edgetouch.MainActivity;
import eagetouch.anxi.com.edgetouch.R;
import eagetouch.anxi.com.edgetouch.accessibility.AccessibilyUtils;

/**
 * Created by user on 11/29/17.
 */

public class NotificationUtils {
    static App mApp = App.getInstance();
    private static final String TAG= "=NotificationUtils";
    private static final int ID_ACCESSIBILITY_OFF= 1001;

    public static void sendAccessibilityOffNotification(){
        LogUtils.d(TAG,"sendAccessibilityOffNotification");
        Context context = mApp.getApplicationContext();
        if(AccessibilyUtils.isAccessibilitySettingsOn(context)){
            LogUtils.d(TAG,"sendAccessibilityOffNotification but has permisssion, so ignore it.");
            return;
        }
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        Notification.Builder builder = new Notification.Builder(context);
        builder.setSmallIcon(R.drawable.ic_phone_iphone_black_24dp);
        builder.setContentTitle(context.getString(R.string.app_name));
        builder.setContentText(context.getString(R.string.notification_accessibility_off));
        builder.setWhen(System.currentTimeMillis());         //发送时间
        builder.setDefaults(Notification.DEFAULT_ALL);      //设置默认的提示音，振动方式，灯光
        builder.setAutoCancel(true);                         //打开程序后图标消失

        //跳转活动
        Intent intent =new Intent (context,MainActivity.class);
        PendingIntent pi = PendingIntent.getActivities(context, 0, new Intent[]{intent}, PendingIntent.FLAG_CANCEL_CURRENT);
        builder.setContentIntent(pi);
        //创建通知栏对象，显示通知信息
        Notification notification = builder.build();
        notificationManager.notify(ID_ACCESSIBILITY_OFF, notification);
    }

    public static void cancerAccessibilityOffNotification(){
        LogUtils.d(TAG,"cancerAccessibilityOffNotification");
        Context context = mApp.getApplicationContext();
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        notificationManager.cancel(ID_ACCESSIBILITY_OFF);
    }
}
