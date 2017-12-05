package com.netease.clousmusic.pluginengin.hook;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.netease.clousmusic.pluginengin.LoadedPlugin;
import com.netease.clousmusic.pluginengin.PluginManager;
import com.netease.clousmusic.pluginengin.utils.ReflectUtil;

import static com.netease.clousmusic.pluginengin.hook.HookHelper.isIntentFromPlugin;

/**
 * Created by liyongan on 17/12/4.
 */

public class VAInstrumentation extends Instrumentation {
    private Instrumentation mBase;

    public VAInstrumentation(Instrumentation base) {
        mBase = base;
    }

    public ActivityResult execStartActivity(Context who, IBinder contextThread, IBinder token, Activity target,
            Intent intent, int requestCode, Bundle options) {
        PluginManager.getInstance().getComponentsHandler().markIntentIfNeeded(intent);
        Log.d("lya", "execStartActivity: ");
        return realExecStartActivity(who, contextThread, token, target, intent, requestCode, options);

    }

    private ActivityResult realExecStartActivity(Context who, IBinder contextThread, IBinder token, Activity target,
            Intent intent, int requestCode, Bundle options) {
        ActivityResult result = null;
        try {
            Class[] parameterTypes = {Context.class, IBinder.class, IBinder.class, Activity.class, Intent.class,
                    int.class, Bundle.class};
            result = (ActivityResult)ReflectUtil.invoke(Instrumentation.class, mBase,
                    "execStartActivity", parameterTypes,
                    who, contextThread, token, target, intent, requestCode, options);
        } catch (Exception e) {
            if (e.getCause() instanceof ActivityNotFoundException) {
                throw (ActivityNotFoundException) e.getCause();
            }
            e.printStackTrace();
        }

        return result;
    }

    @Override
    public Activity newActivity(ClassLoader cl, String className, Intent intent) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        if (isIntentFromPlugin(intent)) {
            String targetClassName = HookHelper.getTargetActivity(intent);
            String targetPackageName = HookHelper.getTargetPackageName(intent);
            LoadedPlugin plugin = PluginManager.getInstance().getLoadedPlugin(targetPackageName);
            Activity activity = mBase.newActivity(plugin.getClassLoader(), targetClassName, intent);
            activity.setIntent(intent);
            try {
                ReflectUtil.invoke(plugin.getClassLoader().loadClass("com.netease.clousmusic.pluginapk.MainActivity"),
                        activity, "initBaseContext",new Class[]{Context.class}, plugin.getPluginContext());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return activity;
        } else {
            return mBase.newActivity(cl, className, intent);
        }
    }

    @Override
    public void callActivityOnCreate(Activity activity, Bundle icicle) {
        final Intent intent = activity.getIntent();
        if (HookHelper.isIntentFromPlugin(intent)) {
            LoadedPlugin plugin = PluginManager.getInstance().getLoadedPlugin(HookHelper.getTargetPackageName(intent));
            // set screenOrientation
            ActivityInfo activityInfo = plugin.getActivityInfo(HookHelper.getTargetActivity(intent));
            if (activityInfo.screenOrientation != ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED) {
                activity.setRequestedOrientation(activityInfo.screenOrientation);
            }
            activity.setTheme(plugin.getThemeId());
        }
        mBase.callActivityOnCreate(activity, icicle);
    }
}
