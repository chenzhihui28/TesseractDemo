package com.czh.tesseract;

import android.app.Application;
import android.content.Context;

/**
 * Created by chenzhihui on 2017/11/17.
 */

public class MyApplication extends Application {
    static Context mContext;
    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();

    }


}
