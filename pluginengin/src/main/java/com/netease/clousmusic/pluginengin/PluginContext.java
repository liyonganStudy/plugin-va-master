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
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.ContextThemeWrapper;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewStub;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by renyugang on 16/8/12.
 */
class PluginContext extends ContextThemeWrapper {

    private final LoadedPlugin mPlugin;
    private HashSet<String> mIgnores = new HashSet<>();
    private LayoutInflater mInflater;
    HashMap<String, Constructor<?>> mConstructors = new HashMap<>();
    LayoutInflater.Factory mFactory = new LayoutInflater.Factory() {

        @Override
        public View onCreateView(String name, Context context, AttributeSet attrs) {
            return handleCreateView(name, context, attrs);
        }
    };

    public PluginContext(LoadedPlugin plugin, Context context) {
        super(context, android.R.style.Theme);
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
        } else if (LAYOUT_INFLATER_SERVICE.equals(name)) {
            if (mInflater == null) {
                LayoutInflater inflater = (LayoutInflater) super.getSystemService(name);
                // 新建一个，设置其工厂
                mInflater = inflater.cloneInContext(this);
                mInflater.setFactory(mFactory);
                // 再新建一个，后续可再次设置工厂
                mInflater = mInflater.cloneInContext(this);
            }
            return mInflater;
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

//    @Override
//    public Resources.Theme getTheme() {
//        return mPlugin.getTheme();
//    }

//    @Override
//    public void startActivity(Intent intent) {
//        ComponentsHandler componentsHandler = mPlugin.getPluginManager().getComponentsHandler();
//        componentsHandler.transformIntentToExplicitAsNeeded(intent);
//        super.startActivity(intent);
//    }

    private final View handleCreateView(String name, Context context, AttributeSet attrs) {
        // 忽略表命中，返回
        if (mIgnores.contains(name)) {
            // 只有开启“详细日志”才会输出，防止“刷屏”现象
            return null;
        }

        // 构造器缓存
        Constructor<?> construct = mConstructors.get(name);

        // 缓存失败
        if (construct == null) {
            // 找类
            Class<?> c = null;
            boolean found = false;
            try {
                c = mPlugin.getClassLoader().loadClass(name);
                if (c == null) {
                    // 没找到，不管
                } else if (c == ViewStub.class) {
                    // 系统特殊类，不管
                } else if (c.getClassLoader() != mPlugin.getClassLoader()) {
                    // 不是插件类，不管
                } else {
                    found = true;
                }
            } catch (ClassNotFoundException e) {
                // 失败，不管
            }
            if (!found) {
                // 只有开启“详细日志”才会输出，防止“刷屏”现象
                mIgnores.add(name);
                return null;
            }
            // 找构造器
            try {
                construct = c.getConstructor(Context.class, AttributeSet.class);
                mConstructors.put(name, construct);
            } catch (Exception e) {
                throw new InflateException(attrs.getPositionDescription() + ": Error inflating mobilesafe class " + name, e);
            }
        }

        // 构造
        try {
            // 只有开启“详细日志”才会输出，防止“刷屏”现象
            return (View) construct.newInstance(context, attrs);
        } catch (Exception e) {
            throw new InflateException(attrs.getPositionDescription() + ": Error inflating mobilesafe class " + name, e);
        }
    }

}
