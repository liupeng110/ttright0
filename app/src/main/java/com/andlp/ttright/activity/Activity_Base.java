package com.andlp.ttright.activity;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.WindowManager;


import org.xutils.x;


public class Activity_Base extends FragmentActivity {
    public String tag = Activity_Base.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);//屏幕常亮
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);         //全屏
        x.view().inject(this);
    }


}
