package eagetouch.anxi.com.eagetouch;

import android.app.Instrumentation;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Message;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.InputDevice;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;

/**
 * Created by user on 11/29/17.
 */

public class FloatView implements View.OnTouchListener, CompoundButton.OnCheckedChangeListener{
    private static final String TAG = "Ax/FloatView";

    private static final int DEFAULT_TIME_LOAD = 19;            // 默认加载时间
    private static final int DEFAULT_TIME_PLAY = 35;            // 默认战斗时间



    private Context mContext;
    private WindowManager mWindowManager;
    private WindowManager.LayoutParams mParams;
    private View mContentView;        // 悬浮窗的布局
    private TextView mTvLoad;                   // 显示加载时间的文本
    private TextView mTvPlay;                   // 显示游戏时间的文本
    private TextView mTvLoadTimeCount;          // 记录加载了多长时间的文本
    private TextView mTvPlayTimeCount;          // 记录游戏了多长事件的文本
    private Switch mSwitch;                     // 功能的开关
    private ImageButton mBtClose;               // 关闭悬浮窗的按钮
    private Button mBtLoadReduce;               // 减少加载等待时间的按钮
    private Button mBtLoadPlus;                 // 增加加载等待时间的按钮
    private Button mBtPlayReduce;               // 减少游戏等待时间的按钮
    private Button mBtPlayPlus;                 // 增加游戏等待时间的按钮

    private int mViewWidth;                     // 悬浮窗的宽度
    private int mViewHeight;                    // 悬浮窗的高度
    private int mScreenWidth;                   // 手机屏幕的宽度
    private int mScreenHeight;                  // 手机屏幕的高度

    float mXInScreen;
    float mYInScreen;
    float mXDownInScreen;
    float mYDownInScreen;
    float mXInView;
    float mYInView;
    int mStatusBarHeight = 0;                   // 状态栏的高度
    boolean mIsWork = false;                    // 是否正在工作
    int mLoadTime = 0;                          // 加载等待时间
    int mPlayTime = 0;                          // 游戏等待时间
    ExecutorService mExecutor;
    boolean mIsCountLoadTime = false;           // 是否在统计加载时间
    boolean mIsCountPlayTime = false;           // 是否在统计游戏时间

    public FloatView(Context context) {
        LogUtils.d(TAG, "FloatView()");
        mContext = context;
        init();
    }

    private void init() {
        LogUtils.d(TAG, "init()");
        getWindowManager();
        getStatusBarHeight();
        mScreenWidth = getScreenWidth(mContext);
        mScreenHeight = getScreenHeight(mContext);
        mViewWidth = (int) mContext.getResources().getDimension(R.dimen.float_view_width);
        mViewHeight = mScreenHeight;
        initFloatView();
    }

    /**
     * 获得窗口服务
     *
     * @return
     */
    private WindowManager getWindowManager() {
        if (mWindowManager == null) {
            mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        }
        return mWindowManager;
    }

    /**
     * 初始化悬浮控件
     */
    private void initFloatView() {
        LogUtils.d(TAG, "initFloatView()");
        // 从配置文件获取当前的大小
        ViewGroup.LayoutParams lp;
        mContentView = new View(mContext);
        mContentView.setBackgroundColor(Color.RED);
        mParams = new WindowManager.LayoutParams();
        mParams.setTitle("EdgeBar");
        //设置window type
        //mParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        //mParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        mParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        //设置图片格式，效果为背景透明
        mParams.format = PixelFormat.RGBA_8888;
        //设置浮动窗口不可聚焦（实现操作除浮动窗口外的其他可见窗口的操作）
        mParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        //调整悬浮窗显示的停靠位置为左侧置顶
        mParams.gravity = Gravity.LEFT | Gravity.TOP;
        mParams.width = mViewWidth;
        mParams.height = mViewHeight;
        mParams.x = 0;
        mParams.y = 0;
        //mContentView.setOnTouchListener(this);
        mContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doback2();
            }
        });
    }


    /***
     * 显示悬浮窗
     */
    public void showFloatView() {
        LogUtils.d(TAG, "showFloatView()");
        if (mContentView.isAttachedToWindow()) {
            LogUtils.d(TAG, "mContentView isAttached To Window ,so remove first.");
            getWindowManager().removeView(mContentView);
        }
        try {
            getWindowManager().addView(mContentView, mParams);
            LogUtils.d(TAG, "mContentView is added to window.");
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 隐藏悬浮窗控件
     */
    public void hideFloatView() {
        LogUtils.d(TAG, "hideFloatView()");
        try {
            if (mContentView != null) {
                getWindowManager().removeView(mContentView);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                LogUtils.d(TAG, "=== down ===");
                mXInScreen = event.getRawX();
                //mYInScreen = event.getRawY() - getStatusBarHeight();
                mYInScreen = event.getRawY();
                mXDownInScreen = event.getRawX();
                //mYDownInScreen = event.getRawY() - getStatusBarHeight();
                mYDownInScreen = event.getRawY();
                mXInView = event.getX();
                mYInView = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                LogUtils.d(TAG, "=== move ===");
                mXInScreen = event.getRawX();
                //mYInScreen = event.getRawY() - getStatusBarHeight();
                mYInScreen = event.getRawY();
                updateViewPosition();
                break;
            case MotionEvent.ACTION_UP:
                LogUtils.d(TAG, "=== up ===");
                mXInScreen = event.getRawX();
                mYInScreen = event.getRawY();
                updateViewPosition();
                break;
        }
        return true;
    }


    /**
     * 更新悬浮球的位置
     */
    private void updateViewPosition() {
        if (mParams == null) return;

        mParams.x = (int) (mXInScreen - mXInView);
        mParams.y = (int) (mYInScreen - mYInView) - mStatusBarHeight;

        if (mParams.x < 0) {
            mParams.x = 0;
        }
        if (mParams.y < 0) {
            mParams.y = 0;
        }
        if ((mParams.x + mViewWidth) > mScreenWidth) {
            mParams.x = mScreenWidth - mViewWidth;
        }
        if (mParams.y + mViewHeight > mScreenHeight) {
            mParams.y = mScreenHeight - mViewHeight;
        }
        getWindowManager().updateViewLayout(mContentView, mParams);
    }

    /**
     * 获得状态栏的高度，用于调整悬浮窗的位置
     *
     * @return
     */
    private int getStatusBarHeight() {
        if (mStatusBarHeight == 0) {
            try {
                Class<?> c = Class.forName("com.android.internal.R$dimen");
                Object o = c.newInstance();
                Field field = c.getField("status_bar_height");
                int x = (Integer) field.get(o);
                mStatusBarHeight = mContext.getResources().getDimensionPixelSize(x);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return mStatusBarHeight;
    }

    /**
     * 获得屏幕的高度
     *
     * @param context
     * @return
     */
    public int getScreenHeight(Context context) {
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.heightPixels;
    }

    /**
     * 获得屏幕的宽度
     * @param context
     * @return
     */
    public int getScreenWidth(Context context) {
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.widthPixels;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        LogUtils.d(TAG, "onCheckedChanged() isChecked:" + isChecked);

    }


    /**
     * 处理器
     */
    android.os.Handler mHandler = new android.os.Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1000:
                    mTvLoadTimeCount.setText("Load Time: " + msg.arg1);
                    break;
                case 1001:
                    mTvPlayTimeCount.setText("Play Time: " + msg.arg1);
                    break;
            }
        }
    };

    private void doback(){

//                    KeyEvent keyEventDown = new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BACK);
//                    KeyEvent keyEventUp = new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_BACK);
        final int repeatCount = (KeyEvent.FLAG_VIRTUAL_HARD_KEY & KeyEvent.FLAG_LONG_PRESS) != 0 ? 1 : 0;
        final KeyEvent evDown = new KeyEvent(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(),
                KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BACK, repeatCount, 0, KeyCharacterMap.VIRTUAL_KEYBOARD,
                0, KeyEvent.FLAG_VIRTUAL_HARD_KEY | KeyEvent.FLAG_FROM_SYSTEM | KeyEvent.FLAG_VIRTUAL_HARD_KEY,
                InputDevice.SOURCE_KEYBOARD);

        final KeyEvent evUp = new KeyEvent(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(),
                KeyEvent.ACTION_UP, KeyEvent.KEYCODE_BACK, repeatCount, 0, KeyCharacterMap.VIRTUAL_KEYBOARD, 0,
                KeyEvent.FLAG_VIRTUAL_HARD_KEY | KeyEvent.FLAG_FROM_SYSTEM | KeyEvent.FLAG_VIRTUAL_HARD_KEY,
                InputDevice.SOURCE_KEYBOARD);

        Class<?> ClassInputManager;
        try {
            ClassInputManager = Class.forName("android.hardware.input.InputManager");
            Method[] methods = ClassInputManager.getMethods();
            //System.out.println("cchen " + Arrays.toString(methods));
            Method methodInjectInputEvent = null;
            Method methodGetInstance = null;
            for (Method method : methods) {
                //System.out.println("cchen " + method.getName());
                if (method.getName().contains("getInstance")) {
                    methodGetInstance = method;
                }
                if (method.getName().contains("injectInputEvent")) {
                    methodInjectInputEvent = method;
                }
            }
            Object instance = methodGetInstance.invoke(ClassInputManager, null);
            //boolean bool = InputManager.class.isInstance(instance);
            // methodInjectInputEvent =
            // InputManager.getMethod("injectInputEvent",
            // KeyEvent.class, Integer.class);
            methodInjectInputEvent.invoke(instance, evDown, 0);
            methodInjectInputEvent.invoke(instance, evUp, 0);
        } catch (Exception e) {
            Log.d(TAG,e.toString());
        }
    }

    private void doback2(){
        final Instrumentation in = new Instrumentation();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(100);
                } catch (Exception e) {
                    Log.d(TAG,e.toString());
                }
                in.sendKeyDownUpSync(KeyEvent.KEYCODE_BACK);
            }
        }).start();
    }

    private void doback3(){

    }
}
