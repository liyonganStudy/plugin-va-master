package com.netease.clousmusic.pluginvademo;

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

    private void simulateInstallExternalPlugin(String apkName) {
//        String demo3Apk= "demo3.apk";

        // 文件是否已经存在？直接删除重来
//        String pluginFilePath = getFilesDir().getAbsolutePath() + File.separator + apkName;
//        File pluginFile = new File(pluginFilePath);
//        if (pluginFile.exists()) {
//            FileUtils.deleteQuietly(pluginFile);
//        }
//        copyAssetsFileToAppFiles(apkName, apkName);
//        if (pluginFile.exists()) {
//            PluginEngine.getInstance().install(pluginFilePath);
//        }
    }
}
