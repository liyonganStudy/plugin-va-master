package com.netease.clousmusic.pluginengin.hook;

import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.support.annotation.UiThread;
import android.support.annotation.WorkerThread;
import android.util.Log;

import com.netease.clousmusic.pluginengin.utils.Constants;
import com.netease.clousmusic.pluginengin.utils.ReflectUtil;

import java.io.File;

import dalvik.system.DexClassLoader;

import static com.netease.clousmusic.pluginengin.LoadedPlugin.NATIVE_DIR;
import static com.netease.clousmusic.pluginengin.LoadedPlugin.OPTIMIZE_DIR;

/**
 * Created by liyongan on 17/12/4.
 */

public class HookHelper {
    private Object sActivityThread;
    private Object sLoadedApk;
    private Instrumentation sInstrumentation;

    public Instrumentation getInstrumentation(Context context) {
        if (getActivityThread(context) != null) {
            try {
                sInstrumentation = (Instrumentation) ReflectUtil.invoke(sActivityThread.getClass(), sActivityThread, "getInstrumentation");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return sInstrumentation;
    }

    public static boolean isIntentFromPlugin(Intent intent) {
        return intent.getBooleanExtra(Constants.KEY_IS_PLUGIN, false);
    }

    public static String getTargetActivity(Intent intent) {
        return intent.getStringExtra(Constants.KEY_TARGET_ACTIVITY);
    }

    public static String getTargetPackageName(Intent intent) {
        return intent.getStringExtra(Constants.KEY_TARGET_PACKAGE);
    }

    @UiThread
    public Object getActivityThread(Context base) {
        if (sActivityThread == null) {
            try {
                Class<?> activityThreadClazz = Class.forName("android.app.ActivityThread");
                Object activityThread = null;
                try {
                    activityThread = ReflectUtil.getField(activityThreadClazz, null, "sCurrentActivityThread");
                } catch (Exception e) {
                    // ignored
                }
                if (activityThread == null) {
                    activityThread = ((ThreadLocal<?>) ReflectUtil.getField(activityThreadClazz, null, "sThreadLocal")).get();
                }
                sActivityThread = activityThread;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return sActivityThread;
    }

    public void setInstrumentation(Object activityThread, Instrumentation instrumentation) {
        try {
            ReflectUtil.setField(activityThread.getClass(), activityThread, "mInstrumentation", instrumentation);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @WorkerThread
    public static Resources createResources(Context context, File apk) {
        Resources hostResources = context.getResources();
        AssetManager assetManager = createAssetManager(apk);
        return new Resources(assetManager, hostResources.getDisplayMetrics(), hostResources.getConfiguration());
    }

    public static AssetManager createAssetManager(File apk) {
        try {
            AssetManager am = AssetManager.class.newInstance();
            ReflectUtil.invoke(AssetManager.class, am, "addAssetPath", apk.getAbsolutePath());
            return am;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static ClassLoader createClassLoader(Context context, File apk, ClassLoader parent) {
        File dexOutputDir = context.getDir(OPTIMIZE_DIR, Context.MODE_PRIVATE);
        String dexOutputPath = dexOutputDir.getAbsolutePath();
        File libsDir = context.getDir(NATIVE_DIR, Context.MODE_PRIVATE);
        return new DexClassLoader(apk.getAbsolutePath(), dexOutputPath, libsDir.getAbsolutePath(), parent);
    }

    public static int selectDefaultTheme(final int curTheme, final int targetSdkVersion) {
        return selectSystemTheme(curTheme, targetSdkVersion,
                android.R.style.Theme,
                android.R.style.Theme_Holo,
                android.R.style.Theme_DeviceDefault,
                android.R.style.Theme_DeviceDefault_Light_DarkActionBar);
    }

    private static int selectSystemTheme(final int curTheme, final int targetSdkVersion, final int orig, final int holo, final int dark, final int deviceDefault) {
        if (curTheme != 0) {
            return curTheme;
        }

        if (targetSdkVersion < 11 /* Build.VERSION_CODES.HONEYCOMB */) {
            return orig;
        }

        if (targetSdkVersion < 14 /* Build.VERSION_CODES.ICE_CREAM_SANDWICH */) {
            return holo;
        }

        if (targetSdkVersion < 24 /* Build.VERSION_CODES.N */) {
            return dark;
        }

        return deviceDefault;
    }
}
