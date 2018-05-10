package eagetouch.anxi.com.edgetouch.server;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import eagetouch.anxi.com.edgetouch.EdgeTouchView;
import eagetouch.anxi.com.edgetouch.utils.LogUtils;


/**
 * Created by user on 11/29/17.
 */

public class EdgeTouchService extends Service {
    private static final String TAG = "Ax/EdgeTouchService";

    public static final String ACTION_START_FLOAT_VIEW = "ACTION_START_FLOAT_VIEW";
    public static final String ACTION_STOP_FLOAT_VIEW = "ACTION_STOP_FLOAT_VIEW";
    public static final String ACTION_RESET_THEME = "ACTION_RESET_THEME";
    public static final String ACTION_RESTART_FLOAT_VIEW = "ACTION_RESTART_FLOAT_VIEW";
    private EdgeTouchView mEdgeTouchView;

    @Override
    public void onCreate() {
        LogUtils.d(TAG,"onCreate()");
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
            }
        } else if (intent == null) {
            // 服务异常被杀死的情况,intent 为 null

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
}
