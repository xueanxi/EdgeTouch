package eagetouch.anxi.com.eagetouch;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Message;
import android.os.SystemClock;
import android.os.Vibrator;
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
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CompoundButton;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import eagetouch.anxi.com.eagetouch.accessibility.EdgeAccessibilitySever;

/**
 * Created by user on 11/29/17.
 */

public class FloatView implements View.OnTouchListener, CompoundButton.OnCheckedChangeListener{
    private static final String TAG = "=FloatView";

    private static final int TIME_TOUCH_VALID = 500;            // 从用户触发时间的有效时间

    private Context mContext;
    private WindowManager mWindowManager;
    private WindowManager.LayoutParams mParams;
    private View mContentView;        // 悬浮窗的布局


    private int mViewWidth;                     // 悬浮窗的宽度
    private int mViewHeight;                    // 悬浮窗的高度
    private int mScreenWidth;                   // 手机屏幕的宽度
    private int mScreenHeight;                  // 手机屏幕的高度

    float mStartTouchX;
    float mStartTouchTime;
    AlphaAnimation mAlphaAnimation;
    Vibrator mVibrator;


    public FloatView(Context context) {
        LogUtils.d(TAG, "FloatView()");
        mContext = context;
        init();
    }

    private void init() {
        LogUtils.d(TAG, "init()");
        getWindowManager();
        mScreenWidth = getScreenWidth(mContext);
        mScreenHeight = getScreenHeight(mContext);
        mViewWidth = (int) mContext.getResources().getDimension(R.dimen.float_view_width);
        mViewHeight = mScreenHeight;
        initFloatView();
        mVibrator = (Vibrator) mContext.getSystemService(Service.VIBRATOR_SERVICE);
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
        mContentView.setAlpha(0f);
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
        mContentView.setOnTouchListener(this);

        mAlphaAnimation = new AlphaAnimation(1.0f, 0.2f);
        //设置动画持续时长
        mAlphaAnimation.setDuration(1000);
        //设置动画结束之后的状态是否是动画的最终状态，true，表示是保持动画结束时的最终状态
        //mAlphaAnimation.setFillAfter(true);
        //设置动画结束之后的状态是否是动画开始时的状态，true，表示是保持动画开始时的状态
        //mAlphaAnimation.setFillBefore(true);
        //设置动画的重复模式：反转REVERSE和重新开始RESTART
        //mAlphaAnimation.setRepeatMode(AlphaAnimation.REVERSE);
        //设置动画播放次数
        mAlphaAnimation.setRepeatCount(AlphaAnimation.INFINITE);
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
                if(mVibrator!= null){
                    mVibrator.vibrate(50);
                }

                mStartTouchX = event.getRawX();
                mStartTouchTime = System.currentTimeMillis();

                break;
            case MotionEvent.ACTION_UP:
                LogUtils.d(TAG, "=== up ===");
                float newX = event.getRawX();
                if((newX - mStartTouchX > mScreenWidth/5)
                        && (System.currentTimeMillis() - mStartTouchTime <TIME_TOUCH_VALID)){
                    doback();
                }
                break;
        }
        return true;
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

            }
        }
    };

    private void doback2(){

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

    private void doback(){
        Intent intent = new Intent(mContext, EdgeAccessibilitySever.class);
        intent.setPackage(mContext.getPackageName());
        intent.setAction(EdgeAccessibilitySever.RUN_BACK);
        mContext.startService(intent);

        lightFloatView();
    }

    private void lightFloatView() {
        if(mContentView!= null ){
            mContentView.setAlpha(1f);
            AnimatorSet animatorSet = new AnimatorSet();
            ObjectAnimator alpha = ObjectAnimator.ofFloat(mContentView, "alpha", 1f, 0f);
            animatorSet.setDuration(200);
            animatorSet.setInterpolator(new AccelerateInterpolator());
            animatorSet.playTogether(alpha);
            animatorSet.start();
        }
    }

    public void onDestory(){
        if(mVibrator!=null){
            mVibrator.cancel();
            mVibrator = null;
        }
    }
}
