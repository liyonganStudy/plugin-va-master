package com.netease.clousmusic.pluginengin;

import android.app.Application;
import android.app.Instrumentation;
import android.content.Context;

import com.netease.clousmusic.pluginengin.hook.HookHelper;
import com.netease.clousmusic.pluginengin.hook.VAInstrumentation;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by liyongan on 17/12/4.
 */

public class PluginManager {
    private static PluginManager sInstance = null;
    private Context mContext;
    private PluginContainerManager mComponentsHandler;
    private HookHelper mHookHelper;
    private Map<String, LoadedPlugin> mPlugins = new ConcurrentHashMap<>();

    public static PluginManager getInstance() {
        if (sInstance == null) {
            synchronized (PluginManager.class) {
                if (sInstance == null)
                    sInstance = new PluginManager();
            }
        }

        return sInstance;
    }

    private PluginManager() {}

    public void attachContext(Application context) {
        Context app = context.getApplicationContext();
        if (app == null) {
            this.mContext = context;
        } else {
            this.mContext = ((Application) app).getBaseContext();
        }
        init();
        hookInstrumentation();
    }

    private void init() {
        mComponentsHandler = new PluginContainerManager(mContext);
        mHookHelper = new HookHelper();
    }

    private void hookInstrumentation() {
        try {
            Instrumentation baseInstrumentation = mHookHelper.getInstrumentation(mContext);
            if (baseInstrumentation.getClass().getName().contains("lbe")) {
                // reject executing in paralell space, for example, lbe.
                System.exit(0);
            }
            final VAInstrumentation instrumentation = new VAInstrumentation(baseInstrumentation);
            Object activityThread = mHookHelper.getActivityThread(mContext);
            mHookHelper.setInstrumentation(activityThread, instrumentation);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadPlugin(File apk) throws Exception {
        LoadedPlugin plugin = LoadedPlugin.create(mContext, apk);
        mPlugins.put(plugin.getPackageName(), plugin);
    }

    public PluginContainerManager getComponentsHandler() {
        return mComponentsHandler;
    }

    public LoadedPlugin getLoadedPlugin(String packageName) {
        return mPlugins.get(packageName);
    }
}
