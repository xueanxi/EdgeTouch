package eagetouch.anxi.com.edgetouch;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.widget.CompoundButton;

import eagetouch.anxi.com.edgetouch.server.EdgeAccessibilitySever;
import eagetouch.anxi.com.edgetouch.utils.LogUtils;
import eagetouch.anxi.com.edgetouch.utils.PreferenceUtils;

/**
 * Created by user on 11/29/17.
 */

public class EdgeTouchView implements View.OnTouchListener, CompoundButton.OnCheckedChangeListener {
    private static final String TAG = "=EdgeTouchView";

    private static final int TIME_TOUCH_VALID = 500;            // 从按下到离开屏幕的时间限制，如果大于限制，则不会触发
    private static final int RATE_TOUCH_VALID = 6;              // 滑动超过屏幕1/6才生效
    private static final int RATE_TOUCH_VALID_LANSCAPE = 12;    // 滑动超过屏幕1/12才生效（横屏时）

    private Context mContext;
    private WindowManager mWindowManager;
    private WindowManager.LayoutParams mParams;
    private View mFloatView;        // 悬浮窗


    private int mViewWidth;                     // 悬浮窗的宽度
    private int mViewHeight;                    // 悬浮窗的高度
    private int mScreenWidth;                   // 手机屏幕的宽度
    private int mScreenHeight;                  // 手机屏幕的高度

    float mStartTouchX;
    long mStartTouchTime;
    Vibrator mVibrator;
    int mThemeIndex = 0;    // 主题编号


    public EdgeTouchView(Context context) {
        LogUtils.d(TAG, "EdgeTouchView()");
        mContext = context;
        init();
    }

    private void init() {
        LogUtils.d(TAG, "init()");
        getWindowManager();
        mScreenWidth = getScreenWidth(mContext);
        mScreenHeight = getScreenHeight(mContext);
        mViewWidth = (int) mContext.getResources().getDimension(R.dimen.float_view_width);
        mViewHeight = mScreenHeight*2/3;
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
        // 从配置文件获取当前的大小
        mFloatView = new View(mContext);
        setFloatViewTheme();

        mParams = new WindowManager.LayoutParams();
        mParams.setTitle("EdgeTouchView");
        //设置window type
        //mParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        //mParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        mParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        //设置图片格式，效果为背景透明
        mParams.format = PixelFormat.RGBA_8888;
        //设置浮动窗口不可聚焦（实现操作除浮动窗口外的其他可见窗口的操作）
        mParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        //调整悬浮窗显示的停靠位置为左侧置顶
        if (PreferenceUtils.isTouchAreaOnLeft()) {
            mParams.gravity = Gravity.LEFT | Gravity.BOTTOM;
        } else {
            mParams.gravity = Gravity.RIGHT | Gravity.BOTTOM;
        }
        mParams.width = mViewWidth;
        mParams.height = mViewHeight;
        mParams.x = 0;
        mParams.y = 0;
        mFloatView.setOnTouchListener(this);
    }


    /**
     * 设置悬浮窗的主题颜色
     */
    public void setFloatViewTheme() {
        mThemeIndex = PreferenceUtils.getThemeColor(0);
        boolean isLeft = PreferenceUtils.isTouchAreaOnLeft();
        switch (mThemeIndex) {
            case 0:
                if (isLeft) {
                    mFloatView.setBackgroundResource(R.drawable.sp_gradient_blue_left);
                } else {
                    mFloatView.setBackgroundResource(R.drawable.sp_gradient_blue_right);
                }
                break;
            case 1:
                if (isLeft) {
                    mFloatView.setBackgroundResource(R.drawable.sp_gradient_orange_left);
                } else {
                    mFloatView.setBackgroundResource(R.drawable.sp_gradient_orange_right);
                }
                break;
            case 2:
                if (isLeft) {
                    mFloatView.setBackgroundResource(R.drawable.sp_gradient_red_left);
                } else {
                    mFloatView.setBackgroundResource(R.drawable.sp_gradient_red_right);
                }
                break;
            case 3:
                mFloatView.setBackgroundResource(R.drawable.sp_gradient_alpha);
                break;
            case 4:
                if (isLeft) {
                    mFloatView.setBackgroundResource(R.drawable.sp_gradient_green_left);
                } else {
                    mFloatView.setBackgroundResource(R.drawable.sp_gradient_green_right);
                }
                break;

            case 5:
                if (isLeft) {
                    mFloatView.setBackgroundResource(R.drawable.sp_gradient_yellow_left);
                } else {
                    mFloatView.setBackgroundResource(R.drawable.sp_gradient_yellow_right);
                }
                break;
            case 6:
                if (isLeft) {
                    mFloatView.setBackgroundResource(R.drawable.sp_gradient_pink_left);
                } else {
                    mFloatView.setBackgroundResource(R.drawable.sp_gradient_pink_right);
                }
                break;
            case 7:
                if (isLeft) {
                    mFloatView.setBackgroundResource(R.drawable.sp_gradient_purple_left);
                } else {
                    mFloatView.setBackgroundResource(R.drawable.sp_gradient_purple_right);
                }
                break;
            case 8:
                if (isLeft) {
                    mFloatView.setBackgroundResource(R.drawable.sp_gradient_white_left);
                } else {
                    mFloatView.setBackgroundResource(R.drawable.sp_gradient_white_right);
                }
                break;
            case 9:
                if (isLeft) {
                    mFloatView.setBackgroundResource(R.drawable.sp_gradient_brown_left);
                } else {
                    mFloatView.setBackgroundResource(R.drawable.sp_gradient_brown_right);
                }
                break;
            case 10:
                if (isLeft) {
                    mFloatView.setBackgroundResource(R.drawable.sp_gradient_blue_dark_left);
                } else {
                    mFloatView.setBackgroundResource(R.drawable.sp_gradient_blue_dark_right);
                }
                break;
            case 11:
                if (isLeft) {
                    mFloatView.setBackgroundResource(R.drawable.sp_gradient_black_left);
                } else {
                    mFloatView.setBackgroundResource(R.drawable.sp_gradient_black_right);
                }
                break;
            default:
                if (isLeft) {
                    mFloatView.setBackgroundResource(R.drawable.sp_gradient_blue_left);
                } else {
                    mFloatView.setBackgroundResource(R.drawable.sp_gradient_blue_right);
                }
                break;
        }
        mFloatView.setAlpha(0f);
    }


    /***
     * 显示悬浮窗
     */
    public void showFloatView() {
        LogUtils.d(TAG, "showFloatView()");
        if (mFloatView.isAttachedToWindow()) {
            LogUtils.d(TAG, "mFloatView isAttached To Window ,so remove first.");
            getWindowManager().removeView(mFloatView);
        }
        try {
            getWindowManager().addView(mFloatView, mParams);
            LogUtils.d(TAG, "mFloatView is added to window.");
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
            if (mFloatView != null) {
                getWindowManager().removeView(mFloatView);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void reStartFloatView() {
        hideFloatView();
        initFloatView();
        showFloatView();
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mStartTouchX = event.getRawX();
                mStartTouchTime = System.currentTimeMillis();

                break;
            case MotionEvent.ACTION_UP:
                float newX = event.getRawX();
                long currentTime = System.currentTimeMillis();
                long intervalTime = currentTime - mStartTouchTime;
                LogUtils.d(TAG, " currentTime:" + currentTime);
                LogUtils.d(TAG, " mStartTouchX:" + mStartTouchTime);
                LogUtils.d(TAG, " intervalTime:" + intervalTime);
                if ((Math.abs(newX - mStartTouchX) > mScreenWidth / RATE_TOUCH_VALID) && intervalTime < TIME_TOUCH_VALID) {
                    doBackPress();
                    lightFloatView();
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
     *
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

    private void doBackPress() {
        vibrator();

        Intent intent = new Intent(mContext, EdgeAccessibilitySever.class);
        intent.setPackage(mContext.getPackageName());
        intent.setAction(EdgeAccessibilitySever.RUN_BACK);
        mContext.startService(intent);
    }

    private void vibrator() {
        if (PreferenceUtils.isVibrateOn()) {
            if (mVibrator != null) {
                mVibrator.vibrate(50);
            }
        }
    }

    private void lightFloatView() {
        if (mFloatView != null) {
            mFloatView.setAlpha(1f);
            AnimatorSet animatorSet = new AnimatorSet();
            ObjectAnimator alpha = ObjectAnimator.ofFloat(mFloatView, "alpha", 1f, 0f);
            animatorSet.setDuration(200);
            animatorSet.setInterpolator(new AccelerateInterpolator());
            animatorSet.playTogether(alpha);
            animatorSet.start();
        }
    }

    public void onDestory() {
        if (mVibrator != null) {
            mVibrator.cancel();
            mVibrator = null;
        }
    }
}
