package eagetouch.anxi.com.edgetouch;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import eagetouch.anxi.com.edgetouch.interfaces.DialogListener;
import eagetouch.anxi.com.edgetouch.utils.LogUtils;
import eagetouch.anxi.com.edgetouch.utils.PermissionUtils;

/**
 * Created by user on 4/11/18.
 */

public class PermissionActivity extends BaseActivity {
    private final String TAG = "=PermissionActivity";

    String[] permissions = new String[]{
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
    };

    boolean hasPermission = false;


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkPermission();
    }


    private void checkPermission() {
        // 检查普通的权限
        for(int i =0;i<permissions.length;i++){
            if(ContextCompat.checkSelfPermission(this, permissions[i]) == PackageManager.PERMISSION_DENIED){
                hasPermission = false;
            }
        }

        if(hasPermission){
            Intent intent = new Intent(this,MainActivity.class);
            intent.setPackage(getPackageName());
            startActivity(intent);
            finish();
        }else{
            LogUtils.d(TAG,"has not Permisssion and go to get permission.");
            requestPermissions(permissions,1001);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 1001){
            hasPermission = true;
            for(int i = 0;i<grantResults.length;i++){
                if(grantResults[i] != PackageManager.PERMISSION_GRANTED){
                    hasPermission = false;
                }
            }
            if(hasPermission){
                Intent intent = new Intent(this,MainActivity.class);
                intent.setPackage(getPackageName());
                startActivity(intent);
            }else{
                Toast.makeText(this,getString(R.string.toast_get_permission_fail), Toast.LENGTH_SHORT).show();
            }
            finish();
        }
    }
}
