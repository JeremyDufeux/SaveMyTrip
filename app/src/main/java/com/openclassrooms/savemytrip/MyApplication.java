package com.openclassrooms.savemytrip;

import android.app.Application;

import com.facebook.stetho.Stetho;
import com.facebook.stetho.okhttp3.StethoInterceptor;

import okhttp3.OkHttpClient;

public class MyApplication extends Application {

    public void onCreate() {
        super.onCreate();

        if(BuildConfig.DEBUG) {
            Stetho.initializeWithDefaults(this);
        }
    }
}
