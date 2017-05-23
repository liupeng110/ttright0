package com.andlp.ttright.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.andlib.lp.MainActivity;
import com.andlp.ttright.R;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

@ContentView(R.layout.activity_welcome)
public class Activity_Welcome extends Activity_Base {

    @ViewInject(R.id.welcome_tv) TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tv.setText("测试");

        x.task().postDelayed(new Runnable() {
            @Override public void run() {
                startActivity(new Intent(Activity_Welcome.this, Activity_Main.class));
                finish();
            }
        },2000);


    }
}
