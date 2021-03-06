package com.netease.clousmusic.pluginengin;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.content.res.TypedArray;

import com.netease.clousmusic.pluginengin.utils.Constants;

import java.util.HashMap;

/**
 * Created by liyongan on 17/12/4.
 */

public class PluginContainerManager {
    private static final String LIBRARY_PACKAGE = "com.netease.clousmusic.pluginengin";
    private static final String STUB_ACTIVITY_STANDARD = "%s.A$%d";
    private static final String STUB_ACTIVITY_SINGLETOP = "%s.B$%d";
    private static final String STUB_ACTIVITY_SINGLETASK = "%s.C$%d";
    private static final String STUB_ACTIVITY_SINGLEINSTANCE = "%s.D$%d";
    private int usedSingleTopStubActivity = 0;
    private int usedSingleTaskStubActivity = 0;
    private int usedSingleInstanceStubActivity = 0;
    private static final int MAX_COUNT_SINGLETOP = 8;
    private static final int MAX_COUNT_SINGLETASK = 8;
    private static final int MAX_COUNT_SINGLEINSTANCE = 8;

    private Context mContext;
    private HashMap<String, String> mCachedStubActivity = new HashMap<>();



    public PluginContainerManager(Context context) {
        mContext = context;

    }

    public void markIntentIfNeeded(Intent intent) {
        if (intent.getComponent() == null) {
            return;
        }

        String targetPackageName = intent.getComponent().getPackageName();
        String targetClassName = intent.getComponent().getClassName();
        // search map and return specific launchmode stub activity
        if (!targetPackageName.equals(mContext.getPackageName()) && PluginManager.getInstance().getLoadedPlugin(targetPackageName) != null) {
            intent.putExtra(Constants.KEY_IS_PLUGIN, true);
            intent.putExtra(Constants.KEY_TARGET_PACKAGE, targetPackageName);
            intent.putExtra(Constants.KEY_TARGET_ACTIVITY, targetClassName);
            dispatchStubActivity(intent);
        }
    }

    private void dispatchStubActivity(Intent intent) {
        String targetClassName = intent.getComponent().getClassName();
        LoadedPlugin loadedPlugin = PluginManager.getInstance().getLoadedPlugin(intent.getComponent().getPackageName());
        ActivityInfo info = loadedPlugin.getActivityInfo(targetClassName);
        if (info == null) {
            throw new RuntimeException("can not find activity");
        }
        int launchMode = info.launchMode;
        Resources.Theme themeObj = loadedPlugin.getResources().newTheme();
        themeObj.applyStyle(info.theme, true);
        String stubActivity = getStubActivity(targetClassName, launchMode, themeObj);
        intent.setClassName(mContext, stubActivity);
    }

    private String getStubActivity(String className, int launchMode, Resources.Theme theme) {
        String stubActivity= mCachedStubActivity.get(className);
        if (stubActivity != null) {
            return stubActivity;
        }
        TypedArray array = theme.obtainStyledAttributes(new int[]{
                android.R.attr.windowIsTranslucent,
                android.R.attr.windowBackground
        });
        boolean windowIsTranslucent = array.getBoolean(0, false);
        array.recycle();
        stubActivity = String.format(STUB_ACTIVITY_STANDARD, LIBRARY_PACKAGE, 1);
        switch (launchMode) {
            case ActivityInfo.LAUNCH_MULTIPLE: {
                stubActivity = String.format(STUB_ACTIVITY_STANDARD, LIBRARY_PACKAGE, 1);
                if (windowIsTranslucent) {
                    stubActivity = String.format(STUB_ACTIVITY_STANDARD, LIBRARY_PACKAGE, 2);
                }
                break;
            }
            case ActivityInfo.LAUNCH_SINGLE_TOP: {
                usedSingleTopStubActivity = usedSingleTopStubActivity % MAX_COUNT_SINGLETOP + 1;
                stubActivity = String.format(STUB_ACTIVITY_SINGLETOP, LIBRARY_PACKAGE, usedSingleTopStubActivity);
                break;
            }
            case ActivityInfo.LAUNCH_SINGLE_TASK: {
                usedSingleTaskStubActivity = usedSingleTaskStubActivity % MAX_COUNT_SINGLETASK + 1;
                stubActivity = String.format(STUB_ACTIVITY_SINGLETASK, LIBRARY_PACKAGE, usedSingleTaskStubActivity);
                break;
            }
            case ActivityInfo.LAUNCH_SINGLE_INSTANCE: {
                usedSingleInstanceStubActivity = usedSingleInstanceStubActivity % MAX_COUNT_SINGLEINSTANCE + 1;
                stubActivity = String.format(STUB_ACTIVITY_SINGLEINSTANCE, LIBRARY_PACKAGE, usedSingleInstanceStubActivity);
                break;
            }
            default:break;
        }
        mCachedStubActivity.put(className, stubActivity);
        return stubActivity;
    }
}
