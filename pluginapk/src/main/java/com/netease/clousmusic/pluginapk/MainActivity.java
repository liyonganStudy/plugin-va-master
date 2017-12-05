package com.netease.clousmusic.pluginapk;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private Context mBase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        if (mBase != null) {
            super.attachBaseContext(mBase);
        } else {
            super.attachBaseContext(newBase);
        }
    }

    public void initBaseContext(Context base) {
        mBase = base;
    }
}
