package eagetouch.anxi.com.eagetouch;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import eagetouch.anxi.com.eagetouch.utils.PermissionUtils;
import eagetouch.anxi.com.eagetouch.utils.ToastUtils;

public class MainActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {


    Switch mSwEageTouch;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initSwitch();
    }

    private void initSwitch() {
        mSwEageTouch = (Switch) this.findViewById(R.id.sw_main_eagetouch);
        boolean canDrawOverlays =PermissionUtils.canDrawOverlays(this.getApplicationContext());
        // 如果没有开启显示悬浮窗的权限，则需要关闭switch
        if(!canDrawOverlays){
            mSwEageTouch.setChecked(false);
        }
        mSwEageTouch.setOnCheckedChangeListener(this);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        int id = buttonView.getId();
        Intent intent = new Intent();
        if(id == R.id.sw_main_eagetouch){
            if(isChecked){
                if(!PermissionUtils.canDrawOverlays(this.getApplicationContext())){
                    // 如果没有开启悬浮窗选先，需要先跳转到Settings里面打开
                    ToastUtils.toastShort(this,getString(R.string.toast_not_permission_overlay));
                    mSwEageTouch.setOnCheckedChangeListener(null);
                    mSwEageTouch.setChecked(false);
                    mSwEageTouch.setOnCheckedChangeListener(this);
                    intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                    startActivity(intent);
                }else{
                    intent.setPackage("eagetouch.anxi.com.eagetouch");
                    intent.setClass(this,EdgeTouchService.class);
                    intent.setAction(EdgeTouchService.ACTION_START_FLOAT_VIEW);
                    startService(intent);
                }
            }else{
                intent.setPackage("eagetouch.anxi.com.eagetouch");
                intent.setClass(this,EdgeTouchService.class);
                intent.setAction(EdgeTouchService.ACTION_STOP_FLOAT_VIEW);
                startService(intent);
            }
        }
    }

    private void startAccessibilityService() {

        // 隐式调用系统设置界面
        CustomDialog.Builder dialog = new CustomDialog.Builder(this);
        CustomDialog d =
                dialog.setTitle(R.string.accessibilityservice_dialog_title)
                        .setTitle(R.string.accessibilityservice_dialog_title)
                        .setMessage(R.string.accessibilityservice_dialog_content)
                        .setPositiveButton(R.string.accessibilityservice_dialog_button_ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                                startActivity(intent);
                                dialogInterface.dismiss();
                            }
                        }).setNegativeButton(R.string.accessibilityservice_dialog_button_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).create();
        d.setCanceledOnTouchOutside(false);
        d.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                finish();
            }
        });
        d.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 10) {
            if (Build.VERSION.SDK_INT >= 23) {
                if (!Settings.canDrawOverlays(this)) {
                    // SYSTEM_ALERT_WINDOW permission not granted...
                    Toast.makeText(this,"not granted",Toast.LENGTH_SHORT);
                }
            }
        }
    }
}
