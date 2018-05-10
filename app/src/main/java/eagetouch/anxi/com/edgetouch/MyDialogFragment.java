package eagetouch.anxi.com.edgetouch;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;

import eagetouch.anxi.com.edgetouch.interfaces.DialogListener;

/**
 */
public class MyDialogFragment extends DialogFragment {
    private String mTitle;
    private String mMessage;
    private String mpositiveText;
    private String mNegativeText;
    private DialogListener mListener;

    public MyDialogFragment() {
    }

    public static MyDialogFragment newInstance(String title,String message,String positiveText) {
        MyDialogFragment dialog = new MyDialogFragment();
        dialog.mMessage = message;
        dialog.mTitle = title;
        dialog.mpositiveText = positiveText;
        dialog.mNegativeText = "";
        dialog.mListener = null;
        return dialog;
    }

    public static MyDialogFragment newInstance(String title,String message,String positiveText,String negativeText, DialogListener listener) {
        MyDialogFragment dialog = new MyDialogFragment();
        dialog.mMessage = message;
        dialog.mTitle = title;
        dialog.mpositiveText = positiveText;
        dialog.mNegativeText = negativeText;
        dialog.mListener = listener;
        return dialog;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Builder builder = new Builder(getActivity());
        builder.setTitle(mTitle);
        builder.setMessage(mMessage);

        builder.setPositiveButton(mpositiveText, new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (mListener != null) {
                    mListener.onPositiveClick();
                }
            }
        });
        builder.setNegativeButton(mNegativeText, new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (mListener != null) {
                    mListener.onNegativeClick();
                }
            }
        });
        final AlertDialog alertDialog = builder.create();
        return alertDialog;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (mListener != null) {
            mListener.onDismiss();
        }
    }
}
