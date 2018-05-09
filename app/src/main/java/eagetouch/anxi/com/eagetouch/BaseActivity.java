package eagetouch.anxi.com.eagetouch;


import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Created by user on 5/9/18.
 */

public abstract class BaseActivity extends AppCompatActivity{

    AlertDialog dialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    public void showDialog(String title, String content,String positivetext,String negativeText,DialogListener listener) {
        getFragmentManager().beginTransaction()
                .add(MyDialogFragment.newInstance(title,content,positivetext,negativeText,listener), "MyDialogFragment")
                .commitAllowingStateLoss();
    }
}
