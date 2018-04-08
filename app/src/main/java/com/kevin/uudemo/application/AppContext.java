package com.kevin.uudemo.application;

import android.app.Application;

import com.baidu.mapapi.SDKInitializer;

/**
 * 创建时间：2018/3/15 16:04
 * 作者：KevinLiu     e-mail:KevinLiu9527@163.com
 * 备注：
 */
public class AppContext extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        SDKInitializer.initialize(this);
    }
}
