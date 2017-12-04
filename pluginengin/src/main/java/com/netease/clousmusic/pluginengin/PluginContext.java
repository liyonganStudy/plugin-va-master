/*
 * Copyright (C) 2017 Beijing Didi Infinity Technology and Development Co.,Ltd. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.netease.clousmusic.pluginengin;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.AssetManager;
import android.content.res.Resources;

/**
 * Created by renyugang on 16/8/12.
 */
class PluginContext extends ContextWrapper {

    private final LoadedPlugin mPlugin;

    public PluginContext(LoadedPlugin plugin, Context context) {
        super(context);
        mPlugin = plugin;
    }

//    @Override
//    public Context getApplicationContext() {
//        return this.mPlugin.getApplication();
//    }

    private Context getHostContext() {
        return getBaseContext();
    }

    @Override
    public ClassLoader getClassLoader() {
        return mPlugin.getClassLoader();
    }

//    @Override
//    public PackageManager getPackageManager() {
//        return this.mPlugin.getPackageManager();
//    }

    @Override
    public Object getSystemService(String name) {
        // intercept CLIPBOARD_SERVICE,NOTIFICATION_SERVICE
        if (name.equals(Context.CLIPBOARD_SERVICE)) {
            return getHostContext().getSystemService(name);
        } else if (name.equals(Context.NOTIFICATION_SERVICE)) {
            return getHostContext().getSystemService(name);
        }

        return super.getSystemService(name);
    }

    @Override
    public Resources getResources() {
        return mPlugin.getResources();
    }

    @Override
    public AssetManager getAssets() {
        return mPlugin.getAssets();
    }

    @Override
    public Resources.Theme getTheme() {
        return mPlugin.getTheme();
    }

//    @Override
//    public void startActivity(Intent intent) {
//        ComponentsHandler componentsHandler = mPlugin.getPluginManager().getComponentsHandler();
//        componentsHandler.transformIntentToExplicitAsNeeded(intent);
//        super.startActivity(intent);
//    }

}