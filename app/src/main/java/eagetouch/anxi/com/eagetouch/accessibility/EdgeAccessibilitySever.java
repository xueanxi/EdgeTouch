package eagetouch.anxi.com.eagetouch.accessibility;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Intent;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;


/**
 * Created by user on 17-1-25.
 */

public class EdgeAccessibilitySever extends AccessibilityService {

    public static final String RUN_RECENT = "run_recent";
    public static final String RUN_BACK = "run_back";
    public static final String RUN_SPLIT_SCREEN = "run_split_screen";
    private static final String TAG = "EdgeAccessibilitySever";

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getAction();
        if (RUN_RECENT.equals(action)) {
            performGlobalAction(GLOBAL_ACTION_RECENTS);
        } else if (RUN_BACK.equals(action)) {
            performGlobalAction(GLOBAL_ACTION_BACK);
        } else if (RUN_SPLIT_SCREEN.equals(action)) {
            performGlobalAction(GLOBAL_ACTION_TOGGLE_SPLIT_SCREEN);
        }
        return START_STICKY;
    }

    @Override
    protected void onServiceConnected() {
        AccessibilityServiceInfo accessibilityServiceInfo = new AccessibilityServiceInfo();
        //指定包名
        accessibilityServiceInfo.packageNames = new String[]{getPackageName()};
        //指定事件类型
        accessibilityServiceInfo.eventTypes = AccessibilityEvent.TYPE_VIEW_CLICKED
                | AccessibilityEvent.TYPE_VIEW_FOCUSED;
        accessibilityServiceInfo.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;
        accessibilityServiceInfo.notificationTimeout = 100;
        setServiceInfo(accessibilityServiceInfo);
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {

    }

    @Override
    public void onInterrupt() {
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }
}
