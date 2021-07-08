package com.ldgd.ld_nfc_ndef_module.appliction;

import android.app.Application;
import android.os.StrictMode;

/**
 * Created by ldgd on 2021/7/8.
 */

public class MyAppliction extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // android 7.0系统解决拍照的问题
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();

    }
}
