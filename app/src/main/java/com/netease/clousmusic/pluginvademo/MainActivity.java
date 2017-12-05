package com.netease.clousmusic.pluginvademo;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.netease.clousmusic.pluginengin.PluginManager;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.install).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadPlugin();
            }
        });

        findViewById(R.id.open).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pkg = "com.netease.clousmusic.pluginapk";
                if (PluginManager.getInstance().getLoadedPlugin(pkg) == null) {
                    Toast.makeText(MainActivity.this, "plugin not loaded", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent();
                ComponentName componentName = new ComponentName(pkg, pkg + ".MainActivity");
                intent.setComponent(componentName);
                startActivity(intent);
            }
        });
    }

    private void loadPlugin() {
        PluginManager pluginManager = PluginManager.getInstance();
        File apk = new File(Environment.getExternalStorageDirectory(), "vatest.apk");
        if (apk.exists()) {
            try {
                pluginManager.loadPlugin(apk);
                Toast.makeText(MainActivity.this, "dfaf", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
