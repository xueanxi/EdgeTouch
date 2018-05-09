package eagetouch.anxi.com.eagetouch.utils;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import eagetouch.anxi.com.eagetouch.App;

/**
 * Created by user on 17-1-9.
 */

public class PreferenceUtils {
    static App mApp = App.getInstance();
    public static final String KEY_FUNCTION_ON = "KEY_FUNCTION_ON";
    public static final String KEY_VIBRATE_ON = "KEY_VIBRATE_ON";
    public static final String KEY_TOUCH_AREA_ON_LEFT = "KEY_TOUCH_AREA_ON_LEFT";
    public static final String KEY_THEME_COLOR = "KEY_THEME_COLOR";

    public static void setBoolean(String key, boolean val) {
        PreferenceManager.getDefaultSharedPreferences(mApp).edit().putBoolean(key, val).apply();
    }

    public static boolean getBoolean(String key, boolean defVal) {
        return PreferenceManager.getDefaultSharedPreferences(mApp).getBoolean(key, defVal);
    }

    public static void setInt(String key, int val) {
        PreferenceManager.getDefaultSharedPreferences(mApp).edit().putInt(key, val).apply();
    }

    public static int getInt(String key, int defVal) {
        return PreferenceManager.getDefaultSharedPreferences(mApp).getInt(key, defVal);
    }

    public static void setString(String key, String val) {
        PreferenceManager.getDefaultSharedPreferences(mApp).edit().putString(key, val).apply();
    }

    public static String getString(String key, String defVal) {
        return PreferenceManager.getDefaultSharedPreferences(mApp).getString(key, defVal);
    }

    public static void registObserver(SharedPreferences.OnSharedPreferenceChangeListener listener) {
        PreferenceManager.getDefaultSharedPreferences(mApp).registerOnSharedPreferenceChangeListener(listener);
    }

    public static void unRegistObserver(SharedPreferences.OnSharedPreferenceChangeListener listener) {
        PreferenceManager.getDefaultSharedPreferences(mApp).unregisterOnSharedPreferenceChangeListener(listener);
    }

    public static void setFloat(String key, float val) {
        PreferenceManager.getDefaultSharedPreferences(mApp).edit()
                .putFloat(key, val).apply();
    }

    public static Float getFloat(String key, float defVal) {
        return PreferenceManager.getDefaultSharedPreferences(mApp).getFloat(key, defVal);
    }

    /**
     * 设置总开关是否开启
     */
    public static void setFuntionOn(boolean values) {
        setBoolean(KEY_FUNCTION_ON, values);
    }

    /**
     * 总开关是否开启
     */
    public static boolean isFuntionOn(boolean dafaultValue) {
        return getBoolean(KEY_FUNCTION_ON, dafaultValue);
    }
    /**
     * 设置震动是否开启
     */
    public static void setVibrateOn(boolean values) {
        setBoolean(KEY_VIBRATE_ON, values);
    }

    /**
     * 震动是否开启
     */
    public static boolean isVibrateOn(boolean dafaultValue) {
        return getBoolean(KEY_VIBRATE_ON, dafaultValue);
    }

    /**
     * 震动是否开启
     */
    public static boolean isVibrateOn() {
        return getBoolean(KEY_VIBRATE_ON, true);
    }
    /**
     * 设置触摸区域是否靠左
     */
    public static void setTouchAreaOnLeft(boolean values) {
        setBoolean(KEY_TOUCH_AREA_ON_LEFT, values);
    }

    /**
     * 触摸区域是否默认靠左
     */
    public static boolean isTouchAreaOnLeft(boolean dafaultValue) {
        return getBoolean(KEY_TOUCH_AREA_ON_LEFT, dafaultValue);
    }

    /**
     * 触摸区域是否默认靠左
     */
    public static boolean isTouchAreaOnLeft() {
        return getBoolean(KEY_TOUCH_AREA_ON_LEFT, true);
    }
    /**
     * 设置主题序号
     */
    public static void setThemeColor(int values) {
        setInt(KEY_THEME_COLOR, values);
    }

    /**
     * 获取主题序号
     */
    public static int getThemeColor(int dafaultValue) {
        return getInt(KEY_THEME_COLOR, dafaultValue);
    }
}
