package eagetouch.anxi.com.edgetouch;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import eagetouch.anxi.com.edgetouch.interfaces.DialogListener;

/**
 * Created by user on 5/9/18.
 */

public abstract class BaseActivity extends AppCompatActivity{

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void showDialog(String title, String content,String positivetext,String negativeText,DialogListener listener) {
        getFragmentManager().beginTransaction()
                .add(MyDialogFragment.newInstance(title,content,positivetext,negativeText,listener), "MyDialogFragment")
                .commitAllowingStateLoss();
    }

    public void showDialog(String title, String content,String positivetext) {
        getFragmentManager().beginTransaction()
                .add(MyDialogFragment.newInstance(title,content,positivetext), "MyDialogFragment")
                .commitAllowingStateLoss();
    }
}
