package eagetouch.anxi.com.edgetouch.server;

import android.accessibilityservice.AccessibilityService;
import android.content.Intent;
import android.view.accessibility.AccessibilityEvent;

import eagetouch.anxi.com.edgetouch.utils.LogUtils;

import static eagetouch.anxi.com.edgetouch.server.EdgeTouchService.ACTION_ACCESSIBILITY_OFF;


/**
 * Created by user on 17-1-25.
 */

public class EdgeAccessibilitySever extends AccessibilityService {
    private static final String TAG = "=EdgeAccessibilitySever";

    public static final String RUN_RECENT = "run_recent";
    public static final String RUN_BACK = "run_back";
    public static final String RUN_SPLIT_SCREEN = "run_split_screen";
    private boolean isOff = false;

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
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {

    }

    @Override
    public void onInterrupt() {
        LogUtils.e(TAG, "onInterrupt");
        isOff = true;
        sendNotification();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtils.e(TAG, "onDestroy");
        if (!isOff) {
            sendNotification();
        }
        isOff = false;
    }

    private void sendNotification() {
        Intent intent = new Intent(this, EdgeTouchService.class);
        intent.setAction(ACTION_ACCESSIBILITY_OFF);
        startService(intent);
    }
}
