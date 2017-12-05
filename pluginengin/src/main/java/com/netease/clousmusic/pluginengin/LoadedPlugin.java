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
        mPath = apk.getPath();
        mPackageInfo = pm.getPackageArchiveInfo(mPath, PackageManager.GET_ACTIVITIES | PackageManager.GET_META_DATA);
        mPackageInfo.applicationInfo.sourceDir = mPath;
        mPackageInfo.applicationInfo.publicSourceDir = mPath;
        // Cache activities
        mActivityInfoMap = new HashMap<>();
        for (ActivityInfo ai : mPackageInfo.activities) {
            ai.applicationInfo.sourceDir = mPath;
            mActivityInfoMap.put(ai.name, ai);
        }
//        mResources = HookHelper.createResources(hostContext, apk);
        try {
            if (BuildConfig.DEBUG) {
                Resources r = pm.getResourcesForApplication(mPackageInfo.applicationInfo);
                mResources = new Resources(r.getAssets(), r.getDisplayMetrics(), r.getConfiguration());
            } else {
                mResources = pm.getResourcesForApplication(mPackageInfo.applicationInfo);
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        ClassLoader parent;
        if (BuildConfig.DEBUG) {
            // 因为Instant Run会替换parent为IncrementalClassLoader，所以在DEBUG环境里
            // 需要替换为BootClassLoader才行
            // Added by yangchao-xy & Jiongxuan Zhang
            parent = ClassLoader.getSystemClassLoader();
        } else {
            // 线上环境保持不变
            parent = getClass().getClassLoader().getParent(); // TODO: 这里直接用父类加载器
        }
        mClassLoader = HookHelper.createClassLoader(hostContext, apk, parent);
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

    public int getThemeId() {
        return HookHelper.selectDefaultTheme(mPackageInfo.applicationInfo.theme, Build.VERSION.SDK_INT);
    }

    public ActivityInfo getActivityInfo(String name) {
        return mActivityInfoMap.get(name);
    }

    public String getPackageName() {
        return mPackageInfo.packageName;
    }
}
