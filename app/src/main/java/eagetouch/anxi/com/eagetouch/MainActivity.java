package eagetouch.anxi.com.eagetouch;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import eagetouch.anxi.com.eagetouch.accessibility.AccessibilyUtils;
import eagetouch.anxi.com.eagetouch.server.EdgeTouchService;
import eagetouch.anxi.com.eagetouch.utils.PermissionUtils;
import eagetouch.anxi.com.eagetouch.utils.PreferenceUtils;
import eagetouch.anxi.com.eagetouch.utils.ToastUtils;


public class MainActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener,View.OnClickListener {


    Switch mSwFunction;
    Switch mSwPermissionOverlay;
    Switch mSwPermissionAccessibility;
    Switch mSwWibrate;
    Switch mSwTouchArea;
    LinearLayout mLyPreferenceContains;
    RelativeLayout mLyTheme;
    ImageView mImgTheme;
    TextView mTvFunction;
    TextView mTvTouchArea;

    String[] mThemeColorList;
    int mSelectColorIndex = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initData();
        initView();
    }

    private void initData() {
        mThemeColorList = this.getResources().getStringArray(R.array.theme_colors);

    }

    private void initView() {
        mSwFunction = (Switch) this.findViewById(R.id.sw_main_function);
        mSwPermissionOverlay = (Switch) this.findViewById(R.id.sw_main_permission_overly);
        mSwPermissionAccessibility = (Switch) this.findViewById(R.id.sw_main_permission_accessbility);
        mSwWibrate = (Switch) this.findViewById(R.id.sw_main_vibrate);
        mSwTouchArea = (Switch) this.findViewById(R.id.sw_main_touch_area);

        mSwFunction.setOnCheckedChangeListener(this);
        mSwPermissionOverlay.setOnCheckedChangeListener(this);
        mSwPermissionAccessibility.setOnCheckedChangeListener(this);
        mSwWibrate.setOnCheckedChangeListener(this);
        mSwTouchArea.setOnCheckedChangeListener(this);

        mLyPreferenceContains = (LinearLayout) this.findViewById(R.id.ly_main_contain_preferences);
        mLyTheme = (RelativeLayout) this.findViewById(R.id.ly_main_theme);
        mImgTheme = (ImageView) this.findViewById(R.id.img_main_theme);
        mLyTheme.setOnClickListener(this);
        mImgTheme.setOnClickListener(this);

        mTvFunction = (TextView) this.findViewById(R.id.tv_main_switch);
        mTvTouchArea = (TextView) this.findViewById(R.id.tv_main_touch_area);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 检测是否有overlay权限
        if(PermissionUtils.canDrawOverlays(this.getApplicationContext())){
            mSwPermissionOverlay.setChecked(true);
        }else{
            mSwPermissionOverlay.setChecked(false);
        }

        // 检测是否有accessibility权限
        if(AccessibilyUtils.isAccessibilitySettingsOn(this.getApplicationContext())){
            mSwPermissionAccessibility.setChecked(true);
        }else{
            mSwPermissionAccessibility.setChecked(false);
        }

        if(PermissionUtils.canDrawOverlays(this.getApplicationContext())
                && AccessibilyUtils.isAccessibilitySettingsOn(this.getApplicationContext())
                && PreferenceUtils.isFuntionOn(false)){
            onFunctionOn();
        }else{
            onFunctionOff();
        }

        mSelectColorIndex = PreferenceUtils.getThemeColor(0);
        mImgTheme.setBackgroundColor(Color.parseColor(mThemeColorList[mSelectColorIndex]));
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        int id = buttonView.getId();
        Intent intent = new Intent();
        if(id == R.id.sw_main_function){
            if(isChecked){
                if(!PermissionUtils.canDrawOverlays(this.getApplicationContext())
                        || !AccessibilyUtils.isAccessibilitySettingsOn(this.getApplicationContext())){
                    // 如果没有开启悬浮窗选先，需要先跳转到Settings里面打开
                    ToastUtils.toastLong(this,getString(R.string.toast_not_permission));
                    onFunctionOff();
                }else{
                    onFunctionOn();
                }
            }else{
                onFunctionOff();
            }
        }
        // Overlay 权限
        else if(id == R.id.sw_main_permission_overly){
            if(isChecked){
                if(!PermissionUtils.canDrawOverlays(this.getApplicationContext())){
                    intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                    startActivity(intent);
                }
            }else{
                if(PermissionUtils.canDrawOverlays(this.getApplicationContext())){
                    intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                    startActivity(intent);
                }
            }
        }
        // Accessibilitity开关
        else if(id == R.id.sw_main_permission_accessbility){
            if(isChecked){
                if(!AccessibilyUtils.isAccessibilitySettingsOn(this.getApplicationContext())){
                    startAccessibilityService();
                }
            }else{
                if(AccessibilyUtils.isAccessibilitySettingsOn(this.getApplicationContext())){
                    startAccessibilityService();
                }
            }
        }
        // 震动开关
        else if(id == R.id.sw_main_vibrate){
            if(isChecked){
                PreferenceUtils.setVibrateOn(true);
            }else{
                PreferenceUtils.setVibrateOn(false);
            }
        }
        // 设置触摸区域的位置
        else if(id == R.id.sw_main_touch_area){
            if(isChecked){
                PreferenceUtils.setTouchAreaOnLeft(true);
                mTvTouchArea.setText(R.string.touch_area_left);
            }else{
                PreferenceUtils.setTouchAreaOnLeft(false);
                mTvTouchArea.setText(R.string.touch_area_right);
            }
            intent = new Intent();
            intent.setPackage("eagetouch.anxi.com.eagetouch");
            intent.setClass(this,EdgeTouchService.class);
            intent.setAction(EdgeTouchService.ACTION_RESTART_FLOAT_VIEW);
            startService(intent);
        }
    }

    private void startAccessibilityService() {
        // 隐式调用系统设置界面
        Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
        startActivity(intent);
    }

    private void onFunctionOn(){
        mTvFunction.setText(R.string.function_on);
        mSwFunction.setChecked(true);

        if(PreferenceUtils.isVibrateOn()){
            mSwWibrate.setChecked(true);
        }else{
            mSwWibrate.setChecked(false);
        }

        if(PreferenceUtils.isTouchAreaOnLeft()){
            mSwTouchArea.setChecked(true);
            mTvTouchArea.setText(R.string.touch_area_left);
        }else{
            mSwTouchArea.setChecked(false);
            mTvTouchArea.setText(R.string.touch_area_right);
        }

        Intent intent = new Intent();
        intent.setPackage("eagetouch.anxi.com.eagetouch");
        intent.setClass(this,EdgeTouchService.class);
        intent.setAction(EdgeTouchService.ACTION_START_FLOAT_VIEW);
        startService(intent);
        ToastUtils.toastShort(this,getString(R.string.toast_funtion_on));
        PreferenceUtils.setFuntionOn(true);

        mLyPreferenceContains.setVisibility(View.VISIBLE);
    }

    private void onFunctionOff(){
        mTvFunction.setText(R.string.function_off);
        mSwFunction.setChecked(false);
        PreferenceUtils.setFuntionOn(false);
        mLyPreferenceContains.setVisibility(View.GONE);
        //ToastUtils.toastShort(this,getString(R.string.toast_funtion_off));

        Intent intent = new Intent();
        intent.setPackage("eagetouch.anxi.com.eagetouch");
        intent.setClass(this,EdgeTouchService.class);
        intent.setAction(EdgeTouchService.ACTION_STOP_FLOAT_VIEW);
        startService(intent);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        Intent intent = null;
        if(id == R.id.ly_main_theme || id == R.id.img_main_theme){
            intent = new Intent(this,ThemeActivity.class);
            startActivity(intent);
        }
    }
}
