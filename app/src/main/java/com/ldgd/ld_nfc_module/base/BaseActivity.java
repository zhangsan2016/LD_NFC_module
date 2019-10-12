package com.ldgd.ld_nfc_module.base;


import android.app.ProgressDialog;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

/**
 * Created by ldgd on 2019/9/23.
 * 功能：
 * 说明：自定义Activity 基类
 */

public abstract class BaseActivity extends AppCompatActivity {
    protected ProgressDialog mProgress;
    private static Toast toast;

    protected void showToast(final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if (toast == null) {
                    toast = Toast.makeText(BaseActivity.this, null, Toast.LENGTH_LONG);
                    toast.setText(msg);
                } else {
                    toast.setText(msg);
                }
                toast.show();
            }
        });
    }

    /**
     * Helper function to display a Toast from non UI thread
     *
     * @param resource_id
     * @param formatArgs
     */
    protected void showToast(final int resource_id, final Object... formatArgs) {
        runOnUiThread(new Runnable() {
            public void run() {
                // This function can be called from a background thread so it may happen after the
                // destruction of the activity. In such case, getResmessageources() may be null.
                Resources resources = getResources();
                if (resources != null) {
                    String message = resources.getString(resource_id, formatArgs);
                    Toast.makeText(BaseActivity.this, message, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    protected void stopProgress() {
        mProgress.cancel();
    }

    protected void showProgress() {
        mProgress = ProgressDialog.show(this, "", "Loading...");
    }


}
