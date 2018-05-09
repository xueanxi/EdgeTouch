package eagetouch.anxi.com.eagetouch;

import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;

/**
 * Created by zhi.li on 2016/8/17.
 */
public interface DialogListener{

	public void onPositiveClick();

	public void onNegativeClick();

	public void onDismiss();
}
