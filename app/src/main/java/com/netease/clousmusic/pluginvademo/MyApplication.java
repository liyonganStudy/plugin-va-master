package com.netease.clousmusic.pluginvademo;

import android.app.Application;
import android.content.Context;

import com.netease.clousmusic.pluginengin.PluginManager;

/**
 * Created by liyongan on 17/12/4.
 */

public class MyApplication extends Application {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        PluginManager.getInstance().attachContext(this);
    }
}
