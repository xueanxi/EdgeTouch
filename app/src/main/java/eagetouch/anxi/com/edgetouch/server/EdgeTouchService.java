package eagetouch.anxi.com.edgetouch.server;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import eagetouch.anxi.com.edgetouch.EdgeTouchView;
import eagetouch.anxi.com.edgetouch.MainActivity;
import eagetouch.anxi.com.edgetouch.R;
import eagetouch.anxi.com.edgetouch.utils.LogUtils;
import eagetouch.anxi.com.edgetouch.utils.NotificationUtils;


/**
 * Created by user on 11/29/17.
 */

public class EdgeTouchService extends Service {
    private static final String TAG = "=EdgeTouchService";

    public static final String ACTION_START_FLOAT_VIEW = "ACTION_START_FLOAT_VIEW";
    public static final String ACTION_STOP_FLOAT_VIEW = "ACTION_STOP_FLOAT_VIEW";
    public static final String ACTION_RESET_THEME = "ACTION_RESET_THEME";
    public static final String ACTION_RESTART_FLOAT_VIEW = "ACTION_RESTART_FLOAT_VIEW";
    public static final String ACTION_ACCESSIBILITY_OFF = "ACTION_ACCESSIBILITY_OFF";
    private EdgeTouchView mEdgeTouchView;

    @Override
    public void onCreate() {
        LogUtils.d(TAG,"onCreate(2)");
        super.onCreate();
        getFloatView();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.getAction() != null) {
            String action = intent.getAction();
            switch (action) {
                case ACTION_START_FLOAT_VIEW:
                    startFloatView();
                    break;
                case ACTION_STOP_FLOAT_VIEW:
                    stopFloatView();
                    break;
                case ACTION_RESET_THEME:
                    resetTheme();
                    break;
                case ACTION_RESTART_FLOAT_VIEW:
                    restartFloatView();
                    break;
                case ACTION_ACCESSIBILITY_OFF:
                    NotificationUtils.sendAccessibilityOffNotification();
                    break;
            }
        } else if (intent == null) {
            // 服务异常被杀死的情况,intent 为 null
            LogUtils.d(TAG,"onstartcomment but intent == null");

        }
        return START_STICKY;
    }

    private void stopFloatView() {
        LogUtils.d(TAG,"service stopFloatView()");
        getFloatView().hideFloatView();
    }

    private void startFloatView() {
        LogUtils.d(TAG,"service startFloatView()");
        getFloatView().showFloatView();
    }

    private void resetTheme(){
        getFloatView().setFloatViewTheme();
    }
    private void restartFloatView(){
        getFloatView().reStartFloatView();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public EdgeTouchView getFloatView() {
        LogUtils.d(TAG,"getFloatView()");
        if(mEdgeTouchView == null){
            mEdgeTouchView = new EdgeTouchView(this);
        }
        return mEdgeTouchView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtils.d(TAG,"onDestory");
    }
}
