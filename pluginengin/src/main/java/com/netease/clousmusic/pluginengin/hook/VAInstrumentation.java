package com.netease.clousmusic.pluginengin.hook;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.netease.clousmusic.pluginengin.utils.ReflectUtil;

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
//        mPluginManager.getComponentsHandler().transformIntentToExplicitAsNeeded(intent);
//         null component is an implicitly intent
//        if (intent.getComponent() != null) {
//            Log.i(TAG, String.format("execStartActivity[%s : %s]", intent.getComponent().getPackageName(),
//                    intent.getComponent().getClassName()));
//             resolve intent with Stub Activity if needed
//            this.mPluginManager.getComponentsHandler().markIntentIfNeeded(intent);
//        }
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
        try {
            cl.loadClass(className);
        } catch (ClassNotFoundException e) {
//            LoadedPlugin plugin = this.mPluginManager.getLoadedPlugin(intent);
//            String targetClassName = PluginUtil.getTargetActivity(intent);
//            if (targetClassName != null) {
//                Activity activity = mBase.newActivity(plugin.getClassLoader(), targetClassName, intent);
//                activity.setIntent(intent);
//                try {
//                    // for 4.1+
//                    ReflectUtil.setField(ContextThemeWrapper.class, activity, "mResources", plugin.getResources());
//                } catch (Exception ignored) {
//                    // ignored.
//                }
//
//                return activity;
//            }
        }
        Log.d("lya", "newActivity");
        return mBase.newActivity(cl, className, intent);
    }

    @Override
    public void callActivityOnCreate(Activity activity, Bundle icicle) {
        final Intent intent = activity.getIntent();
//        if (PluginUtil.isIntentFromPlugin(intent)) {
//            Context base = activity.getBaseContext();
//            try {
//                LoadedPlugin plugin = this.mPluginManager.getLoadedPlugin(intent);
//                ReflectUtil.setField(base.getClass(), base, "mResources", plugin.getResources());
//                ReflectUtil.setField(ContextWrapper.class, activity, "mBase", plugin.getPluginContext());
//                ReflectUtil.setField(Activity.class, activity, "mApplication", plugin.getApplication());
//                ReflectUtil.setFieldNoException(ContextThemeWrapper.class, activity, "mBase", plugin.getPluginContext());
//
//                // set screenOrientation
//                ActivityInfo activityInfo = plugin.getActivityInfo(PluginUtil.getComponent(intent));
//                if (activityInfo.screenOrientation != ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED) {
//                    activity.setRequestedOrientation(activityInfo.screenOrientation);
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//        }
        Log.d("lya", "callActivityOnCreate: ");
        mBase.callActivityOnCreate(activity, icicle);
    }
}
