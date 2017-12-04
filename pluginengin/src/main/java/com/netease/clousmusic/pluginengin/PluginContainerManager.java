package com.netease.clousmusic.pluginengin;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.content.res.TypedArray;

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
        stubActivity = String.format(STUB_ACTIVITY_STANDARD, LIBRARY_PACKAGE, STUB_ACTIVITY_STANDARD);
        switch (launchMode) {
            case ActivityInfo.LAUNCH_MULTIPLE: {
                stubActivity = String.format(STUB_ACTIVITY_STANDARD, LIBRARY_PACKAGE, STUB_ACTIVITY_STANDARD);
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
