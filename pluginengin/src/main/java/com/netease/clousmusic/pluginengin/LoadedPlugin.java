package com.netease.clousmusic.pluginengin;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Build;

import com.netease.clousmusic.pluginengin.hook.HookHelper;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by liyongan on 17/12/4.
 */

public class LoadedPlugin {
    public static final String OPTIMIZE_DIR = "dex";
    public static final String NATIVE_DIR = "valibs";

    private Context mHostContext;
    private PackageInfo mPackageInfo;
    private String mPath;
    private Map<String, ActivityInfo> mActivityInfoMap;
    private Resources mResources;
    private ClassLoader mClassLoader;
    private Context mPluginContext;


    public static LoadedPlugin create(Context host, File apk) throws Exception {
        return new LoadedPlugin(host, apk);
    }

    public LoadedPlugin(Context hostContext, File apk) {
        mHostContext = hostContext;
        PackageManager pm = hostContext.getPackageManager();
        mPath = apk.getAbsolutePath();
        mPackageInfo = pm.getPackageArchiveInfo(mPath, PackageManager.GET_ACTIVITIES | PackageManager.GET_META_DATA);
        mPackageInfo.applicationInfo.sourceDir = mPath;
        mPackageInfo.applicationInfo.publicSourceDir = mPath;
        // Cache activities
        mActivityInfoMap = new HashMap<>();
        for (ActivityInfo ai : mPackageInfo.activities) {
            ai.applicationInfo.sourceDir = mPath;
            mActivityInfoMap.put(ai.name, ai);
        }
        mResources = HookHelper.createResources(hostContext, apk);
        mClassLoader = HookHelper.createClassLoader(hostContext, apk, hostContext.getClassLoader());
        mPluginContext = new PluginContext(this, hostContext);
    }

    public Context getPluginContext() {
        return mPluginContext;
    }

    public Resources getResources() {
        return mResources;
    }

    public ClassLoader getClassLoader() {
        return mClassLoader;
    }

    public AssetManager getAssets() {
        return getResources().getAssets();
    }

    public Resources.Theme getTheme() {
        Resources.Theme theme = mResources.newTheme();
        theme.applyStyle(HookHelper.selectDefaultTheme(mPackageInfo.applicationInfo.theme, Build.VERSION.SDK_INT), false);
        return theme;
    }


    public String getPackageName() {
        return mPackageInfo.packageName;
    }
}
